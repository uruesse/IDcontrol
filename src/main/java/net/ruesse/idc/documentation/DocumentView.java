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
package net.ruesse.idc.documentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import static net.ruesse.idc.control.FileService.getDocumentsDir;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@SessionScoped
@ManagedBean
public class DocumentView {

    private final static Logger LOGGER = Logger.getLogger(DocumentView.class.getName());


    public String getParam() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
        if (paramMap != null) {
            String document = paramMap.get("document");
            if (document == null || document.isEmpty()) {
            } else {
                LOGGER.log(Level.INFO, "document={0}", document);
                return document;
            }
        }
        LOGGER.log(Level.INFO, "Kein Dokument gefunden");
        return "";
    }

    private static String fileName = "";

    public static String getFileName() {
        return fileName;
    }

    public String getFileNameFormatted() {
        LOGGER.log(Level.INFO, "document1={0}", fileName);
        String fn = getParam();
        if (!fn.isEmpty()) {
            setFileName(fn);
        }

        if (!fileName.isEmpty()) {
            LOGGER.log(Level.INFO, "document2={0}", fileName);
            fn = fileName.replace("_", " ");
            fn = fn.replace(".pdf", "");
            return fn;
        } else {
            return "";
        }
    }

    public void setFileName(String fileName) {
        DocumentView.fileName = fileName;
    }

    public StreamedContent getDatei() {
        String type = "application/pdf";
        Path path = getDocumentsDir();
        String fn = getParam();

        if (!fn.isEmpty()) {
            setFileName(fn);
        }

        File file = path.resolve(fileName).toFile();
        LOGGER.log(Level.INFO, "document={0}", file.toString());
        try {
            FileInputStream fis = new FileInputStream(file);
            return new DefaultStreamedContent(fis, type, fn);

        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        LOGGER.log(Level.INFO, "Hier sollte ich eigentlich nicht sein");
        return new DefaultStreamedContent();
    }
}
