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
package net.ruesse.idc.database.persistence.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.ruesse.idc.control.ControlBean;
import net.ruesse.idc.database.persistence.Beitrag;
import net.ruesse.idc.database.persistence.Cv;
import net.ruesse.idc.database.persistence.Family;
import net.ruesse.idc.database.persistence.Offenerechnungen;
import net.ruesse.idc.database.persistence.Person;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
@ViewScoped
public class PersonExt {

    private final static Logger LOGGER = Logger.getLogger(ControlBean.class.getName());
    public static final String PERSISTENCE_UNIT_NAME = "net.ruesse.IDControl.PU";
    private static EntityManagerFactory factory;

    static final long MSPERYEAR = ((long) 365 * 24 * 60 * 60 * 1000);

    public Person person;

    private boolean mitarbeiter;
    private boolean wassergeld;

    public boolean isMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(boolean mitarbeiter) {
        this.mitarbeiter = mitarbeiter;
    }

    public boolean isWassergeld() {
        return wassergeld;
    }

    public void setWassergeld(boolean wassergeld) {
        this.wassergeld = wassergeld;
    }

    public PersonExt(Person person) {
        this.person = person;
        LOGGER.setLevel(Level.INFO);
        checkMitarbeiterStatus();
        checkWassergeldStatus();
    }

    public Person getPerson() {
        return this.person;
    }

    public String getFullname() {
        return person.getVorname() + " " + person.getNachname();
    }

    public String getStrMglnr() {
        return String.format("%013d", person.getMglnr());
    }

    public String formatMglnr(long mglnr) {
        String str = String.format("%013d", mglnr);
        return str.substring(0, 7) + " " + str.substring(7, 8) + " " + str.substring(8, 13);
    }

    public String getStrFMglnr() {
        return formatMglnr(person.getMglnr());
    }

    public String getStrFNr() {
        Collection<Person> family;
        if (person.getFnr() != null) {
            return (person.getFnr()).getFnr().toString();
        } else {
            return null;
        }
    }

    public boolean isAustritt() {
        return person.getAustritt() != null;
    }

    public boolean isRechnung() {
        Collection<Offenerechnungen> or;
        or = person.getOffenerechnungenCollection();
        return !or.isEmpty();
    }

        public boolean isBeitrag() {
        Collection<Beitrag> be;
        be = person.getBeitragCollection();
        return !be.isEmpty();
    }

    
    /**
     * Berechnet das aktuelle Alter ACHTUNG macht keine Prüfung ob
     * eingangsparameter valide ist.
     *
     * @param d
     * @return
     */
    private int calcAge(Date d) {
        Date now = new Date();
        return (int) ((now.getTime() - d.getTime()) / MSPERYEAR);
    }

    public boolean isGeburtsdatum() {
        return person.getGeburtsdatum() != null;
    }

    public int getAge() {
        long age = 0;
        if (person.getGeburtsdatum() != null) {
            age = calcAge(person.getGeburtsdatum());
            LOGGER.log(Level.FINE, "Alter: {0}", age);
        }
        return (int) age;
    }

    public String getStrAge() {
        //Irgendwann mal die führenden nullen durch &nbsp; ersetzten. Das wird aber per default angeszeigt und ansonsten nicht richtig sortiert.

        if (person.getGeburtsdatum() != null) {
            return String.format("%03d", calcAge(person.getGeburtsdatum()));
        }
        return null;
    }

    public boolean isBemerkung() {
        return person.getBemerkung() != null;
    }

    public boolean isFamilie() {
        return person.getFnr() != null;
    }

    public boolean isLastschrift() {
        return "Lastschrift".equals(person.getZahlungsmodus());
    }

