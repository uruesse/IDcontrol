/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ruesse.idc.test;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author ulrich
 */
@ManagedBean
@ViewScoped
public class PersondataGridView implements Serializable {

    private List<Person> persons;

    private Person selectedPerson;

    @ManagedProperty("#{personService}")
    private PersonService service;

    @PostConstruct
    public void init() {
        persons = service.createPersons();
    }

    public List<Person> getPersons() {
        System.out.println("Get Persons: " + persons.toString());
        return persons;
    }

    public void setService(PersonService service) {
        this.service = service;
    }

    public Person getSelectedPerson() {
        System.out.println("In Get selected Person");
        if (selectedPerson != null) {
            System.out.println("Get selected Person: " + selectedPerson.mglnr);
        }
        return selectedPerson;
    }

    public void setSelectedPerson(Person selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

}
