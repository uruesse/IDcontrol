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
package net.ruesse.idc.report;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import net.ruesse.idc.control.ApplicationControlBean;
import net.ruesse.idc.control.Constants;
import net.ruesse.idc.database.sql.SqlSupport;
import net.ruesse.idc.mglinfo.MglView;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class PrintSupport {

    private final static Logger LOGGER = Logger.getLogger(PrintSupport.class.getName());

    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME).createEntityManager();

    public PrintSupport() {
        LOGGER.setLevel(Level.INFO);
    }

    /**
     *
     * @return
     */
    public static List<String> availablePrinters() {
        List<String> printers = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            LOGGER.log(Level.INFO, "Gefundener Drucker:[{0}]", printService.getName());
            printers.add(printService.getName());
        }
        return printers;
    }

    public void test(JasperReport report) {
        
        //http://jasperreports.sourceforge.net/sample.reference/batchexport/index.html
        //http://jasperreports.sourceforge.net/sample.reference/printservice/index.html
        //https://github.com/eugenp/tutorials/blob/master/spring-all/src/main/java/org/baeldung/jasperreports/SimpleReportExporter.java
        
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("aParameter", "Hallo Welt");
        java.sql.Connection conn = em.unwrap(java.sql.Connection.class);
        JasperPrint print;
        try {
            print = JasperFillManager.fillReport(report, parameter, conn);
        } catch (JRException ex) {
            Logger.getLogger(PrintSupport.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        PrinterJob job = PrinterJob.getPrinterJob();
        /* Create an array of PrintServices */
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        int selectedService = 0;
        /* Scan found services to see if anyone suits our needs */
        for (int i = 0; i < services.length; i++) {
            if (services[i].getName().toUpperCase().contains("Your printer's name")) {
                /*If the service is named as what we are querying we select it */
                selectedService = i;
            }
        }
        try {
            job.setPrintService(services[selectedService]);
        } catch (PrinterException ex) {
            Logger.getLogger(PrintSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        MediaSizeName mediaSizeName = MediaSize.findMedia(4, 4, MediaPrintableArea.INCH);
        printRequestAttributeSet.add(mediaSizeName);
        printRequestAttributeSet.add(new Copies(1));
        JRPrintServiceExporter exporter;
        exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        /* We set the selected service and pass it as a paramenter */
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[selectedService].getAttributes());
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.TRUE);
        try {
            exporter.exportReport();
        } catch (JRException ex) {
            Logger.getLogger(PrintSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param printerName
     * @return
     */
    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

    static public void printThePDF(String file, String printer) throws Exception {

        PDDocument document = PDDocument.load(new File(file));

        PrintService myPrintService = findPrintService(printer);

        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPageable(new PDFPageable(document));

        job.setPrintService(myPrintService);
        job.print();

    }

    /**
     *
     * @param report
     * @return
     */
    public static JasperReport getReport(String report) {

        String jasperFileName = Constants.REPORTS_DIR + report + Constants.REPORT_DST;

        if (!Files.exists(Paths.get(jasperFileName))) {
            try {
                String str = compileTheReportToFile(report);
                LOGGER.info("Report kompiliert: " + str);
            } catch (JRException | IOException ex) {
               LOGGER.log(Level.SEVERE, null, ex);
                return null;
            }
        }
        JasperReport jr = null;
        if (!Files.exists(Paths.get(jasperFileName))) {
            LOGGER.info("Report wurde nicht korrekt kompiliert versuche on the fly zu kompilieren ");
            try {
                jr = compileReport(report);
            } catch (JRException | IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        } else {

            try {
                jr = (JasperReport) JRLoader.loadObjectFromFile(jasperFileName);
            } catch (JRException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        return jr;

    }

    /**
     *
     * @param report
     * @return
     * @throws JRException
     * @throws IOException
     */
    public static JasperReport compileReport(String report) throws JRException, IOException {

        JasperReport jasperReport = null;

        String jrxmlFileName = Constants.REPORTTEMPLATES_DIR + report + Constants.REPORT_SRC;

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

    /**
     *
     * @param report
     * @return
     * @throws JRException
     * @throws IOException
     */
    public static String compileTheReportToFile(String report) throws JRException, IOException {

        String jrxmlFileName = Constants.REPORTTEMPLATES_DIR + report + Constants.REPORT_SRC;
        String jasperFileName = Constants.REPORTS_DIR + report + Constants.REPORT_DST;
        LOGGER.log(Level.INFO, "Kompiliere: {0}", jrxmlFileName);

        InputStream jrxmlInput = JRLoader.getFileInputStream(jrxmlFileName);

        if (jrxmlInput != null) {
            JasperDesign design;
            try {
                design = JRXmlLoader.load(jrxmlInput);
            } finally {
                jrxmlInput.close();
            }
            JasperCompileManager.compileReportToFile(design, jasperFileName);
            return jasperFileName;
        }
        return null;
    }

    
   /**
    * 
    * @param report
    * @param em 
    */
    public static void printReport(String report, EntityManager em) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;

        LOGGER.fine("vor Compile Report");
        jasperReport = getReport(report);
        LOGGER.fine("Nach Compile Report");

        // Initialisieren der HashMap wird noch nicht verwendet
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("aParameter", "Hallo Welt");

        try {
            if (jasperReport != null) {
                em.getTransaction().begin();

                LOGGER.fine("jasperReport ist ungleich null");
                java.sql.Connection conn = em.unwrap(java.sql.Connection.class);
                //java.sql.Connection conn = new SqlSupport().getSqlConnection();
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, conn);

                if (ApplicationControlBean.isPDF()) {
                    String pdfFile = Constants.TEMP_DIR + report + ".pdf";
                    JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile);

                    if (!ApplicationControlBean.getKartendrucker().isEmpty()) {
                        try {
                            //printThePDF(pdfFile, "ZEBRA CARD PRINTER ZXP11");
                            printThePDF(pdfFile, ApplicationControlBean.getKartendrucker());
                        } catch (Exception ex) {
                            Logger.getLogger(MglView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        JasperPrintManager.printReport(jasperPrint, ApplicationControlBean.isDruckerdialog());
                    } catch (JRException err) {
                        LOGGER.info(err.getMessage());
                    }
                }

                em.getTransaction().commit();
            } else {
                LOGGER.fine("jasperReport ist gleich null");
            }
        } catch (JRException ex) {
            Logger.getLogger(MglView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param jasperReport
     */
    public void exportReport(JasperReport jasperReport) {
        JasperPrint jasperPrint;

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("aParameter", "Hallo Welt");

        try {
            if (jasperReport != null) {
                em.getTransaction().begin();
                java.sql.Connection connection = em.unwrap(java.sql.Connection.class);

                LOGGER.fine("jasperReport ist ungleich null");

                jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, connection);
                /*
                try {
                    JasperPrintManager.printReport(jasperPrint, true);
                } catch (JRException err) {
                    LOGGER.info(err.getMessage());
                }
                 */
                JasperExportManager.exportReportToPdfFile(jasperPrint, "/Users/ulrich/Desktop/Example.pdf");

                em.getTransaction().commit();
            } else {
                LOGGER.fine("jasperReport ist gleich null");
            }
        } catch (JRException ex) {
            Logger.getLogger(MglView.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*
        try {
            printThePDF("/Users/ulrich/Desktop/Example.pdf", "ZEBRA CARD PRINTER ZXP11");
            //printThePDF("/Users/ulrich/Desktop/Example.pdf", "swdrucker");
        } catch (Exception ex) {
            Logger.getLogger(MglView.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
    }
}
