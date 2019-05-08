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
package net.ruesse.idc.control;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import static net.ruesse.idc.control.FileService.getTempDir;
import net.ruesse.idc.database.sql.SqlSupport;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
public class FileUploadView {

    private static final Logger LOGGER = Logger.getLogger(FileUploadView.class.getName());

    private UploadedFile file;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void upload() {
        if (file != null) {
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {

        String fn=event.getFile().getFileName();
        Path pDest = getTempDir().resolve(fn);
        
        try {
            copyFile(pDest, event.getFile().getInputstream());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        SqlSupport sp = new SqlSupport();

        sp.importSchema("idcremote", pDest);

        FacesMessage msg = new FacesMessage("Fertig", "Die Datei " + event.getFile().getFileName() + " wurde erfolgreich hochgeladen und in die Datenbank importiert.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void copyFile(Path pDest, InputStream in) {

        OutputStream out = null;
        try {
            // write the inputStream to a FileOutputStream
            out = new FileOutputStream(pDest.toFile());
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            LOGGER.info("New file created!");
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

    }
}
