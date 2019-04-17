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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import static net.ruesse.idc.control.FileService.getDatabaseBaseDir;
import static net.ruesse.idc.control.FileService.getLogoDir;
import net.ruesse.idc.database.persistence.service.PersonExt;
import net.ruesse.idc.database.sql.SqlSupport;
import net.ruesse.idc.report.PrintSupport;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ApplicationScoped
@ManagedBean
public class ApplicationControlBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationControlBean.class.getName());

    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

    static private boolean isDemo;
    static private boolean isPDF;
    static private boolean isDruckerdialog;
    static private boolean isDevelopment = false;
    static private String kartendrucker;
    static private Map persistenceParameters = null;
    static private PersonExt loginMgl;

    public ApplicationControlBean() {
        LOGGER.setLevel(Level.INFO);
        LOGGER.fine("aufgerufen");
        isDemo = false;
        isPDF = false;
        isDruckerdialog = true;
        isDevelopment = false;
        kartendrucker = "";
    }

    public boolean isIsDemo() {
        LOGGER.log(Level.FINE, "isDemo={0}", isDemo);
        return isDemo;
    }

    public void setIsDemo(boolean isDemo) {
        LOGGER.log(Level.FINE, "isDemo={0}", isDemo);
        ApplicationControlBean.isDemo = isDemo;
    }

    public boolean isIsPDF() {
        return isPDF;
    }

    static public boolean isPDF() {
        return isPDF;
    }

    public void setIsPDF(boolean isPDF) {
        ApplicationControlBean.isPDF = isPDF;
    }

    public boolean isIsDruckerdialog() {
        return isDruckerdialog;
    }

    public void setIsDruckerdialog(boolean isDruckerdialog) {
        ApplicationControlBean.isDruckerdialog = isDruckerdialog;
    }

    public static boolean isDruckerdialog() {
        return isDruckerdialog;
    }

    public static boolean isIsDevelopment() {
        return isDevelopment;
    }

    public static void setIsDevelopment(boolean isDevelopment) {
        ApplicationControlBean.isDevelopment = isDevelopment;
    }

    public static PersonExt getLoginMgl() {
        return loginMgl;
    }

    public static void setLoginMgl(PersonExt loginMgl) {
        ApplicationControlBean.loginMgl = loginMgl;
    }
    
    

    public static String getKartendrucker() {
        return kartendrucker;
    }

    public static void setKartendrucker(String kartendrucker) {
        ApplicationControlBean.kartendrucker = kartendrucker;
    }

    public void printActionRS() {
        String REPORT = "IDCard-back";

        PrintSupport.printReport(REPORT, em);

        addMessage("Fertig", "Druckauftrag erledigt");
    }

    public void printActionAL() {
        String REPORT = "Anwesenheitsliste";

        PrintSupport.generatePDFReport(REPORT, em);
        //return "/files/pdf/Anwesenheitsliste.pdf";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest origRequest = (HttpServletRequest) context.getExternalContext().getRequest();
        String contextPath = origRequest.getContextPath();

        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + "/faces/anwesenheitsliste.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ApplicationControlBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void resetScanLog() {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM IDCLOCAL.SCANLOG").executeUpdate();
        em.getTransaction().commit();
    }

    public void stageToProd() {
        SqlSupport sp = new SqlSupport();

        sp.executeSQLScript("createdb.sql");

        sp.executeSQLScript("stage2prod.sql");
        sp.executeSQLScript("fremdzahler.sql");
        sp.executeSQLScript("demo/entenhausen.sql");

        addMessage("Fertig", "Datenbank kopiert");
    }

    public void exportTables() {
        SqlSupport sp = new SqlSupport();

        String exportFile = sp.exportSchema("idcremote");

        addMessage("Fertig", "Datenbank in die Datei " + exportFile + "exportiert");
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public StreamedContent getLogoImage() {
        FacesContext context = FacesContext.getCurrentInstance();
        StreamedContent img = null;

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            img = new DefaultStreamedContent();
        } else {
            try {
                Path path = getLogoDir().resolve("Logo.png");
                byte[] data = Files.readAllBytes(path);
                img = new DefaultStreamedContent(new ByteArrayInputStream(data), "image/png", "Logo.png");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return (img);
    }

    public static Map getPersistenceParameters() {
        if (persistenceParameters == null) {
            // unbedingt hier aufrufen: in getDataBaseDir wird der Diemo-modus ermittelt
            String dbDir = getDatabaseBaseDir().resolve("IDControl").toString();
            persistenceParameters = new HashMap();
            if (isDevelopment) {
                LOGGER.info("javax.persistence.jdbc.url: jdbc:derby://localhost:1527/IDControl;create=true");
                persistenceParameters.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.ClientDriver");
                persistenceParameters.put("javax.persistence.jdbc.url", "jdbc:derby://localhost:1527/IDControl;create=true");
            } else {
                LOGGER.log(Level.INFO, "javax.persistence.jdbc.url:jdbc:derby:{0};create=true", dbDir);
                persistenceParameters.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
                persistenceParameters.put("javax.persistence.jdbc.url", "jdbc:derby:" + dbDir + ";create=true");
            }
            persistenceParameters.put("javax.persistence.jdbc.user", "idc");
            persistenceParameters.put("javax.persistence.jdbc.password", "idcpass");
        }
        return persistenceParameters;
    }

}
