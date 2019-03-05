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

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import net.ruesse.idc.control.ControlBean;
import net.ruesse.idc.database.persistence.Cv;
import net.ruesse.idc.database.persistence.Openpost;
import net.ruesse.idc.database.persistence.Person;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
@ViewScoped
public class PersonExt {

    private final static Logger LOGGER = Logger.getLogger(ControlBean.class.getName());
   
    public Person person;

    public PersonExt(Person person) {
        this.person = person;
        LOGGER.setLevel(Level.FINE);
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

    public int getAge() {
        Date now = new Date();
                long age = (now.getTime() - person.getGeburtsdatum().getTime()) / 365 / 24 / 60 / 60 / 1000;
        LOGGER.log(Level.FINE, "Alter: {0}", age);
        return (int) age;
    }

    public String getState() {
        String status = "inaktiv";
        LOGGER.log(Level.FINE, "Status: {0}", person.getStatus());
        if ("Aktiv".equals(person.getStatus())) {
            Collection<Cv> cv = person.getCvCollection();
            Date now = new Date();
            try {
                for (Cv c : cv) {
                    LOGGER.log(Level.FINE, "c: {0} {1} {2} {3}", new Object[]{c.getCvkey(), c.getCvvalue(), c.getValidfrom(), c.getValidto()});
                    if ("Funktionen".equals(c.getCvkey())) {
                        if (now.after(c.getValidfrom()) && (c.getValidto() == null || now.before(c.getValidto()))) {
                            status = "Mitarbeiter";
                        }
                    }
                }
            } catch (java.util.NoSuchElementException e) {
            }
        }
        return status;
    }

    public int getOpenwaterbill() {
        return 0;
    }

    Openpost o = null;

    public int getOpenposts() {
        if (o == null) {
            Collection<Openpost> op = person.getOpenpostCollection();
            try {
                o = op.iterator().next();
            } catch (java.util.NoSuchElementException e) {
                return 0;
            }
        }
        return o.getSummevorjahr() + o.getSummeaktjahr();
    }

    public int getSummevorjahr() {
        if (o == null) {
            getOpenposts();
        }
        return o.getSummevorjahr();
    }

    public int getSummeaktjahr() {
        if (o == null) {
            getOpenposts();
        }
        return o.getSummeaktjahr();
    }
}
