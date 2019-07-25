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

import net.ruesse.idc.application.Constants;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.text.MaskFormatter;
import static net.ruesse.idc.control.ApplicationControlBean.getLoginMglUserRights;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import static net.ruesse.idc.control.ApplicationControlBean.setLoginMgl;
import net.ruesse.idc.database.persistence.Person;
import net.ruesse.idc.database.persistence.Scanlog;
import net.ruesse.idc.database.persistence.service.PersonMgl;
import net.ruesse.idc.database.persistence.service.PersonUser;
import static net.ruesse.idc.ressources.MsgBundle.getMessage;
import org.primefaces.event.CaptureEvent;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Named
@RequestScoped
public class ControlBean implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(ControlBean.class.getName());
    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public ControlBean() {
        LOGGER.setLevel(Level.INFO);
    }


    private String mnr;
    private String mnrLogin;
    private static String decodermessage;
    private static PersonUser loginPerson;
    private Member member;
    private String scanCode;

    private enum accesstype {
        access, doubt, deny, error
    }

    /**
     *
     * @return userId
     */
    public String getParam() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
        if (paramMap != null) {
            String userId = paramMap.get("user");
            String startApp = paramMap.get("start");
            if (userId == null || userId.isEmpty()) {
            } else {
                if (startApp == null || startApp.isEmpty()) {
                    startSession(userId, "scan.xhtml");
                } else {
                    LOGGER.log(Level.FINE, "projectId={0}", userId);
                    startSession(userId, startApp+".xhtml");
                }
                return userId;
            }
        }
        return "";
    }

    /**
     *
     * @param strParam
     */
    public void setParam(String strParam) {
        // nix Tun, die Routine muss nur vorhanden sein -- sonst gibt es eine
        // javax.el.PropretyNotWritableException 
    }

    /**
     *
     * @return
     */
    public Member getMember() {
        if (member != null) {
            LOGGER.log(Level.INFO, "mnr={0}", member.getDisplayName());
        }
        return member;
    }

    /**
     *
     * @param member
     */
    public void setMember(Member member) {
        if (member != null) {
            LOGGER.log(Level.INFO, "strmnr={0}", member.getDisplayName());
            this.member = member;
            setMnr(member.getMglnr());
            showMessage();
        }
        this.member = null;
    }

    /**
     *
     * @return
     */
    public String getScanCode() {
        return scanCode;
    }

    /**
     *
     * @param scanCode
     */
    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }

    @Inject
    private MemberService memberSrv;

    /**
     *
     * @param query
     * @return
     */
    public List<Member> completeMember(String query) {

        List<Member> allMembers = memberSrv.getMembers();
        List<Member> filteredMembers = new ArrayList<>();

        LOGGER.log(Level.INFO, "Query-String: {0}", query);

        // Bei gescannten QR-codes ist der query string 15 ziffern lang
        // für diesen Fall werden die beiden Prüfziffern zur Mitgliedersuche entfernt
        // und das mitglied direkt gesucht
        if (query.length() == 15 && query.matches("\\d+")) {
            setScanCode(query);
            query = query.substring(2, 15);
            for (int i = 0; i < allMembers.size(); i++) {
                Member aktMember = allMembers.get(i);
                if (aktMember.getMglnr().equals(query)) {
                    setMember(aktMember);
                    return new ArrayList<>();
                }
            }
        } else {
            setScanCode(null);
        }

        for (int i = 0; i < allMembers.size(); i++) {
            Member aktMember = allMembers.get(i);
            if (aktMember.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredMembers.add(aktMember);
            }
        }

        if (filteredMembers.size() == 1) {
            // if (filteredMembers.get(0).getMglnr().equals(query)) {
            // Ausweis wurde eingescannt
            setMember(filteredMembers.get(0));
            filteredMembers = new ArrayList<>();
            //}
        }

        return filteredMembers;
    }

    /**
     *
     * @param memberSrv
     */
    public void setMemberSrv(MemberService memberSrv) {
        this.memberSrv = memberSrv;
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
     * @return
     */
    public String getMnrLogin() {
        return mnrLogin;
    }

    /**
     *
     * @param mnrLogin
     */
    public void setMnrLogin(String mnrLogin) {
        this.mnrLogin = mnrLogin;
    }

    /**
     *
     * @return
     */
    public static PersonUser getLoginPerson() {
        return loginPerson;
    }

    /**
     *
     * @param loginPerson
     */
    public static void setLoginPerson(PersonUser loginPerson) {
        ControlBean.loginPerson = loginPerson;
    }

    /**
     *
     * @param text
     * @return
     */
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
     * Aufruf über die URL oder weitergeleitet von der Login-Maske
     *
     * @param strLogin
     * @param redirect
     */
    public void startSession(String strLogin, String redirect) {
        LOGGER.log(Level.INFO, "mnrLogin={0}", strLogin);

        Person person = null;
        if (strLogin != null) {

            strLogin = strLogin.replaceAll(" ", "");

            if (strLogin.length() == 15 && strLogin.matches("\\d+")) {
                strLogin = strLogin.substring(2, 15);
            }

            if (strLogin.length() == 13 && strLogin.matches("\\d+")) {
                //Query q = em.createQuery("SELECT p FROM Person p WHERE p.mglnr = :mglnr");
                Query q = em.createNamedQuery("Person.findByMglnr");
                q.setParameter("mglnr", Mgl2Long(strLogin));

                try {
                    person = (Person) q.getSingleResult();
                } catch (javax.persistence.NoResultException e) {
                    person = null;
                }
            }
        }

        if (person != null) {
            loginPerson = new PersonUser(person);
            if (loginPerson.getPersonMgl().getUserstatus() > 1) {
                setLoginMgl(loginPerson);
                setMnrLogin(null);
                if (redirect != null) {
                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect(redirect);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                showAccessMessage(accesstype.deny, "Sie haben leider nicht die nötigen Zugriffsrechte");
                setMnrLogin(null);
            }
        } else {
            setMnrLogin(null);
        }

    }

    /**
     * Aufruf aus der Login-Maske
     */
    public void startSession() {
        LOGGER.log(Level.INFO, "mnrLogin={0}", this.mnrLogin);
        startSession(this.mnrLogin, "scan.xhtml");
    }

    /**
     *
     * @return
     */
    public boolean getUserRightMin() {
        return getLoginMglUserRights() == 1;
    }

    /**
     *
     * @return
     */
    public boolean getUserRightEinlass() {
        return getLoginMglUserRights() > 1;
    }

    /**
     *
     * @return
     */
    public boolean getUserRightSewobe() {
        return getLoginMglUserRights() > 2;
    }

    /**
     *
     * @return
     */
    public boolean getUserRightAdmin() {
        return getLoginMglUserRights() > 3;
    }

    /**
     *
     * @return
     */
    public boolean getUserRightDev() {
        return getLoginMglUserRights() > 4;
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

        showPersonStatus(new PersonMgl(person));

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
    public void showPersonStatus(PersonMgl pe) {
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
            sl.setMglnr(pe.person.getMglnr());
            em.getTransaction().begin();
            em.persist(sl);
            em.getTransaction().commit();

            LOGGER.info("Scancode = " + scanCode);
            if (scanCode != null && scanCode.length() == 15) {
                if (!pe.getCard().getPrfmglnr().toString().equals(scanCode)) {
                    atype = accesstype.deny;
                    showAccessMessage(atype, "Der eingescannte Ausweis ist ungültig!");
                }
            } else {
                atype = accesstype.doubt;
                showAccessMessage(atype, "Es wurde kein Ausweis eingescannt");
            }

            if (!pe.person.getHauptkategorie().equals("Mitglied")) {
                atype = accesstype.deny;
                showAccessMessage(atype, "" + pe.getFullname(), " ist kein Mitglied sondern " + pe.person.getHauptkategorie() + " mit Status=" + pe.person.getStatus() + ".");
                if (pe.person.getBemerkung() != null) {
                    showAccessMessage(atype, "Bemerkung: ", pe.person.getBemerkung());
                }
                return;
            }

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
}
