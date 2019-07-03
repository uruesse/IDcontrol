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
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import net.ruesse.idc.control.ApplicationControlBean;
import static net.ruesse.idc.control.FileService.getDocumentsDir;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Named
@SessionScoped
//@RequestScoped -- das funktioniert nicht!!
public class DocumentView implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(DocumentView.class.getName());
    private static final long serialVersionUID = 1L;

    int screenhight;
    boolean printeroutput = false;
    private static String fileName = "";

    /**
     *
     * @return
     */
    public int getScreenhight() {
        LOGGER.info("Screenhight: N/A");
        int ret;
        if (ApplicationControlBean.getLoginMgl() == null) {
            if (screenhight < 300) {
                ret = 1024;
            } else {
                ret = screenhight;
            }
        } else if (ApplicationControlBean.getLoginMgl().getPdfHight() < 300) {
            ret = 1024;
        } else {
            ret = ApplicationControlBean.getLoginMgl().getPdfHight();
        }
        LOGGER.info("Screenhight: " + ret);
        return ret;
    }

    /**
     *
     * @param screenhight
     */
    public void setScreenhight(int screenhight) {
        LOGGER.info("Screenhight: " + screenhight);
        if (ApplicationControlBean.getLoginMgl() == null) {
            this.screenhight = screenhight;
        } else {
            ApplicationControlBean.getLoginMgl().setPdfHight(screenhight);
        }
    }

    /**
     *
     * @return
     */
    public String getParam() {
        String document = null;
        String printname = null;

        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();

        if (paramMap != null) {
            document = paramMap.get("document");
            printname = paramMap.get("printname");
            if (document == null || document.isEmpty()) {
                if (printname == null || printname.isEmpty()) {
                } else {
                    printeroutput = true;
                }
            } else {
                printeroutput = false;
            }
        }
        if ((document == null || document.isEmpty()) && (printname == null || printname.isEmpty())) {
            LOGGER.log(Level.INFO, "Kein Dokument gefunden printeroutput = " + printeroutput);
            return "";
        } else {
            LOGGER.log(Level.INFO, printeroutput ? "printer={0}" : "document={0}", printeroutput ? printname : document);
            //PrimeFaces.current().executeScript("location.reload();");
            return printeroutput ? printname : document;
        }
    }

    /**
     *
     * @return
     */
    public static String getFileName() {
        return fileName;
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        DocumentView.fileName = fileName;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        if (printeroutput) {
            return "Ausgabeseite von " + getFileName() + ".pdf";
        } else {
            return "Anzeige von: " + getFileNameFormatted();
        }

    }

    /**
     * Caching ist nicht erlaubt bei Anzeige von Druckausgaben ansonsten kann es
     * sein, dass gecachte Ausgaben angezeigt werden
     *
     * Caching macht im Chrome-Browser grundsätzlich Probleme, deshalb aktuell
     * abgeschaltet
     *
     * @return
     */
    public boolean getCaching() {
        return false;
        //return !printeroutput;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        String fn = getParam();

        if (!fn.isEmpty()) {
            setFileName(fn);
        }

        if (printeroutput) {
            return "Drukerausgabe";
        } else {
            return "Dokumentation";
        }
    }

    /**
     *
     * @return
     */
    public StreamedContent getDatei() {
        String type = "application/pdf";
        Path path = getDocumentsDir();
        String fn = getParam();

        if (!fn.isEmpty()) {
            setFileName(fn);
        }

        File file;
        FileInputStream fis = null;

        if (printeroutput) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Path pdfFilePath = Paths.get(externalContext.getRealPath(""), "resources", "files", "pdf", getFileName() + ".pdf");
            file = pdfFilePath.toFile();
            fn = getFileName() + ".pdf";
        } else {
            file = path.resolve(fileName).toFile();
        }
        LOGGER.log(Level.INFO, "File={0}", file.toString());
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        if (fis != null) {
            LOGGER.log(Level.INFO, "FileInputStream ={0}", fis.toString());
            return new DefaultStreamedContent(fis, type, fn);
        } else {
            LOGGER.log(Level.INFO, "Hier sollte ich eigentlich nicht sein");
            return new DefaultStreamedContent();
        }
    }
}
