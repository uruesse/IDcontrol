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
package net.ruesse.idc.test;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ApplicationScoped
@ManagedBean
public class PersonService implements Serializable {
    private final static Logger LOGGER = Logger.getLogger(PersonService.class.getName());

    /**
     * 
     */
    public PersonService() {
        LOGGER.setLevel(Level.INFO);
    }

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static List<Person> personlist = null;

    /**
     *
     * @return
     */
    public List<Person> createPersons() {
        if (personlist != null) {
            return personlist;
        }

        personlist = new ArrayList<>();

        try {
            personlist.add(new Person("1122333700001", "Duck", "Dagobert", dateFormat.parse("01.01.1950"), null, "inaktives Mitglied", 0, 0));
            personlist.add(new Person("1122333700002", "Duck", "Daisy", dateFormat.parse("01.04.1950"), null, "aktives Mitglied", 0, 0));
            personlist.add(new Person("1122333700003", "Duck", "Donald", dateFormat.parse("01.09.1960"), null, "aktives Mitglied", 5500, 2250));
            personlist.add(new Person("1122333700004", "Duck", "Tick", dateFormat.parse("01.09.2010"), null, "Mitarbeiter", 0, 0));
            personlist.add(new Person("1122333700005", "Duck", "Trick", dateFormat.parse("01.09.2010"), null, "aktives Mitglied", 0, 0));
            personlist.add(new Person("1122333700006", "Duck", "Track", dateFormat.parse("01.09.2010"), null, "Mitarbeiter", 0, 0));
            personlist.add(new Person("1122333700007", "Gans", "Gustav", dateFormat.parse("01.09.2010"), dateFormat.parse("31.12.2018"), "aktives Mitglied", 0, 0));
            LOGGER.fine("Liste aufgebaut" + personlist.toString());
        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return personlist;
    }

    /**
     *
     * @param string
     * @param delimiter
     * @return
     */
    public static String substringAfter(String string, String delimiter) {
        int pos = string.indexOf(delimiter);

        return pos >= 0 ? string.substring(pos + delimiter.length()) : "";
    }

    /**
     *
     * @param scantext
     * @return
     */
    public Person findPerson(String scantext) {
        Person p;
        String mnr;
        p = null;

        if (scantext != null) {
            mnr = scantext.replaceAll(" ", "");
            LOGGER.log(Level.FINE, "1 mnr: {0}", mnr);

            if (mnr.length() != 13) {
                mnr = substringAfter(scantext, "=");
            }
            LOGGER.log(Level.FINE, "2 mnr: {0}", mnr);

            if (personlist == null) {
                createPersons();
            }

            if (personlist != null && mnr.length() == 13) {
                for (Person pr : personlist) {
                    if (pr.getMglnr().equals(mnr)) {
                        p = pr;
                    }
                }

            }
        }
        return p;
    }
}
