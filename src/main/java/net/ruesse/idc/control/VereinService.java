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
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import net.ruesse.idc.database.persistence.Verein;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class VereinService {

    private static final Logger LOGGER = Logger.getLogger(VereinService.class.getName());
    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME).createEntityManager();

    public String getExpFileName() {
        Verein v = em.createNamedQuery("Verein.findAll", Verein.class).getSingleResult();

        LOGGER.info("aktuell");

        LocalDateTime dt = LocalDateTime.ofInstant(v.getDatatime().toInstant(), ZoneId.systemDefault());

        String expName = String.format("%07d-%s", v.getMglnr(), dt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        return expName;
    }

    public String getFileInfo() {
        Verein v = em.createNamedQuery("Verein.findAll", Verein.class).getSingleResult();
        LOGGER.info("aktuell");

        DateFormat df;
        df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, Locale.GERMANY);

        return v.getFirma() + ", " + df.format(v.getDatatime());
    }
}
