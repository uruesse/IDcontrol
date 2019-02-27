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
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.ruesse.idc.ressources.MsgBundle.getMessage;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class Person implements Serializable {
    private final static Logger LOGGER = Logger.getLogger(Person.class.getName());

    public String mglnr;
    public String lastname;
    public String firstname;
    public Date birthdate;
    public Date exitdate;
    public String state;
    public int openposts;
    public int openwaterbill;

    public Person() {
        LOGGER.setLevel(Level.INFO);
    }

    public Person(String mglnr, String lastname, String firstname, Date birthdate) {
        this();
        this.mglnr = mglnr;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthdate = birthdate;
    }

    public Person(String mglnr, String lastname, String firstname, Date birthdate, Date exitdate, String state, int openposts, int openwaterbill) {
        this(mglnr,lastname,firstname,birthdate);
        this.exitdate = exitdate;
        this.state = state;
        this.openposts = openposts;
        this.openwaterbill = openwaterbill;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFullname() {
        return firstname + " " + lastname;
    }

    public String getFullurl() {
        return java.text.MessageFormat.format(getMessage("title.fullurl"), mglnr);
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getExitdate() {
        return exitdate;
    }

    public void setExitdate(Date exitdate) {
        this.exitdate = exitdate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getOpenposts() {
        return openposts;
    }

    public void setOpenposts(int openposts) {
        this.openposts = openposts;
    }

    public int getOpenwaterbill() {
        return openwaterbill;
    }

    public void setOpenwaterbill(int openwaterbill) {
        this.openwaterbill = openwaterbill;
    }

    public String getMglnr() {
        return mglnr;
    }

    public void setMglnr(String mglnr) {
        this.mglnr = mglnr;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.mglnr);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (!Objects.equals(this.mglnr, other.mglnr)) {
            return false;
        }
        return true;
    }

}
