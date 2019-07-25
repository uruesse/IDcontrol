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
package net.ruesse.idc.dataimport;

import com.csvreader.CsvReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.application.Constants;
import static net.ruesse.idc.control.FileService.deleteDirectoryStream;
import static net.ruesse.idc.control.FileService.getTempDir;
import net.ruesse.idc.database.persistence.Verein;
import net.ruesse.idc.database.persistence.service.PersonCache;
import net.ruesse.idc.database.sql.SqlSupport;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Named
@SessionScoped
public class LoadWizard implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(LoadWizard.class.getName());

    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();
    private static final long serialVersionUID = 1L;

    @Inject
    PersonCache pc;

    private boolean skip;
    List<DataTable> DataTableList;

    /**
     *
     */
    @PostConstruct
    public void init() {
        DataTableList = createDataTable();
    }

    /**
     *
     * @return
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     *
     * @param skip
     */
    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    /**
     *
     * @param event
     * @return
     */
    public String onFlowProcess(FlowEvent event) {
        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else {
            return event.getNewStep();
        }
    }

    /**
     *
     * @return
     */
    public String getCreatestagedbHint() {
        return "Der Datenimport erfolgt über mehrere Stufen.<br/>Zunächst werden die Daten aus SEWOBE unverändert in eine tempräre Datenbank, der Staging-Datenbank geladen. Dann wird zunächst deren Itegrität geprüft und die Daten für die Anwendung ID-Control aufbereitet. Anschließend werden die Daten aus der Staging Datenbank in die ID-Control Datenbank kopiert. Zum Schluss wird die Staging Datenbank gelöscht.<br/><br/>Dieser Schritt initialisiert die Staging-Datenbank und kann nicht rückgängig gemacht werden.";
    }

    /**
     *
     * @return
     */
    public String getLoadcsvHint() {
        return "In diesem Schritt werden die exportierten .csv-Dateien aus der SEWOBE-Datenbank hochgeladen und in die Stage-Datenbank importiert.<br/>Dabei muss der Dateiname der SEWOBE-Export-Datei (ohne die Enndung .csv) dem Tabellennamen in der Stage-Datenbank entsprechen, wobei großgeschriebene Dateinamen UTF-8 kodiert, alle anderen ISO-8859-1 kodiert eingelesen weden. Dateien mit falschem Namen werden ignoriert.<br/>Bei fehlerhaften Datensätzen versucht das Programm so viele Daten wie möglich zu laden und gibt über die fehlerhaften Datensätze ein Fehlerprotokoll heraus. Kann der Import bereits die Kopfzeile der .csv-Datei nicht auflösen, bricht der Import sofort ab.<br/>Eine Anleitung zum Export der Dateien aus SWEOBE findet sich im Handbuch für die Mitgliederverwaltung";
    }

    /**
     *
     * @return
     */
    public String getLoadStage2ProdHint() {
        return "In diesen Schritt werden die Daten in der Staging Datenbank zunächst validiert, dann überarbeitet und anschließend in die ID-Control Datenbank übertragen. Fehler werden weitestgehend ignoriert.";
    }

    /**
     *
     * @return
     */
    public String getLoadFinalizeHint() {
        return "Schließt die Datenübernahme ab, löscht temporäre Dateien und springt in die Mitgliederübersicht. Hier können die übertragenen Daten kontrolliert werden. Im Anschluss daran sollte ein Datenexport stattfinden, mit dem die anderen Instanzen cvon ID-Control befüllt werden können.";
    }

    /**
     *
     * @return
     */
    public List<DataTable> getDataTableList() {
        return DataTableList;
    }

    /**
     *
     * @param DataTableList
     */
    public void setDataTableList(List<DataTable> DataTableList) {
        this.DataTableList = DataTableList;
    }

    private boolean step1done = false;
    private boolean step3done = false;
    private boolean stepxdone = false;

    /**
     *
     */
    public void stageCreate() {
        SqlSupport sp = new SqlSupport();

        sp.executeSQLScript("createstagedb.sql");
        step1done = true;
        addMessage("Die Datenbank wurde angelegt.", "Bitte zum nächsten Schritt gehen");
        DataTableList = createDataTable();
    }

    /**
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void validateStep1(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!step1done) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Die Datenbank wurde noch nicht initialisiert.", "Bitte zuerst die Datenbank initialisieren"));
        }
    }

    /**
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void validateStep2(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        boolean nextstep = true;
        for (DataTable dt : DataTableList) {
            if ((dt.getTablename().equals("PERSON") || dt.getTablename().equals("BEITRAGSPOSITIONEN")) && dt.getItems() == 0) {
                nextstep = false;
            }
        }
        if (!nextstep) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Minimalanforderungen nicht ausreichend", "Es müssen zumindest die Tabellen PERSON und BEITZRAGSPOSITIONEN gefüllt sein"));
        }
    }

    /**
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void validateStep3(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!step3done) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "", ""));
        }
    }

    /**
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    public void validateStep(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!stepxdone) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "", ""));
        }
    }

    /**
     *
     * @param summary
     * @param detail
     */
    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     *
     * @param summary
     * @param detail
     */
    public void addMessageFail(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {

        String fn = event.getFile().getFileName();
        Path pDest = getTempDir().resolve(fn);

        try {
            copyFile(pDest, event.getFile().getInputstream());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        ReadCSV(pDest);

    }

    /**
     *
     * @param pDest
     * @param in
     */
    public void copyFile(Path pDest, InputStream in) {

        OutputStream out = null;
        try {
            // write the inputStream to a FileOutputStream
            out = new FileOutputStream(pDest.toFile());
            boolean firstread = true;
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                //BOM für UTF-8 ist 0xEF, 0xBB, 0xBF
                // hier wird beim ersten Lesen ein eventuell vorhandener BOM entfernt
                if (firstread && read > 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    LOGGER.log(Level.INFO, "Entferne BOM");
                    out.write(bytes, 3, read - 3);
                } else {
                    out.write(bytes, 0, read);
                }
                firstread = false;
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

    /**
     *
     * @param filepath
     */
    public void ReadCSV(Path filepath) {
        int count = 0;
        String InsertString;
        String csvfile = filepath.toString();
        String filename;
        filename = filepath.getFileName().toString();

        // (.csv entfernen)
        filename = filename.substring(0, filename.length() - 4);

        // großgeschriebene Dateinamen werden als UTF-8 kodiert angesehen alle anderen als ISO-8859-1
        // das ist eine schmutzige implementierung und ein workaround der nur notwendig ist, solange 
        // manuell erstellte .csv-Dateien eingelesen werden
        boolean isUTF = filename.toUpperCase().equals(filename);

        DataTable aktTable = null;

        for (DataTable dt : DataTableList) {
            if (dt.getTablename().toUpperCase().equals(filename.toUpperCase())) {
                aktTable = dt;
            }
        }

        if (aktTable == null) {
            LOGGER.log(Level.INFO, "Kann die {0}.csv-Datei nicht einlesen, da die korrespondierende Datenbanktabelle fehlt.", new Object[]{filename});
            addMessageFail("Fehler", "Die Datei " + filepath.getFileName().toString() + " konnte nicht eingelesen werden, da die korrespondierende Datenbanktabelle fehlt.");
            return;
        }

        // Sonderbehandlung für den Export der offenen Rechnungen aus SEWOBE
        // Die exportierte Datei ist strenggenommen keine richtige .csv-Datei und wird hier angepasst
        if ("offenerechnungen".equals(filename.toLowerCase())) {
            try {
                List<String> contents = Files.readAllLines(filepath, Charset.forName("ISO-8859-1"));
                Path outpath = filepath.getParent().resolve("offeneposten.csv");
                csvfile = outpath.toString();
                OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(outpath.toFile()), Charset.forName("ISO-8859-1"));
                //Headerzeile austauschen
                fw.write("kdnr;mitgliedsnummer_sewobe;rechnungsnummer;rechnungsdatum;zahlungsziel;zahlweise;mahnstufe;bezeichnung;aktuelloffen" + System.lineSeparator());

                //Read from the stream
                int i = 0;
                for (String content : contents) {
                    // die ersten beiden Zeilen verwerfen
                    if (i > 1) {
                        // das letzte Semikolon entfernen
                        if (content.endsWith(";")) {
                            content = content.substring(0, content.length() - 1);
                        }
                        fw.write(content + System.lineSeparator());
                    }
                    i++;
                }
                fw.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        SqlSupport sp = new SqlSupport();

        sp.beginTransaction();

        try {
            CsvReader person;
            Charset charSet;
            if (isUTF) {
                LOGGER.log(Level.INFO, "UTF-8");
                charSet = Charset.forName("UTF-8");
            } else {
                LOGGER.log(Level.INFO, "ISO-8859-1");
                charSet = Charset.forName("ISO-8859-1");
            }
            person = new CsvReader(csvfile, ';', charSet);
            person.readHeaders();
            String[] header = person.getHeaders();

            sp.nativeSQL("DELETE FROM IDCSTAGE." + filename);
            InsertString = "insert into IDCSTAGE." + filename + " (";
            boolean first = true;
            for (String header1 : header) {
                if (first) {
                    first = false;
                } else {
                    InsertString += (",");
                }
                InsertString += header1.toUpperCase();
            }
            InsertString += ") VALUES";
            while (person.readRecord()) {
                count++;
                String[] precord = person.getValues();
                String sqlinsert = InsertString;
                sqlinsert += '(';
                first = true;
                int index = 0;
                for (String precord1 : precord) {
                    if (first) {
                        first = false;
                    } else {
                        sqlinsert += ",";
                    }
                    switch (header[index].toLowerCase()) {
                        case "nummer":
                        case "mitgliedsnummer_sewobe":
                        case "mitgliedsnummer_fremdzahler":
                        case "vereinsnr":
                        case "familiennummer_sewobe":
                        case "alter_errechnet":
                        case "rechnungsnummer":
                        case "mahnstufe":
                            if (precord1.isEmpty()) {
                                sqlinsert += "null";
                            } else {
                                sqlinsert += precord1;
                            }
                            break;
                        case "mandat_vorhanden":
                        case "abweichenderzahler":
                            if (precord1.equals("1")) {
                                sqlinsert += "true";
                            } else {
                                sqlinsert += "false";
                            }
                            break;
                        case "geburtsdatum":
                        case "eintritt":
                        case "austritt":
                        case "kuendigung":
                        case "rechnungsdatum":
                        case "faellig_start":
                        case "faellig_ende":
                        case "lebenslaufbeginn":
                        case "lebenslaufende":
                        case "zahlungsziel":
                            if (precord1.isEmpty()) {
                                sqlinsert += "null";
                            } else {
                                Date date1 = null;
                                try {
                                    date1 = new SimpleDateFormat("dd.mm.yyyy").parse(precord1);
                                } catch (ParseException ex) {
                                    LOGGER.log(Level.SEVERE, null, ex);
                                }
                                if (date1 == null) {
                                    sqlinsert += "null";
                                } else {
                                    sqlinsert += "'" + new SimpleDateFormat("yyyy-mm-dd").format(date1) + "'";
                                }
                            }
                            break;
                        case "aktuelloffen":
                            if (precord1.isEmpty()) {
                                sqlinsert += "null";
                            } else {
                                String str = precord1;
                                str = str.replace(" EUR", "");
                                str = str.replace(",", "");
                                sqlinsert += str;
                            }
                            break;
                        case "offene_beitraege":
                        case "rechnungssumme":
                            if (precord1.isEmpty()) {
                                sqlinsert += "null";
                            } else {
                                Float f = Float.parseFloat(precord1.replace(',', '.'));
                                f *= 100;
                                f.intValue();
                                sqlinsert += f.intValue();
                            }
                            break;
                        default:
                            if (precord1.isEmpty()) {
                                sqlinsert += "null";
                            } else {
                                sqlinsert += "'" + precord1 + "'";
                            }
                    }
                    index++;
                }
                sqlinsert += ")";
                LOGGER.info(sqlinsert);
                sp.nativeSQL(sqlinsert);
            }
            person.close();

        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Datei nicht gefunden", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IO-Fehler", ex);

        }

        aktTable.setItems(sp.getTableSize("IDCSTAGE", filename));
        aktTable.setCsv(count);

        sp.commitTransaction();

        addMessage(
                "Fertig", "Die Datei " + filepath.getFileName().toString() + " wurde erfolgreich hochgeladen und in die Datenbank importiert.");
    }

    /**
     *
     */
    public void stageToProd() {
        SqlSupport sp = new SqlSupport();
        sp.executeSQLScript("createdb.sql");
        sp.executeSQLScript("stage2prod.sql");
        step3done = true;
        addMessage("Fertig", "Die Datenbank wurde erfolgreich kopiert");
    }

    /**
     *
     */
    public void finalizeImport() {

        SqlSupport sp = new SqlSupport();
        sp.executeSQLScript("deletestagedb.sql");

        //sp.beginTransaction();
        //sp.nativeSQL("update IDCREMOTE.VEREIN set DATATIME=CURRENT_TIMESTAMP");
        //sp.commitTransaction();

        /*
        ** An dieser Stelle sollte der gesamte Persistenzcache gelöscht werden.
        ** em.clear klappt irgendwie nicht richtig
        ** deswegen
        ** folgt der explizite Refresh auf die Vereinssuche als Workaround
         */
        //em.clear();
        //Cache cache = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).getCache();
        //cache.evictAll();
        pc.invalidate();
        // --- BEGINN WORKAROUND
        Query q = em.createNamedQuery("Verein.findAll", Verein.class
        );
        q.setHint("eclipselink.refresh", "true");
        try {
            Verein v = (Verein) q.getSingleResult();
        } catch (NoResultException e) {
            //nix tun
        }
        // --- ENDE WORKAROUND

        deleteDirectoryStream(getTempDir());
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("mglinfo.xhtml");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public List<DataTable> createDataTable() {
        SqlSupport sp = new SqlSupport();
        List<String> tables = sp.getTables("IDCSTAGE");

        List<DataTable> list = new ArrayList<>();

        tables.forEach((String table) -> {
            long size = sp.getTableSize("IDCSTAGE", table);
            LOGGER.log(Level.INFO, "Tabellen\u00fcbersicht: {0}, {1}", new Object[]{table, size});

            list.add(new DataTable(table.toUpperCase(), size, 0, 0));
        });

        return list;
    }
}
