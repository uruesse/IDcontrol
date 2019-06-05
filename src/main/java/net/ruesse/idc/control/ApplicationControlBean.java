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

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import net.ruesse.idc.webclient.FileServiceClient;
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
    static private boolean isBackupAvailable;
    static private boolean isDevelopment = false;
    static private String kartendrucker;
    static private Map persistenceParameters = null;
    static private PersonExt loginMgl;
    static boolean screenResolution = false;
    static int AnzahlDrucke = 1;

    private List<String> printers;

    public ApplicationControlBean() {

        LOGGER.setLevel(Level.INFO);
        LOGGER.fine("aufgerufen");
        isDemo = false;
        isPDF = false;
        isDruckerdialog = false;

        screenResolution = GraphicsEnvironment.isHeadless();
        if (screenResolution == false) {
            LOGGER.info("ScreenResolution = nicht angeschlossen");
        } else {
            LOGGER.info("ScreenResolution = " + screenResolution);
        }

        isDevelopment = false;
        isBackupAvailable = false;
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
        return screenResolution == false;
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

    public static boolean isIsBackupAvailable() {
        return isBackupAvailable;
    }

    public boolean isBackupAvailable() {
        return isBackupAvailable;
    }

    public static void setIsBackupAvailable(boolean isBackupAvailable) {
        ApplicationControlBean.isBackupAvailable = isBackupAvailable;
    }

    public static void setIsDevelopment(boolean isDevelopment) {
        ApplicationControlBean.isDevelopment = isDevelopment;
    }

    public static PersonExt getLoginMgl() {
        return loginMgl;
    }

    public static int getLoginMglUserRights() {
        if (loginMgl != null) {
            if (loginMgl.isMitarbeiter()) {
                return loginMgl.getUserstatus(); 
            } 
        } 
        return 0;
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
                LOGGER.log(Level.INFO, "POM-ID: {0}", model.getId());
                LOGGER.log(Level.INFO, "POM-Build: {0}", model.getBuild());
                LOGGER.log(Level.INFO, "POM-Version: {0}", model.getVersion());
                return model.getId();
            } else {
                LOGGER.log(Level.INFO, "POM: Resource nicht gefunden");
            }

        } catch (IOException | XmlPullParserException iOException) {
            LOGGER.log(Level.INFO, "POM: Resource nicht gefunden Fehler beim Einlesen ");
        }
        return "";
    }

    public String getWorkingDirInfo() {
        return getWorkingDir().toString();
    }

    public String getBackupInfo() {
        String strResult;
        try {
            FileServiceClient fileServiceClient = new FileServiceClient();
            strResult = fileServiceClient.getLastBackup();
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Konnte den Webservice nicht aufrufen.");
            isBackupAvailable = false;
            return "Webservice nicht erreichbar";
        }

        String strTimestamp = strResult.substring(8, strResult.length() - 4);
        Timestamp timestamp = null;
        Date parsedDate;
        LOGGER.log(Level.INFO, "Timestamp: " + strTimestamp);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            parsedDate = dateFormat.parse(strTimestamp);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
        }

        if (strResult.equals("")) {
            isBackupAvailable = false;
            return "";
        } else {
            VereinService vs = new VereinService();
            if (strResult.compareTo(vs.getExpFileName() + ".IDC") > 0) {
                isBackupAvailable = true;
            } else {
                isBackupAvailable = false;
            }
            LOGGER.log(Level.INFO, "strResult: {0} expFileName: {1} Result: {2} isBackup: {3}", new Object[]{strResult, vs.getExpFileName() + ".IDC", strResult.compareTo(vs.getExpFileName() + ".IDC"), isBackupAvailable});

            return new SimpleDateFormat("dd.MM.yyyy HH:mm.ss").format(timestamp);
        }
    }

    public String getLastBackup() {
        if (isIsBackupAvailable()) {
            return "Datenstand vom " + getBackupInfo() + " laden";
        } else {
            return "z.Zt. kein Datenupdate verfügbar";
        }
    }

    public void uploadLastBackup() {
        if (isIsBackupAvailable()) {
            String strResult;
            FileServiceClient fileServiceClient;
            try {
                fileServiceClient = new FileServiceClient();
                strResult = fileServiceClient.getLastBackup();
            } catch (Exception ex) {
                isBackupAvailable = false;
                return;
            }

            Path lastBackup = fileServiceClient.downloadFile(strResult);
            if (lastBackup != null) {
                SqlSupport sqlSupport = new SqlSupport();
                sqlSupport.importSchema("IDCREMOTE", lastBackup);

                addMessage("Fertig", "Datenstand vom " + getBackupInfo() + " in die Datenbank kopiert");
                return;
            }
        }
        addMessageFail("Fehler", "Datenstand vom " + getBackupInfo() + " konnte nicht in die Datenbank kopiert werden. Siehe Log-File ausgabe.");
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

    public void addEntenhauusen() {
        SqlSupport sp = new SqlSupport();
        sp.executeSQLScript("demo/entenhausenadd.sql");
        addMessage("Fertig", "Demodaten in die Datenbank kopiert");
    }

    public void dropEntenhauusen() {
        SqlSupport sp = new SqlSupport();
        sp.executeSQLScript("demo/entenhausendrop.sql");
        addMessage("Fertig", "Demodaten aus der Datenbank entfernt");
    }

    public void onlyEntenhauusen() {
        SqlSupport sp = new SqlSupport();
        sp.executeSQLScript("createdb.sql");
        sp.executeSQLScript("createverein.sql");
        sp.executeSQLScript("demo/entenhausenersv.sql");
        sp.executeSQLScript("demo/entenhausenadd.sql");
        addMessage("Fertig", "Datenbank aus Demodaten aus erstellt");
    }

    public void exportTables() {
        SqlSupport sp = new SqlSupport();

        String exportFile = sp.exportSchema("idcremote");
        boolean ws = true;
        try {
            FileServiceClient fileServiceClient = new FileServiceClient();
            fileServiceClient.uploadFile(exportFile);
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Konnte die Datei {0} nicht \u00fcber den Webservice exportieren", exportFile);
            ws = false;
            // Nix tun, wenn hier was schief läuft
        }

        if (ws) {
            addMessage("Fertig", "Die Datenbank wurde in die Datei " + exportFile + " exportiert und anschließend über den Webservice hochgeladen.");
        } else {
            addMessage("Fertig", "Die Datenbank wurde in die Datei " + exportFile + " exportiert. Der Webservice war nicht verfügbar.");
        }
    }

    public void addMessage(String summary, String detail) {
        LOGGER.log(Level.INFO, "{0} {1}", new Object[]{summary, detail});
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage("AppControl", message);
    }

    public void addMessageFail(String summary, String detail) {
        LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{summary, detail});
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
        FacesContext.getCurrentInstance().addMessage("AppControl", message);
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
