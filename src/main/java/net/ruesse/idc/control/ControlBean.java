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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.text.MaskFormatter;
import net.ruesse.idc.database.persistence.Person;
import net.ruesse.idc.database.persistence.Scanlog;
import net.ruesse.idc.database.persistence.service.PersonExt;
import net.ruesse.idc.database.persistence.service.PersonService;
import static net.ruesse.idc.ressources.MsgBundle.getMessage;
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
    private static final String PERSISTENCE_UNIT_NAME = "net.ruesse.IDControl.PU";
    private static EntityManagerFactory factory;
    private static EntityManager em;

    /**
     *
     */
    public ControlBean() {
        LOGGER.setLevel(Level.INFO);
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }

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

    private long Mgl2Long(String text) {
        long mgl = 0;
        String strlong = text.replaceAll(" ", "");
        try {
            mgl = Long.parseLong(strlong);
        } catch (java.lang.NumberFormatException e) {
            mgl = 0;
        }
        return mgl;
    }

    /**
     *
     */
    public void showMessage() {
        LOGGER.log(Level.FINE, "mnr={0}", this.mnr);

        //Query q = em.createQuery("SELECT p FROM Person p WHERE p.mglnr = :mglnr");
        Query q = em.createNamedQuery("Person.findByMglnr");
        q.setParameter("mglnr", Mgl2Long(this.mnr));
        Person person;
        try {
            person = (Person) q.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            person = null;
        }

        showPersonStatus(new PersonExt(person));
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

    /**
     *
     * @param atype
     * @param summary
     */
    private void showAccessMessage(accesstype atype, String summary) {
        showAccessMessage(atype, summary, "");
    }

    /**
     *
     * @param atype
     * @param summary
     * @param detail
     * @param param
     */
    private void showAccessMessage(accesstype atype, String summary, String detail, Object param) {
        showAccessMessage(atype, summary, java.text.MessageFormat.format(detail, param));
    }

    /**
     *
     * @param atype
     * @param summary
     * @param detail
     * @param params
     */
    private void showAccessMessage(accesstype atype, String summary, String detail, Object[] params) {
        showAccessMessage(atype, summary, java.text.MessageFormat.format(detail, params));
    }

    /**
     *
     * @param pe
     */
    public void showPersonStatus(PersonExt pe) {
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

        accesstype atype = accesstype.access;
        if (pe.person == null) {
            if (this.mnr == null || this.mnr.isEmpty()) {
                showAccessMessage(accesstype.error, getMessage("control.qrcodeerror"), getMessage("control.einlesefehler"));
            } else {
                showAccessMessage(accesstype.error, getMessage("control.mitgliednichtgefunden"), getMessage("control.gescannterwert"), this.mnr);
            }
        } else {

            Scanlog sl = new Scanlog();
            sl.setScantime(new Timestamp(System.currentTimeMillis()));
            sl.setMglnr(pe.person);
            em.getTransaction().begin();
            em.persist(sl);
            em.getTransaction().commit();

            atype = accesstype.access;

            String op = pe.getOpenbills();

            if (!"".equals(op)) {
                atype = accesstype.doubt;
            }

            if (pe.person.getAustritt() != null) {
                if (pe.person.getAustritt().compareTo(new Date()) < 0) {
                    atype = accesstype.deny;
                    showAccessMessage(atype, getMessage("control.mitgliedausgetreten"), df.format(pe.person.getAustritt()));
                }
            }

            showAccessMessage(atype, getMessage("control.beitragsinformationfuer") + pe.getFullname(), "");

            if (null == pe.person.getFnr()) {
                showAccessMessage(atype, getMessage("control.mitgliedsnummer"), pe.getStrFMglnr());
            } else {
                showAccessMessage(atype, "Familienmitglied - Mitgliedsnummer", pe.getStrFMglnr());
            }

            showAccessMessage(atype, getMessage("control.alter"), String.valueOf(pe.getAge()));
            //showAccessMessage(atype, getMessage("control.offenerposten"), nf.format((double) pe.getOpenposts() / 100));

            if (pe.person.getBemerkung() != null) {
                showAccessMessage(accesstype.doubt, "Bemerkung: ", pe.person.getBemerkung());
            }

            if (pe.person.getAbweichenderzahler()) {
                if (pe.person.getFremdzahler() == null) {
                    showAccessMessage(accesstype.doubt, "Abweichender Zahler unbekannt. Angaben zum Beitragskonto könnten falsch sein!");
                } else {
                    showAccessMessage(atype, "Abweichender Zahler: ", pe.getFremdzahlerInfo());
                }
            }
            if ("".equals(op)) {
                showAccessMessage(atype, "Beitragskonto ist ausgeglichen");
            } else {
                showAccessMessage(atype, getMessage("control.offenerposten"), op);
            }

            String status = pe.getState();
            switch (status) {
                case "Mitarbeiter":
                    showAccessMessage(accesstype.access, getMessage("control.wasseregeld"), status);
                    break;
                case "aktiv":
                    showAccessMessage(atype, getMessage("control.wasseregeld"), status);
                    break;
                default:
                    showAccessMessage(accesstype.deny, getMessage("control.wasseregeld"), status);
            }
        }
    }

    /*
    **
    ** Nachfolgender code = Softwarescanner
    **
     */
    private String filename;

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

        showPersonStatus(new PersonExt(ps.findPerson(decodermessage)));
    }
}
