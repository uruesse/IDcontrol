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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterResolution;
import javax.servlet.http.HttpServletRequest;
import net.ruesse.idc.control.ApplicationControlBean;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.control.Constants;
import static net.ruesse.idc.control.FileService.getLogoDir;
import static net.ruesse.idc.control.FileService.getReportTemplatesDir;
import static net.ruesse.idc.control.FileService.getReportsDir;
import static net.ruesse.idc.control.FileService.getWorkingDir;
import net.ruesse.idc.mglinfo.MglView;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

/*
Hinzufügen von Fonts: siehe hier:
https://medium.com/@seymorethrottle/jasper-reports-adding-custom-fonts-589b55a52e7c

 */
/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class PrintSupport {

    private final static Logger LOGGER = Logger.getLogger(PrintSupport.class.getName());

    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

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

    /**
     * 
     * @param jasperPrint
     * @param report
     * @throws JRException 
     */
    private static void PrintReportToPDF(JasperPrint jasperPrint, String report) throws JRException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Path pdfFilePath = Paths.get(externalContext.getRealPath(""), "resources", "files", "pdf", report + ".pdf");
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath.toString());
    }

    /**
     * 
     * @param jasperPrint
     * @param selectedPrinter
     * @param anzahlDrucke
     * @throws JRException 
     */
    private static void PrintReportToPrinter(JasperPrint jasperPrint, String selectedPrinter, int anzahlDrucke) throws JRException {
        // Info zum Setzen der Media-Size
        //https://community.oracle.com/thread/1264031
        //http://jasperreports.sourceforge.net/sample.reference/batchexport/index.html
        //http://jasperreports.sourceforge.net/sample.reference/printservice/index.html
        //https://github.com/eugenp/tutorials/blob/master/spring-all/src/main/java/org/baeldung/jasperreports/SimpleReportExporter.java
        //http://jasperreports.sourceforge.net/sample.reference/batchexport/index.html
        //http://jasperreports.sourceforge.net/sample.reference/printservice/index.html
        //https://github.com/eugenp/tutorials/blob/master/spring-all/src/main/java/org/baeldung/jasperreports/SimpleReportExporter.java
        
        PrintService selectedService = findPrintService(selectedPrinter);
        
        if (selectedService != null) {
            //Set the printing settings
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            //printRequestAttributeSet.add(MediaSizeName.ISO_A4);
            //printRequestAttributeSet.add(MediaSizeName.ISO_A7);
            //printRequestAttributeSet.add(MediaSize.findMedia(54.19f,86.71f,Size2DSyntax.MM));
            //printRequestAttributeSet.add(MediaSize.findMedia(86.71f,54.19f,Size2DSyntax.MM));
            printRequestAttributeSet.add(new PrinterResolution(300, 300, PrinterResolution.DPI));
            //printRequestAttributeSet.add(new MediaSize(53.98f, 85.72f, Size2DSyntax.MM));
            //printRequestAttributeSet.add(new MediaPrintableArea(0.0f, 0.0f, 53.98f, 85.72f, MediaPrintableArea.MM));
            //printRequestAttributeSet.add(new MediaPrintableArea(0.0f, 0.0f, 2.125f, 3.375f, MediaPrintableArea.INCH));
            //printRequestAttributeSet.add(new MediaPrintableArea(0.0f, 0.0f, 54.0f, 85.6f, MediaPrintableArea.MM));
            //printRequestAttributeSet.add(new MediaPrintableArea(0.0f, 0.0f, 54.187f, 86.713f,  MediaPrintableArea.MM));
            //printRequestAttributeSet.add(new MediaPrintableArea(0.0f, 0.0f, 86.713f, 54.187f,  MediaPrintableArea.MM));
            printRequestAttributeSet.add(new Copies(anzahlDrucke));
            if (jasperPrint.getOrientationValue() == net.sf.jasperreports.engine.type.OrientationEnum.LANDSCAPE) {
                printRequestAttributeSet.add(OrientationRequested.LANDSCAPE);
            } else {
                printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
            }
            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
            printServiceAttributeSet.add(new PrinterName(selectedPrinter, null));
            
            SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
            configuration.setPrintService(selectedService);
            configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
            configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
            configuration.setDisplayPageDialog(false);
            configuration.setDisplayPrintDialog(ApplicationControlBean.isDruckerdialog());
            
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setConfiguration(configuration);

            try {
                exporter.exportReport();
            } catch (Exception e) {
                LOGGER.info("JasperReport Error: " + e.getMessage());
            }
        } else {
            LOGGER.info("JasperReport Error: Printer not found!");
        }
    }

    /**
     *
     * @param report
     * @return
     */
    public static JasperReport getReport(String report) {

        Path jasperFile;
        jasperFile = getReportsDir().resolve(report + Constants.REPORT_DST);

        if (!Files.exists(jasperFile)) {
            try {
                String str = compileTheReportToFile(report);
                LOGGER.info("Report kompiliert: " + str);
            } catch (JRException | IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                return null;
            }
        }
        JasperReport jr = null;
        if (!Files.exists(jasperFile)) {
            LOGGER.info("Report wurde nicht korrekt kompiliert versuche on the fly zu kompilieren ");
            try {
                jr = compileReport(report);
            } catch (JRException | IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                jr = (JasperReport) JRLoader.loadObjectFromFile(jasperFile.toString());
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

        String jrxmlFileName = "" + getReportTemplatesDir() + report + Constants.REPORT_SRC;

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

        Path jrxmlFile = getReportTemplatesDir().resolve(report + Constants.REPORT_SRC);
        Path jasperFile = getReportsDir().resolve(report + Constants.REPORT_DST);
        LOGGER.log(Level.INFO, "Kompiliere: {0}", jrxmlFile.toString());

        InputStream jrxmlInput = JRLoader.getFileInputStream(jrxmlFile.toString());

        if (jrxmlInput != null) {
            JasperDesign design;
            try {
                design = JRXmlLoader.load(jrxmlInput);
            } finally {
                jrxmlInput.close();
            }
            JasperCompileManager.compileReportToFile(design, jasperFile.toString());
            return jasperFile.toString();
        }
        return null;
    }

    /**
     *
     * @param report
     * @param em
     * @param anzahlDrucke
     * @param drucker
     */
    public static void printReport(String report, EntityManager em, int anzahlDrucke, String drucker) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;

        LOGGER.fine("vor Compile Report");
        jasperReport = getReport(report);
        LOGGER.fine("Nach Compile Report");

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("workingdir", getWorkingDir().toString());
        parameter.put("logodir", getLogoDir().toString());

        try {
            if (jasperReport != null) {
                em.getTransaction().begin();

                LOGGER.fine("jasperReport ist ungleich null");
                java.sql.Connection conn = em.unwrap(java.sql.Connection.class);
                //java.sql.Connection conn = new SqlSupport().getSqlConnection();
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, conn);

                if ("PDF".equals(drucker)) {
                    try {
                        PrintReportToPDF(jasperPrint, report);
                    } catch (JRException err) {
                        LOGGER.info(err.getMessage());
                    }
                    FacesContext context = FacesContext.getCurrentInstance();
                    HttpServletRequest origRequest = (HttpServletRequest) context.getExternalContext().getRequest();
                    String contextPath = origRequest.getContextPath();

                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + "/faces/druckausgabe.xhtml?name=" + report);
                    } catch (IOException ex) {
                        Logger.getLogger(ApplicationControlBean.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    try {
                        PrintReportToPrinter(jasperPrint, drucker, anzahlDrucke);
                        //JasperPrintManager.printReport(jasperPrint, ApplicationControlBean.isDruckerdialog());
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
}
