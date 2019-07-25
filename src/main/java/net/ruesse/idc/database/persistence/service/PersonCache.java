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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.ruesse.idc.application.Constants;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.control.Member;
import net.ruesse.idc.database.persistence.Person;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ApplicationScoped
public class PersonCache {

    //@Inject
    //private transient Logger LOG;
    private static final Logger LOG = Logger.getLogger(PersonCache.class.getName());

    static private List<PersonMgl> allMgl = null;
    static private List<Member> members = null;

    static private String[] allStatus;
    static private String[] allHauptkategorie;

    /**
     *
     */
    public PersonCache() {
        LOG.setLevel(Level.FINE);
        LOG.log(Level.FINE, "");
        if (allMgl == null) {
            refreshAction();
        }
    }

    /**
     *
     */
    //@PostConstruct
    //public void init() {
    //    if (allMgl == null) {
    //        refreshAction();
    //    }
    //}
    /**
     *
     * @return
     */
    public List<PersonMgl> getAllMgl() {
        LOG.log(Level.FINE, "");
        if (allMgl == null) {
            refreshAction();
        }
        return allMgl;
    }

    /**
     *
     * @param allMgl
     */
    public void setAllMgl(List<PersonMgl> allMgl) {
        LOG.log(Level.FINE, "");
        PersonCache.allMgl = allMgl;
    }

    /**
     *
     * @return
     */
    public List<Member> getMembers() {
        LOG.log(Level.FINE, "");
        if (allMgl == null) {
            refreshAction();
        }
        return members;
    }

    /**
     *
     * @param members
     */
    public void setMembers(List<Member> members) {
        LOG.log(Level.FINE, "");
        PersonCache.members = members;
    }

    /**
     *
     * @return
     */
    public String[] getAllStatus() {
        if (allMgl == null) {
            refreshAction();
        }
        return allStatus;
    }

    /**
     *
     * @param allStatus
     */
    public void setAllStatus(String[] allStatus) {
        PersonCache.allStatus = allStatus;
    }

    /**
     *
     * @return
     */
    public String[] getAllHauptkategorie() {
        if (allMgl == null) {
            refreshAction();
        }
        return allHauptkategorie;
    }

    /**
     *
     * @param allHauptkategorie
     */
    public void setAllHauptkategorie(String[] allHauptkategorie) {
        PersonCache.allHauptkategorie = allHauptkategorie;
    }

    /**
     *
     */
    public void invalidate() {
        LOG.log(Level.FINE, "");
        PersonCache.allMgl = null;
        PersonCache.members = null;

        // prüfen, ob das hier klappt.
        // siehe: https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Caching/Cache_API
        Cache cache = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).getCache();
        cache.evictAll();
    }

    /**
     *
     */
    public static void refreshAction() {
        LOG.log(Level.FINE, "");
        PersonCache.allMgl = new ArrayList<>();

        PersonCache.members = new ArrayList<>();
        int id = 0;

        EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

        LOG.log(Level.INFO, "Starte Query auf Personen");
        Query q = em.createNamedQuery("Person.findAll");
        q.setHint("eclipselink.refresh", "true");
        List<Person> personlist = q.getResultList();

        LOG.log(Level.FINE, "VorForEach");

        for (Person p : personlist) {
            PersonMgl pm = new PersonMgl(p);
            PersonCache.allMgl.add(pm);
            PersonCache.members.add(new Member(id++, pm));
        }

        q = em.createNativeQuery("SELECT DISTINCT STATUS FROM IDCREMOTE.PERSON ORDER BY STATUS");
        List<String> list = q.getResultList();
        allStatus = list.stream().toArray(String[]::new);

        q = em.createNativeQuery("SELECT DISTINCT HAUPTKATEGORIE FROM IDCREMOTE.PERSON ORDER BY HAUPTKATEGORIE");
        list = q.getResultList();
        allHauptkategorie = list.stream().toArray(String[]::new);
    }

}
