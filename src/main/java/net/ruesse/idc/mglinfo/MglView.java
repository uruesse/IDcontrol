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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.ruesse.idc.control.ApplicationControlBean;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.control.CardService;
import net.ruesse.idc.database.persistence.Auswahl;
import net.ruesse.idc.database.persistence.Person;
import net.ruesse.idc.database.persistence.service.PersonMgl;
import net.ruesse.idc.control.Constants;
import net.ruesse.idc.report.PrintSupport;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@SessionScoped
@Named
public class MglView implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(MglView.class.getName());
    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

    private static final long serialVersionUID = 1L;

    static private List<PersonMgl> allMgl = null;
    static private List<PersonMgl> filteredMgl;
    static private List<PersonMgl> selectedMgl;

    static private String[] allStatus;
    static private String[] allHauptkategorie;

    private PersonMgl selectedPerson;

    /**
     *
     */
    public MglView() {
        LOGGER.setLevel(Level.INFO);
    }

    /**
     *
     */
    @PostConstruct
    public void init() {
        if (allMgl == null) {
            refreshAction();
        }
    }

    /**
     *
     */
    public void refreshAction() {
        allMgl = new ArrayList<>();
        filteredMgl = new ArrayList<>();
        selectedMgl = new ArrayList<>();

        LOGGER.log(Level.INFO, "Starte Query auf Personen");
        Query q = em.createNamedQuery("Person.findAll");
        List<Person> personlist = q.getResultList();

        LOGGER.log(Level.FINE, "VorForEach");
        personlist.stream().map((p) -> {
            LOGGER.log(Level.FINE, "mnr: {0}", p.getMglnr());
            return p;
        }).forEachOrdered((p) -> {
            allMgl.add(new PersonMgl(p));
        });

        q = em.createNativeQuery("SELECT DISTINCT STATUS FROM IDCREMOTE.PERSON  ORDER BY STATUS");
        List<String> list = q.getResultList();
        allStatus = list.stream().toArray(String[]::new);

        q = em.createNativeQuery("SELECT DISTINCT HAUPTKATEGORIE FROM IDCREMOTE.PERSON  ORDER BY HAUPTKATEGORIE");
        list = q.getResultList();
        allHauptkategorie = list.stream().toArray(String[]::new);
    }

    /**
     *
     * @return
     */
    public String[] getAllStatus() {
        return allStatus;
    }

    /**
     *
     * @return
     */
    public String[] getAllHauptkategorie() {
        return allHauptkategorie;
    }

    /**
     *
     * @return
     */
    public List<PersonMgl> getSelectedMgl() {
        return selectedMgl;
    }

    /**
     *
     * @param selectedMgl
     */
    public void setSelectedMgl(List<PersonMgl> selectedMgl) {
        MglView.selectedMgl = selectedMgl;
    }

    /**
     *
     * @return
     */
    public List<PersonMgl> getFilteredMgl() {
        return filteredMgl;
    }

    /**
     *
     * @param filteredMgl
     */
    public void setFilteredMgl(List<PersonMgl> filteredMgl) {
        MglView.filteredMgl = filteredMgl;
    }

    /**
     *
     * @return
     */
    public List<PersonMgl> getAllMgl() {
        return allMgl;
    }

    /**
     *
     * @param allMgl
     */
    public void setAllMgl(List<PersonMgl> allMgl) {
        MglView.allMgl = allMgl;
    }

    /**
     *
     * @return
     */
    public List<PersonMgl> getMglliste() {
        LOGGER.log(Level.FINE, "Persons: {0}", allMgl.toString());
        return allMgl;
    }

    /**
     *
     * @return
     */
    public PersonMgl getSelectedPerson() {
        LOGGER.fine("aufgerufen");
        if (selectedPerson != null) {
            LOGGER.log(Level.FINE, "Mitgliedsnummer: {0}", selectedPerson.person.getMglnr());
        }
        return selectedPerson;
    }

    /**
     *
     * @return
     */
    public String getStrMglnrOfSelectedPerson() {
        return String.format("%013d", selectedPerson.person.getMglnr());
    }

    /**
     *
     * @param selectedPerson
     */
    public void setSelectedPerson(PersonMgl selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

    /**
     *
     * @return
     */
    public String confirmationMessage() {
        return "Es werden nur Ausweise für Mitglieder gedruckt, deren Status Aktiv ist. Jetzt drucken?";
    }

    /**
     *
     */
    public void printAction() {
        String REPORT = "IDCard-front";

        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM IDCLOCAL.AUSWAHL").executeUpdate();
        // Auswahl füllen
        for (PersonMgl p : selectedMgl) {
            if (p.person.getStatus().equals("Aktiv")) {
                Auswahl a = new Auswahl(p.person.getMglnr());
                try {
                    em.persist(a);
                } catch (EntityExistsException e) {
                    // ignorieren
                }
            }
        }
        em.getTransaction().commit();

        // Versionsfortschreibung
        for (PersonMgl p : selectedMgl) {
            CardService cardService = new CardService(p.person);
            cardService.updateCard();
        }

        PrintSupport.printReport(REPORT, em, 1, ApplicationControlBean.getStaticKartendrucker());

        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM IDCLOCAL.AUSWAHL").executeUpdate();
        em.getTransaction().commit();

        addMessage("Fertig", "Druckauftrag erledigt");
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
}
