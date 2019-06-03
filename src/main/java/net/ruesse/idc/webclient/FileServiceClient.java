/*
 * Copyright 2019 Ulrich Rüße <ulrich@ruesse.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ruesse.idc.webclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import static net.ruesse.idc.control.FileService.getImportsDir;
import net.ruesse.idc.control.VereinService;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class FileServiceClient {

    private static final Logger LOGGER = Logger.getLogger(FileServiceClient.class.getName());

    private byte[] calcChecksum(byte[] bytes) {
        byte[] checksum = null;
        try {
            checksum = MessageDigest.getInstance("MD5").digest(bytes);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return checksum;
    }

    /**
     *
     * @param idcFile
     * @return
     */
    public Path downloadFile(String idcFile) {
        FileServiceImplService client = new FileServiceImplService();
        FileServiceImpl service = client.getFileServiceImplPort();

        File filePath = getImportsDir().resolve(idcFile).toFile();
        byte[] checksumBytes = service.download(idcFile + ".md5");
        byte[] fileBytes = service.download(idcFile);

        byte[] checksumFile = calcChecksum(fileBytes);
        LOGGER.log(Level.INFO, "Download: {0} MD5: {1} / {2}", new Object[]{idcFile, new String(checksumBytes), DatatypeConverter.printHexBinary(checksumFile)});

        if (DatatypeConverter.printHexBinary(checksumFile).equalsIgnoreCase(new String(checksumBytes))) {
            try {
                FileOutputStream fos = new FileOutputStream(filePath);
                try (BufferedOutputStream outputStream = new BufferedOutputStream(fos)) {
                    outputStream.write(fileBytes);
                }
                LOGGER.log(Level.INFO, "File downloaded: {0} ", filePath);
                return getImportsDir().resolve(idcFile);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     *
     * @param idcFile
     */
    public void uploadFile(String idcFile) {
        FileServiceImplService client = new FileServiceImplService();
        FileServiceImpl service = client.getFileServiceImplPort();

        byte[] checksumBytes;

        File file = new File(idcFile);

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] imageBytes;
            try (BufferedInputStream inputStream = new BufferedInputStream(fis)) {
                imageBytes = new byte[(int) file.length()];
                inputStream.read(imageBytes);
            }
            
            checksumBytes = calcChecksum(imageBytes);
            String str1 = DatatypeConverter.printHexBinary(service.upload(file.getName() + ".md5", checksumBytes));
            String str2 = DatatypeConverter.printHexBinary(service.upload(file.getName(), imageBytes));

            if (str1.equals(str2)) {
                LOGGER.log(Level.INFO, "File uploaded: {0} MD5: {1}", new Object[]{idcFile, DatatypeConverter.printHexBinary(checksumBytes)});
            } else { 
                LOGGER.log(Level.INFO, "File upload fehlgeschlagen: {0} MD5: {1} / {2}", new Object[]{idcFile, str1, str2});
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    public String getLastBackup() throws Exception {
        FileServiceImplService client = new FileServiceImplService();
        FileServiceImpl service = client.getFileServiceImplPort();

        VereinService vereinService = new VereinService();

        byte[] result;
        result = service.lastbackup(vereinService.getVereinId());
        String strResult = new String(result);
        LOGGER.log(Level.INFO, "Letzter Backup: {0}", strResult);

        // TODO Das ist ein Provisorium -- eigene FileService Exceptions definieren
        if (strResult.isEmpty()) throw new Exception();
        return strResult;
    }
}
