package net.ruesse.idc.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.text.MaskFormatter;
import net.ruesse.idc.test.Person;
import net.ruesse.idc.test.PersonService;
import org.primefaces.event.CaptureEvent;

/**
 *
 * @author ulrich
 */
@ManagedBean
@ViewScoped
//@RequestScoped
public class ControlBean implements Serializable {
    

    private String filename;
    PersonService ps = new PersonService();

    //@ManagedProperty("#(param.mnr)")
    private String mnr;
    private static String decodermessage;

    public String getParam() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
        String projectId = paramMap.get("mnr");
        System.out.println("In getParam " + projectId);
        showMessage();
        return projectId;
    }

    //@PostConstruct
    //public void init() {
    //    setMglnr("0920000 7");
    //}
    public String getMnr() {
        System.out.println("In getMnr " + mnr);
        return mnr;
    }

    public void setMnr(String strmnr) {
        System.out.println("In setMnr " + strmnr);
        this.mnr = strmnr;
    }

    /* public void Action() {
        System.out.println("In Action " + this.mnr);
        infox();
    }
     */
    public void showMessage() {
        System.out.println("In showMessage " + this.mnr);
        showPersonStatus(ps.findPerson(this.mnr));
    }

    public void showPersonStatus(Person person) {
        DateFormat df;
        df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);

        NumberFormat nf;
        nf = DecimalFormat.getCurrencyInstance(Locale.GERMANY);

        MaskFormatter mf = null;
        try {
            mf = new MaskFormatter("******* * *****");
            mf.setValueContainsLiteralCharacters(false);
        } catch (ParseException ex) {
            Logger.getLogger(ControlBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        FacesMessage.Severity sev;
        if (person == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Konnte QR-Code nicht einlesen", decodermessage));
        } else {
            sev = FacesMessage.SEVERITY_INFO;
            if (person.getOpenposts() > 0) {
                sev = FacesMessage.SEVERITY_WARN;
            }
            if (person.getExitdate() != null) {
                if (person.getExitdate().compareTo(new Date()) < 0) {
                    sev = FacesMessage.SEVERITY_ERROR;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, "Mitglied ist ausgetreten am:  ", df.format(person.getExitdate())));
                }
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, "Beitragsinformation für " + person.getFullname(), ""));
            try {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, "Mitgliedsnummer.  ", mf.valueToString(person.getMglnr())));
            } catch (ParseException ex) {
                Logger.getLogger(ControlBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            Date now = new Date();
            long yearInMillis = 365 * 24 * 60 * 60 * 1000;
            long age = (now.getTime() - person.getBirthdate().getTime()) / 365 / 24 / 60 / 60 / 1000;

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, "Alter: ", String.valueOf(age)));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, "Offener Posten: ", " " + nf.format((double)person.getOpenposts() / 100)));

            switch (person.getState()) {
                case "Mitarbeiter":
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Wassergeld: ", person.getState()));
                    break;
                case "aktives Mitglied":
                    if (person.openwaterbill > 0) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Wassergeld: ", person.getState() + " offener Betrag: " + nf.format((double)person.getOpenwaterbill() / 100)));
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Wassergeld: ", person.getState() + ", Betrag ausgeglichen"));
                    }
                    break;
                default:
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wassergeld: ", person.getState()));
            }
        }

        System.out.println(
                "In showMessage " + this.mnr);
    }

    private static String decodeQRCode(File qrCodeimage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }

    private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);

        return String.valueOf(i);
    }

    public String getFilename() {
        return filename;
    }

    public void oncapture(CaptureEvent captureEvent) {
        filename = getRandomImageName();
        byte[] data = captureEvent.getData();

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String newFileName = externalContext.getRealPath("") + File.separator + "resources" + File.separator + "files"
                + File.separator + "images" + File.separator + "photocam" + File.separator + filename + ".jpeg";

        FileImageOutputStream imageOutput;
        File imageFile;
        try {
            imageFile = new File(newFileName);
            imageOutput = new FileImageOutputStream(imageFile);
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
        } catch (IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }

        try {
            String decodedText = decodeQRCode(imageFile);
            if (decodedText == null) {
                System.out.println("Kein QR-Code im eingescannten Bild gefunden");
                decodermessage = "Kein QR-Code im eingescannten Bild gefunden";
            } else {
                System.out.println("Decoded text = " + decodedText);
                decodermessage = decodedText;
                mnr = decodedText;
            }
        } catch (IOException e) {
            decodermessage = "Konnte QR Code nicht entschlüsseln, IOException :: " + e.getMessage();
            System.out.println(decodermessage);
        }

        showPersonStatus(ps.findPerson(decodermessage));
    }
}
