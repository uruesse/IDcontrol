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

import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import static net.ruesse.idc.control.FileService.getDatabaseBaseDir;
import static net.ruesse.idc.control.FileService.getLogoDir;
import static net.ruesse.idc.control.FileService.getWorkingDir;
import net.ruesse.idc.database.persistence.service.PersonExt;
import net.ruesse.idc.database.sql.SqlSupport;
import net.ruesse.idc.report.PrintSupport;
import static net.ruesse.idc.report.PrintSupport.availablePrinters;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
    static int screenResolution = 0;
    static int AnzahlDrucke = 1;

    private List<String> printers;

    public ApplicationControlBean() {

        LOGGER.setLevel(Level.INFO);
        LOGGER.fine("aufgerufen");
        isDemo = false;
        isPDF = false;

        screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
        if (screenResolution == 0) {
            LOGGER.info("ScreenResolution = nicht angeschlossen");
            isDruckerdialog = false;
        } else {
            isDruckerdialog = true;
            LOGGER.info("ScreenResolution = " + screenResolution);
        }

        isDevelopment = false;
        kartendrucker = "PDF";

        //options
        printers = availablePrinters();
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

    public boolean isValidDruckerdialog() {
        return screenResolution == 0;
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

    public String getKartendrucker() {
        LOGGER.info(kartendrucker);
        return kartendrucker;
    }

    public static String getStaticKartendrucker() {
        LOGGER.info(kartendrucker);
        return kartendrucker;
    }

    public void setKartendrucker(String kartendrucker) {
        LOGGER.info(kartendrucker);
        ApplicationControlBean.kartendrucker = kartendrucker;
    }

    public int getAnzahlDrucke() {
        return AnzahlDrucke;
    }

    public void setAnzahlDrucke(int AnzahlDrucke) {
        ApplicationControlBean.AnzahlDrucke = AnzahlDrucke;
    }

    public List<String> getPrinters() {
        return printers;
    }

    public void setPrinters(List<String> printers) {
        this.printers = printers;
    }
    
     public String getBuildInfo() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            InputStream resourceAsStream = externalContext.getResourceAsStream("/META-INF/maven/net.ruesse/IDControl/pom.xml");
            if (resourceAsStream != null) {
                model = reader.read(new InputStreamReader(resourceAsStream));
            } else {
                // In der Entwicklungsumgebung:
                model = reader.read(new FileReader("/Users/ulrich/Documents/Entwicklung/IDControl/pom.xml"));
                LOGGER.log(Level.INFO, "POM: Resource nicht gefunden -- dies ist vermutlich die Entwicklungsumgebung");
            }

            if (model != null) {
                LOGGER.log(Level.INFO, "POM-ID: " + model.getId());
                LOGGER.log(Level.INFO, "POM-Build: " + model.getBuild());
                LOGGER.log(Level.INFO, "POM-Version: " + model.getVersion());
                return model.getId();
            } else {
                LOGGER.log(Level.INFO, "POM: Resource nicht gefunden" );
            }

        } catch (IOException | XmlPullParserException iOException) {
            LOGGER.log(Level.INFO, "POM: Resource nicht gefunden Fehler beim Einlesen ");
        }
        return "";
    }


    public String getWorkingDirInfo() {
        return getWorkingDir().toString();
    }

    public String getCATALINA_HOME() {
        return System.getProperty("catalina.base");
    }

    public void printActionRS() {
        String REPORT = "IDCard-back";

        if (AnzahlDrucke >= 1) {
            PrintSupport.printReport(REPORT, em, AnzahlDrucke, kartendrucker);
            addMessage("Fertig", "Druckauftrag erledigt");
        } else {
            addMessageFail("KEIN Druck!", "Mindestens eine Kopie erforderlich");
        }
    }

    public void printActionAL() {
        String REPORT = "Anwesenheitsliste";
        PrintSupport.printReport(REPORT, em, AnzahlDrucke, "PDF");
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

    public void addMessageFail(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
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
