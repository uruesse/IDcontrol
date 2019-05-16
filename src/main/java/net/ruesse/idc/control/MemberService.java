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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.database.persistence.Person;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean(name = "memberService", eager = true)
@ApplicationScoped
public class MemberService {

    private final static Logger LOGGER = Logger.getLogger(MemberService.class.getName());
    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

    private List<Member> members;

    @PostConstruct
    public void init() {
        refreshAction();
        /*
        members = new ArrayList<>();
        members.add(new Member(0, "0920000700153", "0920000 7 00153 - Ulrich Rüße", "0920000700153 ulrich rüße"));
        members.add(new Member(1, "0920000700154", "0920000 7 00154 - Annette Rüße", "0920000700154 annette rüße"));
        members.add(new Member(2, "0920000700155", "0920000 7 00155 - Sebastian Rüße", "0920000700155 sebastian rüße"));
        members.add(new Member(3, "0920000700156", "0920000 7 00156 - Cynthia Rüße", "0920000700156 cynthia rüße"));
         */
    }

    public List<Member> getMembers() {
        return members;
    }

    public void refreshAction() {
        members = new ArrayList<>();
        int id = 0;

        LOGGER.log(Level.INFO, "Starte Query auf Personen");
        Query q = em.createNamedQuery("Person.findAll");
        List<Person> personlist = q.getResultList();

        LOGGER.log(Level.FINE, "VorForEach");
        for (Person p : personlist) {
            members.add(new Member(id++, p));
        }
    }

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
