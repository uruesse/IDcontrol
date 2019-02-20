/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author ulrich
 */
@ApplicationScoped
@ManagedBean
public class PersonService implements Serializable {

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static List<Person> personlist = null;

    public List<Person> createPersons() {
        if (personlist != null) return personlist;

        personlist = new ArrayList<>();

        try {
            personlist.add(new Person("1122333700001", "Duck", "Dagobert", dateFormat.parse("01.01.1950"), null, "inaktives Mitglied", 0, 0));
            personlist.add(new Person("1122333700002", "Duck", "Daisy", dateFormat.parse("01.04.1950"), null, "aktives Mitglied", 0, 0));
            personlist.add(new Person("1122333700003", "Duck", "Donald", dateFormat.parse("01.09.1960"), null, "aktives Mitglied", 5500, 2250));
            personlist.add(new Person("1122333700004", "Duck", "Tick", dateFormat.parse("01.09.2010"), null, "Mitarbeiter", 0, 0));
            personlist.add(new Person("1122333700005", "Duck", "Trick", dateFormat.parse("01.09.2010"), null, "aktives Mitglied", 0, 0));
            personlist.add(new Person("1122333700006", "Duck", "Track", dateFormat.parse("01.09.2010"), null, "Mitarbeiter", 0, 0));
            personlist.add(new Person("1122333700007", "Gans", "Gustav", dateFormat.parse("01.09.2010"), dateFormat.parse("31.12.2018"), "aktives Mitglied", 0, 0));
            System.out.println("Liste aufgebaut" + personlist.toString());
        } catch (ParseException ex) {
            Logger.getLogger(PersonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return personlist;
    }

    public static String substringAfter(String string, String delimiter) {
        int pos = string.indexOf(delimiter);

        return pos >= 0 ? string.substring(pos + delimiter.length()) : "";
    }

    public Person findPerson(String scantext) {
        Person p;
        String mnr;
        p = null;

        if (scantext != null) {
            mnr = scantext.replaceAll(" ", "");
            System.out.println("in Find Person 1 mnr: " + mnr);

            if (mnr.length() != 13) {
                mnr = substringAfter(scantext, "=");
            }
            System.out.println("in Find Person 2 mnr: " + mnr);
            
            if (personlist == null) createPersons();

            if (personlist !=null && mnr.length() == 13) {
                for (Person pr : personlist) {
                    if (pr.getMglnr().equals(mnr)) {
                        p=pr;
                    }   
                }

            }
        }
        return p;
    }
}
