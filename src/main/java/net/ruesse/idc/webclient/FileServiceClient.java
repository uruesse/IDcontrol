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
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import static net.ruesse.idc.control.FileService.getImportsDir;
import net.ruesse.idc.database.persistence.service.VereinService;
import net.ruesse.idc.database.persistence.service.WebDavService;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ViewScoped
public class FileServiceClient {

    private static final Logger LOGGER = Logger.getLogger(FileServiceClient.class.getName());

    String WebserviceURL = "";
    String WebserviceUser = "";
    String WebservicePassword = "";
    boolean WebserviceAvailable = false;

    /**
     *
     * @param bytes
     * @return
     */
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
     * @param bytes
     * @return
     */
    private String calcChecksumStr(byte[] bytes) {
        return DatatypeConverter.printHexBinary(calcChecksum(bytes));
    }

    //@Inject
    //WebDavService wds;
    /**
     *
     */
    public FileServiceClient() {
        WebDavService wds = new WebDavService();
        if (wds.isWebserviceAvailable()) {
            WebserviceURL = wds.getWebserviceURL();
            WebserviceUser = wds.getWebserviceUser();
            WebservicePassword = wds.getWebservicePassword();
            WebserviceAvailable = true;
        }
        /*
        VereinService vs = new VereinService();
        if (vs != null) {
            WebserviceURL = vs.getAktVerein().getUridav();
        } 
        WebserviceUser = ApplicationControlBean.getLoginMgl().getWebDavUser();
        WebservicePassword = ApplicationControlBean.getLoginMgl().getWebDavPassword();
         */
    }

    /**
     *
     * @param idcFile
     * @return
     */
    public Path downloadFile(String idcFile) {
        if (!WebserviceAvailable) {
            LOGGER.log(Level.INFO, "Webservice nicht verfügbar");
            return null;
        }
        FileServiceWebDav service = new FileServiceWebDav(WebserviceURL, WebserviceUser, WebservicePassword);

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
                service.shutdown();
                return getImportsDir().resolve(idcFile);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        service.shutdown();
        return null;
    }

    /**
     *
     * @param idcFile
     */
    public void uploadFile(String idcFile) {
        if (!WebserviceAvailable) {
            LOGGER.log(Level.INFO, "Webservice nicht verfügbar");
            return;
        }
        FileServiceWebDav service = new FileServiceWebDav(WebserviceURL, WebserviceUser, WebservicePassword);

        String strChecksumBytes;

        File file = new File(idcFile);

        if (service.exists(file.getName())) {
            LOGGER.log(Level.INFO, "File existiert bereits: {0} - Datei wurde nicht hochgeladen.", new Object[]{idcFile});
        } else {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] imageBytes;
                try (BufferedInputStream inputStream = new BufferedInputStream(fis)) {
                    imageBytes = new byte[(int) file.length()];
                    inputStream.read(imageBytes);
                }

                strChecksumBytes = new String(service.upload(file.getName() + ".md5", calcChecksumStr(imageBytes).getBytes()));
                String str2 = calcChecksumStr(service.upload(file.getName(), imageBytes));

                if (strChecksumBytes.equals(str2)) {
                    LOGGER.log(Level.INFO, "File uploaded: {0} MD5: {1}", new Object[]{idcFile, strChecksumBytes});
                } else {
                    LOGGER.log(Level.INFO, "File upload fehlgeschlagen: {0} MD5: {1} / {2}", new Object[]{idcFile, strChecksumBytes, str2});
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        service.shutdown();
    }

    /**
     *
     * @param idcFile
     */
    public void deleteFile(String idcFile) {
        if (!WebserviceAvailable) {
            LOGGER.log(Level.INFO, "Webservice nicht verfügbar");
            return;
        }
        FileServiceWebDav service = new FileServiceWebDav(WebserviceURL, WebserviceUser, WebservicePassword);

        File file = new File(idcFile);

        service.purge(file.getName() + ".md5");
        service.purge(file.getName());
        service.shutdown();

    }

    /**
     *
     *
     * @return
     */
    public ArrayList<String> listFiles() {
        if (!WebserviceAvailable) {
            LOGGER.log(Level.INFO, "Webservice nicht verfügbar");
            return null;
        }
        ArrayList<String> files;
        FileServiceWebDav service = new FileServiceWebDav(WebserviceURL, WebserviceUser, WebservicePassword);
        files = service.list(".IDC");
        files.sort(Collections.reverseOrder());
        service.shutdown();

        for (ListIterator li = files.listIterator(0); li.hasNext();) {
            LOGGER.info(li.next().toString());
        }
        return files;
    }

    /**
     *
     * @return @throws Exception
     */
    public String getLastBackup() throws Exception {
        String strResult = "";
        if (WebserviceAvailable) {

            VereinService vereinService = new VereinService();
            String vereinID = vereinService.getVereinId();
            //String vereinID = "0920000";

            ArrayList<String> backups = listFiles();
            for (ListIterator li = backups.listIterator(0); li.hasNext();) {
                String tmp = li.next().toString();
                LOGGER.info(tmp);
                if (tmp.startsWith(vereinID)) {
                    strResult = tmp;
                    break;
                }

            }

            LOGGER.log(Level.INFO, "Letzter Backup: {0}", strResult);
        } else {
            LOGGER.log(Level.INFO, "Webservice nicht verfügbar");
        }

        // TODO Das ist ein Provisorium -- eigene FileService Exceptions definieren
        if (strResult.isEmpty()) {
            throw new Exception();
        }
        return strResult;
    }

    /**
     *
     * @return
     */
    public String getWebserviceUser() {
        return WebserviceUser;
    }
    
    

}
