/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ruesse.idc.test;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author ulrich
 */
public class Person implements Serializable {

    public String mglnr;
    public String lastname;
    public String firstname;
    public Date birthdate;
    public Date exitdate;
    public String state;
    public int openposts;
    public int openwaterbill;

    public Person() {
    }

    public Person(String mglnr, String lastname, String firstname, Date birthdate) {
        this.mglnr = mglnr;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthdate = birthdate;
    }

    public Person(String mglnr, String lastname, String firstname, Date birthdate, Date exitdate, String state, int openposts, int openwaterbill) {
        this.mglnr = mglnr;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthdate = birthdate;
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
        //return "http://mirakulix.ruesse.net:8080/IDControl/faces/index.xhtml?mnr=" + mglnr;
        return "https://dlrgrs.eorga.de/IDControl/faces/index.xhtml?mnr=" + mglnr;
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
