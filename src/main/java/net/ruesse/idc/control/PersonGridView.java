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

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import net.ruesse.idc.database.persistence.Person;
import net.ruesse.idc.database.persistence.service.PersonService;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
@ViewScoped
public class PersonGridView implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(PersonGridView.class.getName());

    private List<Person> persons;

    private Person selectedPerson;

    @ManagedProperty("#{personService}")
    private PersonService service;
    
        /**
     * 
     */
    @PostConstruct
    public void init() {
        persons = service.createPersons();
    }


    /**
     * 
     */
    public PersonGridView() {
        LOGGER.setLevel(Level.INFO);
    }

    /**
     * 
     * @return 
     */
    public List<Person> getPersons() {
        LOGGER.log(Level.FINE, "Persons: {0}", persons.toString());
        return persons;
    }

    /**
     * 
     * @param service 
     */
    public void setService(PersonService service) {
        this.service = service;
    }

    /**
     * 
     * @return 
     */
    public Person getSelectedPerson() {
        LOGGER.fine("aufgerufen");
        if (selectedPerson != null) {
            LOGGER.log(Level.FINE, "Mitgliedsnummer: {0}", selectedPerson.getMglnr());
        }
        return selectedPerson;
    }

    /**
     * 
     * @param selectedPerson 
     */
    public void setSelectedPerson(Person selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

}
