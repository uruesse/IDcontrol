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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.ruesse.idc.database.persistence.Person;
import net.ruesse.idc.database.persistence.service.PersonExt;

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
        LOGGER.setLevel(Level.INFO);
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

}
