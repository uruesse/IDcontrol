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

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.database.persistence.Verein;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class VereinService {

    private static final Logger LOGGER = Logger.getLogger(VereinService.class.getName());
    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

    Verein aktVerein;

    public VereinService() {
        try {
            aktVerein = em.createNamedQuery("Verein.findAll", Verein.class).getSingleResult();
        } catch (NoResultException e) {
            aktVerein = new Verein();
        }
    }

    public Verein getAktVerein() {
        return aktVerein;
    }

    public String getExpFileName() {
        //Verein v = em.createNamedQuery("Verein.findAll", Verein.class).getSingleResult();

        LOGGER.info("aktuell");

        LocalDateTime dt = LocalDateTime.ofInstant(aktVerein.getDatatime().toInstant(), ZoneId.systemDefault());

        String expName = String.format("%07d-%s", aktVerein.getMglnr(), dt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        return expName;
    }

    public String getVereinId() {
        //Verein v = em.createNamedQuery("Verein.findAll", Verein.class).getSingleResult();
        String str = String.format("%07d", aktVerein.getMglnr());
        LOGGER.log(Level.INFO, "aktueller Verein:{0}", str);

        return str;
    }

    public String getFileInfo() {
        //Verein v= em.createNamedQuery("Verein.findAll", Verein.class).getSingleResult();

        if (aktVerein.getFirma() == null || aktVerein.getDatatime() == null) {
            return "Keine Datenbankinfo vorhanden";
        }
        
        DateFormat df;
        df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, Locale.GERMANY);
        LOGGER.log(Level.INFO, "aktuell: {0}, {1}", new Object[]{aktVerein.getFirma(), df.format(aktVerein.getDatatime())});
        return aktVerein.getFirma() + ", " + df.format(aktVerein.getDatatime());
    }
}