    private void checkMitarbeiterStatus() {
        LOGGER.log(Level.FINE, "Status: {0}", person.getStatus());
        if ("Aktiv".equals(person.getStatus())) {
            Date now = new Date();
            /* suche Mitarbeiter im aktuellen Lebenslaufeintrag */
            Collection<Cv> cv = person.getCvCollection();
            try {
                for (Cv c : cv) {
                    LOGGER.log(Level.FINE, "c: {0} {1} {2} {3}", new Object[]{c.getCvkey(), c.getCvvalue(), c.getValidfrom(), c.getValidto()});
                    if ("Funktionen".equals(c.getCvkey())) {
                        if (now.after(c.getValidfrom()) && (c.getValidto() == null || now.before(c.getValidto()))) {
                            setMitarbeiter(true);
                        }
                    }
                }
            } catch (java.util.NoSuchElementException e) {
            }
        }
    }

    private void checkWassergeldStatus() {
        Date now = new Date();
        Collection<Beitrag> be = person.getBeitragCollection();
        for (Beitrag b : be) {
            LOGGER.log(Level.FINE, "b: {0} {1} {2}", new Object[]{b.getBeitragsposition(), b.getFaelligStart(), b.getFaelligEnde()});
            if ("Wassergeld".equals(b.getBeitragsposition())) {
                if (now.getYear() == b.getFaelligStart().getYear() || (now.getYear() + 1) == b.getFaelligStart().getYear() && now.getYear() < b.getFaelligEnde().getYear()) {
                    setWassergeld(true);
                }
            }
        }
    }

    public String getState() {
        LOGGER.log(Level.FINE, "Status: {0}", person.getStatus());
        if (isMitarbeiter()) {
            return "Mitarbeiter";
        } else if (isWassergeld()) {
            return "aktiv";
        } else {
            return "inaktiv";
        }
    }

    public Person getFremdzahlerPerson() {

        Person fz;

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        Query q = em.createNamedQuery("Person.findByMglnr");
        q.setParameter("mglnr", person.getFremdzahler());
        try {
            fz = (Person) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return fz;
    }

    public String getFremdzahlerInfo() {
        String str = "Fremdzahler unbekannt";
        Person fz = getFremdzahlerPerson();
        if (fz != null) {
            str = "[";
            str += formatMglnr(fz.getMglnr());
            str += "] " + fz.getVorname() + " " + fz.getNachname();
        }
        return str;
    }

    public String getOpenbills() {
        String str = "";
        int summe = 0;
        int count = 0;
        int lastschriften = 0;

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        Collection<Offenerechnungen> openbills = null;

        Collection<Person> family;
        if (person.getFnr() != null) {
            Family f = person.getFnr();
            family = f.getPersonCollection();
        } else {
            family = new ArrayList() {
                {
                    add(person);
                }
            };
        }

        for (Person p : family) {
            if (p.getAbweichenderzahler() || p.getFremdzahler() != null) {
                Person fz = getFremdzahlerPerson();

                if (fz != null) {
                    if (openbills == null) {
                        openbills = fz.getOffenerechnungenCollection();
                    } else {
                        openbills.addAll(fz.getOffenerechnungenCollection());
                    }
                }
            } else {
                if (openbills == null) {
                    openbills = p.getOffenerechnungenCollection();
                } else {
                    openbills.addAll(p.getOffenerechnungenCollection());
                }
            }
        }

        if (openbills != null) {
            for (Offenerechnungen ob : openbills) {
                LOGGER.log(Level.FINE, "offene Rechnungen: {0} {1} {2}", new Object[]{ob.getRechnungsnummer(), ob.getRechnungssumme(), ob.getRechnungsdatum()});
                summe += ob.getRechnungssumme();
                //str += " [" + ob.getRechnungsnummer() + "]";
                count += 1;
                if ("Lastschrift".equals(ob.getZahlmodus())) {
                    lastschriften += ob.getRechnungssumme();
                }
            }

            if (summe > 0) {
                if (count == 1) {
                    str = count + " offene Rechnung über ";
                } else {
                    str = count + " offene Rechnungen über insgesamt ";
                }
                str += String.format("%,.2f", (double) summe / 100);
                str += " €";
                if (lastschriften == summe) {
                    str += ", Gesamtbetrag wird abgebucht";
                } else if (lastschriften > 0) {
                    str += ", davon werden ";
                    str += String.format("%,.2f", (double) lastschriften / 100);
                    str += " € abgebucht.";
                }
            }
        }
        return str;
    }

}
