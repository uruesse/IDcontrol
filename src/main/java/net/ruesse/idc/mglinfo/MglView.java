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
package net.ruesse.idc.mglinfo;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.ruesse.idc.database.persistence.Auswahl;
import net.ruesse.idc.database.persistence.Person;
import net.ruesse.idc.database.persistence.service.PersonExt;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
@SessionScoped
public class MglView implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(MglView.class.getName());

    private static final String PERSISTENCE_UNIT_NAME = "net.ruesse.IDControl.PU";
    private static EntityManagerFactory factory;

    private List<PersonExt> allMgl;
    private List<PersonExt> filteredMgl;
    private List<PersonExt> selectedMgl;

    private PersonExt selectedPerson;

    public List<PersonExt> getSelectedMgl() {
        return selectedMgl;
    }

    public void setSelectedMgl(List<PersonExt> selectedMgl) {
        this.selectedMgl = selectedMgl;
    }

    public List<PersonExt> getFilteredMgl() {
        return filteredMgl;
    }

    public void setFilteredMgl(List<PersonExt> filteredMgl) {
        this.filteredMgl = filteredMgl;
    }

    public List<PersonExt> getAllMgl() {
        return allMgl;
    }

    public void setAllMgl(List<PersonExt> allMgl) {
        this.allMgl = allMgl;
    }

    /**
     *
     */
    @PostConstruct
    public void init() {
        allMgl = new ArrayList<>();
        //this.allMgl = mitgliederliste.createMitgliederliste();
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();

        LOGGER.log(Level.FINE, "Starte Query auf Personen");
        Query q = em.createNamedQuery("Person.findAll");
        List<Person> personlist = q.getResultList();

        LOGGER.log(Level.FINE, "VorForEach");
        for (Person p : personlist) {
            LOGGER.log(Level.FINE, "mnr: {0}", p.getMglnr());
            allMgl.add(new PersonExt(p));
        }
    }

    /**
     *
     */
    public MglView() {
        LOGGER.setLevel(Level.FINE);
    }

    /**
     *
     * @return
     */
    public List<PersonExt> getMglliste() {
        LOGGER.log(Level.FINE, "Persons: {0}", allMgl.toString());
        return allMgl;
    }

    /**
     *
     * @return
     */
    public PersonExt getSelectedPerson() {
        LOGGER.fine("aufgerufen");
        if (selectedPerson != null) {
            LOGGER.log(Level.FINE, "Mitgliedsnummer: {0}", selectedPerson.person.getMglnr());
        }
        return selectedPerson;
    }

    public String getStrMglnrOfSelectedPerson() {
        return String.format("%013d", selectedPerson.person.getMglnr());
    }

    /**
     *
     * @param selectedPerson
     */
    public void setSelectedPerson(PersonExt selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

    /**
     *
     * @param printerName
     * @return
     */
    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            LOGGER.log(Level.INFO, "Gefundene Drucker:[{0}]", printService.getName());
        }
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

    public static void printThePDF(String file, String printer) throws Exception {

        PDDocument document = PDDocument.load(new File(file));

        PrintService myPrintService = findPrintService(printer);

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        job.setPrintService(myPrintService);
        job.print();

    }

    /**
     *
     * @param jrxmlFileName
     * @return
     * @throws JRException
     * @throws IOException
     */
    public JasperReport compileReport(String jrxmlFileName) throws JRException, IOException {

        /*
        https://www.programcreek.com/java-api-examples/?api=net.sf.jasperreports.engine.JasperCompileManager
         */
        JasperReport jasperReport = null;

        InputStream jrxmlInput = JRLoader.getFileInputStream(jrxmlFileName);

        if (jrxmlInput != null) {
            JasperDesign design;
            try {
                design = JRXmlLoader.load(jrxmlInput);
            } finally {
                jrxmlInput.close();
            }
            jasperReport = JasperCompileManager.compileReport(design);
        }

        return jasperReport;
    }

    public void printAction() {
        JasperReport jasperReport = null;
        JasperPrint jasperPrint;

        // https://stackoverflow.com/questions/3493495/getting-database-connection-in-pure-jpa-setup
        LOGGER.fine("vor Compile Report");
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();

        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM DLRG.AUSWAHL").executeUpdate();
        // Auswahl füllen
        for (PersonExt p : selectedMgl) {
            Auswahl a = new Auswahl(p.person.getMglnr());
            try {
                em.persist(a);
            } catch (EntityExistsException e) {
                // ignorieren
            }
        }
        em.getTransaction().commit();

        try {
            try {
                jasperReport = compileReport("/Users/ulrich/JaspersoftWorkspace/IDControl/IDKarte.jrxml");
                LOGGER.fine("Nach Compile Report");
            } catch (JRException ex) {
                Logger.getLogger(MglView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MglView.class.getName()).log(Level.SEVERE, null, ex);
            }
            HashMap<String, Object> parameter = new HashMap<>();
            parameter.put("aParameter", "Hallo Welt");
            if (jasperReport != null) {
                em.getTransaction().begin();
                java.sql.Connection connection = em.unwrap(java.sql.Connection.class);

                LOGGER.fine("jasperReport ist ungleich null");

                jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, connection);
                JasperExportManager.exportReportToPdfFile(jasperPrint, "/Users/ulrich/Desktop/Example.pdf");
                em.createNativeQuery("DELETE FROM DLRG.AUSWAHL").executeUpdate();
                em.getTransaction().commit();
            } else {
                LOGGER.fine("jasperReport ist gleich null");
            }
        } catch (JRException ex) {
            Logger.getLogger(MglView.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            printThePDF("/Users/ulrich/Desktop/Example.pdf", "swdrucker");
        } catch (Exception ex) {
            Logger.getLogger(MglView.class.getName()).log(Level.SEVERE, null, ex);
        }
        addMessage("Fertig", "Druckauftrag erledigt");
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
