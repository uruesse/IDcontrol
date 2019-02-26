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
import static net.ruesse.idc.ressources.MsgBundle.getMessage;
import net.ruesse.idc.test.Person;
import net.ruesse.idc.test.PersonService;
import org.primefaces.event.CaptureEvent;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
@ViewScoped
//@RequestScoped
public class ControlBean implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(ControlBean.class.getName());

    /**
     *
     */
    public ControlBean() {
        LOGGER.setLevel(Level.INFO);
    }

    private String filename;
    PersonService ps = new PersonService();

    //@ManagedProperty("#(param.mnr)")
    private String mnr;
    private static String decodermessage;

    private enum accesstype {
        access, doubt, deny, error
    }

    /**
     *
     * @return projectId
     */
    public String getParam() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
        String projectId = paramMap.get("mnr");
        LOGGER.log(Level.FINE, "projectId={0}", projectId);
        showMessage();
        return projectId;
    }

    /**
     *
     * @return
     */
    public String getMnr() {
        LOGGER.log(Level.FINE, "mnr={0}", mnr);
        return mnr;
    }

    /**
     *
     * @param strmnr
     */
    public void setMnr(String strmnr) {
        LOGGER.log(Level.FINE, "strmnr={0}", strmnr);
        this.mnr = strmnr;
    }

    /**
     *
     */
    public void showMessage() {
        LOGGER.log(Level.FINE, "mnr={0}", this.mnr);
        showPersonStatus(ps.findPerson(this.mnr));
    }

    /**
     *
     * @param atype
     * @param summary
     * @param detail
     */
    private void showAccessMessage(accesstype atype, String summary, String detail) {
        FacesMessage.Severity sev;

        switch (atype) {
            case access:
                sev = FacesMessage.SEVERITY_INFO;
                break;
            case doubt:
                sev = FacesMessage.SEVERITY_WARN;
                break;
            case deny:
                sev = FacesMessage.SEVERITY_ERROR;
                break;
            case error:
            default:
                sev = FacesMessage.SEVERITY_FATAL;
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, summary, detail));
    }

    private void showAccessMessage(accesstype atype, String summary, String detail, Object param) {
        showAccessMessage(atype, summary, java.text.MessageFormat.format(detail, param));
    }

    private void showAccessMessage(accesstype atype, String summary, String detail, Object[] params) {
        showAccessMessage(atype, summary, java.text.MessageFormat.format(detail, params));
    }

    /**
     *
     * @param person
     */
    public void showPersonStatus(Person person) {
        DateFormat df;
        df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);

        NumberFormat nf;
        nf = DecimalFormat.getCurrencyInstance(Locale.GERMANY);

        LOGGER.log(Level.FINE, "mnr={0}", this.mnr);

        MaskFormatter mf = null;
        try {
            mf = new MaskFormatter(getMessage("control.mnr_mask"));
            mf.setValueContainsLiteralCharacters(false);
        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        accesstype atype;
        if (person == null) {
            showAccessMessage(accesstype.error, getMessage("control.qrcodeerror"), decodermessage);
        } else {
            atype = accesstype.access;
            if (person.getOpenposts() > 0) {
                atype = accesstype.doubt;
            }
            if (person.getExitdate() != null) {
                if (person.getExitdate().compareTo(new Date()) < 0) {
                    atype = accesstype.deny;
                    showAccessMessage(atype, getMessage("control.mitgliedausgetreten"),  df.format(person.getExitdate()));
                }
            }

            showAccessMessage(atype, getMessage("control.beitragsinformationfuer") + person.getFullname(), "");
            try {
                showAccessMessage(atype, getMessage("control.mitgliedsnummer"),  mf.valueToString(person.getMglnr()));
            } catch (ParseException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            Date now = new Date();
            long yearInMillis = 365 * 24 * 60 * 60 * 1000;
            long age = (now.getTime() - person.getBirthdate().getTime()) / 365 / 24 / 60 / 60 / 1000;

            showAccessMessage(atype, getMessage("control.alter"), String.valueOf(age));
            showAccessMessage(atype, getMessage("control.offenerposten"), nf.format((double) person.getOpenposts() / 100));

            switch (person.getState()) {
                case "Mitarbeiter":
                    showAccessMessage(accesstype.access, getMessage("control.wasseregeld"), person.getState());
                    break;
                case "aktives Mitglied":
                    if (person.openwaterbill > 0) {
                        showAccessMessage(accesstype.doubt, getMessage("control.wasseregeld"), getMessage("control.offenerbetrag"), new Object[]{person.getState(), nf.format((double) person.getOpenwaterbill() / 100)});
                    } else {
                        showAccessMessage(accesstype.access, getMessage("control.wasseregeld"), getMessage("control.betragausgeglichen"), person.getState() );
                    }
                    break;
                default:
                    showAccessMessage(accesstype.deny, getMessage("control.wasseregeld"), person.getState());
            }
        }
    }

    /**
     *
     * @param qrCodeimage
     * @return
     * @throws IOException
     */
    private static String decodeQRCode(File qrCodeimage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            LOGGER.fine("There is no QR code in the image");
            return null;
        }
    }

    /**
     *
     * @return
     */
    private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);

        return String.valueOf(i);
    }

    /**
     *
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param captureEvent
     */
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
                LOGGER.fine("Kein QR-Code im eingescannten Bild gefunden");
                decodermessage = getMessage("control.qrcodenotfound");
            } else {
                LOGGER.log(Level.FINE, "Decoded text = {0}", decodedText);
                decodermessage = decodedText;
                mnr = decodedText;
            }
        } catch (IOException e) {
            decodermessage = "Konnte QR Code nicht entschlüsseln, IOException: " + e.getMessage();
            LOGGER.fine(decodermessage);
        }

        showPersonStatus(ps.findPerson(decodermessage));
    }
}
