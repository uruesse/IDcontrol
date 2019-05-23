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
package net.ruesse.idc.error;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import static net.ruesse.idc.control.FileService.getLoggingDir;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
@RequestScoped
public class ErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());

    public String getStatusCode() {
        String val = String.valueOf((Integer) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.status_code"));
        return val;
    }

    public String getMessage() {
        String val = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.message");
        return val;
    }

    public String getExceptionType() {
        String val;
        Object ex = FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.exception_type");
        if (ex != null) {
            val = ex.toString();
            return val;
        } else {
            return "";
        }
    }

    public String getException() {
        String val;
        Object ex = FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.exception");
        if (ex != null) {
            val = (String) ((Exception) ex).toString();
        } else {
            val = "";
        }
        return val;
    }

    public String getRequestURI() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.request_uri");
    }

    public String getServletName() {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.servlet_name");
    }

    /**
     * Erzeugt eine NullPointer-Exception zum Testen eines 500er Fehlers
     */
    public void error500() {
        throw new NullPointerException("künstlich erzeugter Fehler zum Testen des Layouts");
    }

    public List<String> fileContent = new ArrayList<>();

    public List<String> getFileContent() {
        return fileContent;
    }

    public void setFileContent(List<String> fileContent) {
        this.fileContent = fileContent;
    }


    public void loadFile(String path) throws ParseException {
        //String path = "/Users/ulrich/Documents/Entwicklung/apache-tomcat-9.0.16/logs/catalina.out";
        LOGGER.info("Lese Logdatei ein: " + path);
        fileContent.clear();
        //fileContent = new ArrayList<String>();

        File f = new File(path);
        LOGGER.info("Anzahl Bytes: " + f.length());
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(f));
            String s = "";
            while ((s = r.readLine()) != null) {
                fileContent.add(s);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        //filteredFileContent.addAll(fileContent);
        LOGGER.info("Anzahl Zeilen: " + fileContent.size());
    }

    
    String logFile;

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
        try {
            loadFile (logFile);
        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    

    public List<String> getLogFileList() {
        List<String> result = null;

        try (Stream<Path> walk = Files.walk(getLoggingDir())) {

            result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            //result.forEach(System.out::println);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }
}
