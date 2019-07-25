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

import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.application.Constants;
import net.ruesse.idc.database.persistence.Person;
import net.ruesse.idc.database.persistence.Userpref;

/**
 * Klasse zur Behandlung von Personen, die die Software benutzen im Wesentlichen
 * werden die Benutzerberechtigungen und -einstellungen verwaltet.
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class PersonUser {

    private final static Logger LOGGER = Logger.getLogger(PersonUser.class.getName());
    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

    PersonMgl personMgl;
    Userpref userPref;

    /**
     *
     * @param person
     */
    public PersonUser(Person person) {
        personMgl = new PersonMgl(person);

        try {
            Query q = em.createNamedQuery("Userpref.findByMglnr", Userpref.class);
            q.setParameter("mglnr", person.getMglnr());
            userPref = (Userpref) q.getSingleResult();
        } catch (NoResultException e) {
            userPref = new Userpref();
            userPref.setMglnr(person.getMglnr());
            userPref.setPrinter("PDF");
            userPref.setPdfheight(1024);
            saveUserPref();
        }
        LOGGER.info("UserPref=" + userPref.getMglnr());
    }

    /**
     *
     * @return
     */
    public PersonMgl getPersonMgl() {
        return personMgl;
    }

    /**
     *
     * @param personMgl
     */
    public void setPersonMgl(PersonMgl personMgl) {
        this.personMgl = personMgl;
    }

    /**
     *
     * @return
     */
    public int getUserRights() {
        if (personMgl != null) {
            if (personMgl.isMitarbeiter()) {
                return personMgl.getUserstatus();
            }
        }
        return 0;
    }

    /**
     *
     * @param printer
     */
    public void setPrinter(String printer) {
        userPref.setPrinter(printer);
        saveUserPref();
    }

    /**
     *
     * @return
     */
    public String getPrinter() {
        return userPref.getPrinter();
    }

    /**
     *
     * @param pdfhight
     */
    public void setPdfHight(Integer pdfhight) {
        userPref.setPdfheight(pdfhight);
        saveUserPref();
    }

    /**
     *
     * @return
     */
    public Integer getPdfHight() {
        return userPref.getPdfheight();
    }

    /**
     *
     * @param user
     */
    public void setWebDavUser(String user) {
        userPref.setWebdavuser(user);
        saveUserPref();
        new WebDavService().setWebserviceAvailable(false);
    }

    /**
     *
     * @return
     */
    public String getWebDavUser() {
        return userPref.getWebdavuser();
    }

    /**
     *
     * @param password
     */
    public void setWebDavPassword(String password) {
        if (!password.isEmpty())  {
            if (password.matches(" *")) {
                userPref.setWebdavpwd("");
            } else {
                userPref.setWebdavpwd(password);
            }
            saveUserPref();
            new WebDavService().setWebserviceAvailable(false);
        }
    }

    /**
     *
     * @return
     */
    public String getWebDavPassword() {
        return userPref.getWebdavpwd();
    }

    /**
     *
     */
    private void saveUserPref() {
        em.getTransaction().begin();
        em.persist(userPref);
        em.getTransaction().commit();
    }

}
