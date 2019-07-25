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
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.database.persistence.Card;
import net.ruesse.idc.database.persistence.Person;

/**
 *   * Quick and Dirty TODO umfassend überarbeiten!
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class CardService {

    private final static Logger LOGGER = Logger.getLogger(CardService.class.getName());
    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

    Person person;
    Card c;

    DateFormat dateFormat;

    /**
     *
     * @param person
     */
    public CardService(Person person) {
        this.dateFormat = new SimpleDateFormat("yyyyMMdd");
        this.person = person;
    }

    /**
     *
     */
    public void updateCard() {

        try {
            c = (Card) em.createNamedQuery("Card.findByMglnr")
                    .setParameter("mglnr", person.getMglnr())
                    .getSingleResult();
        } catch (NoResultException e) {
            createCard();
            return;
        }
        c.setIssue(c.getIssue() + 1);
        try {
            c.setDateofissue(dateFormat.parse(getIssueDate()));
        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        c.setPrfmglnr(calculateCheckNr(person.getMglnr(), dateFormat.format(c.getDateofissue())));
        saveCard();
    }

    /**
     *
     */
    private void createCard() {

        c = new Card();
        c.setMglnr(person.getMglnr());
        try {
            c.setDateofissue(dateFormat.parse(getIssueDate()));
        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        c.setIssue(1);
        c.setPrfmglnr(calculateCheckNr(person.getMglnr(), dateFormat.format(c.getDateofissue())));

        saveCard();
    }

    /**
     *
     * @param mglnr
     * @param strDate
     * @return
     */
    private long calculateCheckNr(long mglnr, String strDate) {
        BigInteger bmglnr, bdateofissue, bi2, bi1;
        bmglnr = BigInteger.valueOf(mglnr);
        bdateofissue = new BigInteger(strDate);

        bi1 = bdateofissue.multiply(new BigInteger("10000000000000")).add(bmglnr);
        // 99 - (bi1 % 89)
        bi2 = BigInteger.valueOf(99).add(bi1.mod(BigInteger.valueOf(89)).negate());

        return (bi2.multiply(new BigInteger("10000000000000")).add(bmglnr).longValue());
    }

    /**
     * TODO noch auszuformulieren in der jetzigen Version wird das
     * Eintrittsdatum auch als Ausgabedatum für die Karte verwendet Das soll
     * säter auch für Issue=1 gelten Zusätzliche Karten werden dann aber
     * hochgezählt.
     *
     * @return
     */
    private String getIssueDate() {
        return dateFormat.format(person.getEintritt());
    }

    /**
     *
     */
    private void saveCard() {
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
    }

}
