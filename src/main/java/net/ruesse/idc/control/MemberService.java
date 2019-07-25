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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.database.persistence.service.PersonCache;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ApplicationScoped
@Named("memberService")
public class MemberService {

    private final static Logger LOGGER = Logger.getLogger(MemberService.class.getName());
    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();
    
    @Inject
    PersonCache pc;

    private List<Member> members = null;

    /**
     *
     */
    @PostConstruct
    public void init() {
        //if (members == null) {
            refreshAction();
        //}
    }

    /**
     *
     * @return
     */
    public List<Member> getMembers() {
        return members;
    }

    /**
     *
     */
    public void refreshAction() {
        //PersonCache pc = new PersonCache();
        members = pc.getMembers();
        /*
        members = new ArrayList<>();
        int id = 0;

        LOGGER.log(Level.INFO, "Starte Query auf Personen");
        Query q = em.createNamedQuery("Person.findAll");
        List<Person> personlist = q.getResultList();

        LOGGER.log(Level.FINE, "VorForEach");
        for (Person p : personlist) {
            members.add(new Member(id++, p));
        }
        */
    }

    /**
     *
     * @param strMglnr
     * @return
     */
    public Member findMemberByMglnr(String strMglnr) {
        // Es kann nur ein Ergebnis geben
        LOGGER.log(Level.INFO, "Suche Mglnr: " + strMglnr);

        for (Member m : members) {
            if (m.getMglnr().equals(strMglnr)) {
                return m;
            }
        }

        return null;
    }

}
