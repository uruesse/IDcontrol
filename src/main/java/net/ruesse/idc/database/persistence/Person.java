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
package net.ruesse.idc.database.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Entity
@Table(catalog = "", schema = "DLRG")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p")
    , @NamedQuery(name = "Person.findByMglnr", query = "SELECT p FROM Person p WHERE p.mglnr = :mglnr")
    , @NamedQuery(name = "Person.findByFnr", query = "SELECT p FROM Person p WHERE p.fnr = :fnr")
    , @NamedQuery(name = "Person.findByFirma", query = "SELECT p FROM Person p WHERE p.firma = :firma")
    , @NamedQuery(name = "Person.findByAnrede", query = "SELECT p FROM Person p WHERE p.anrede = :anrede")
    , @NamedQuery(name = "Person.findByTitel", query = "SELECT p FROM Person p WHERE p.titel = :titel")
    , @NamedQuery(name = "Person.findByNachname", query = "SELECT p FROM Person p WHERE p.nachname = :nachname")
    , @NamedQuery(name = "Person.findByVorname", query = "SELECT p FROM Person p WHERE p.vorname = :vorname")
    , @NamedQuery(name = "Person.findByHauptkategorie", query = "SELECT p FROM Person p WHERE p.hauptkategorie = :hauptkategorie")
    , @NamedQuery(name = "Person.findByGeburtsdatum", query = "SELECT p FROM Person p WHERE p.geburtsdatum = :geburtsdatum")
    , @NamedQuery(name = "Person.findByBemerkung", query = "SELECT p FROM Person p WHERE p.bemerkung = :bemerkung")
    , @NamedQuery(name = "Person.findByStatus", query = "SELECT p FROM Person p WHERE p.status = :status")
    , @NamedQuery(name = "Person.findByEintritt", query = "SELECT p FROM Person p WHERE p.eintritt = :eintritt")
    , @NamedQuery(name = "Person.findByAustritt", query = "SELECT p FROM Person p WHERE p.austritt = :austritt")
    , @NamedQuery(name = "Person.findByKuendigung", query = "SELECT p FROM Person p WHERE p.kuendigung = :kuendigung")
    , @NamedQuery(name = "Person.findByAbweichenderzahler", query = "SELECT p FROM Person p WHERE p.abweichenderzahler = :abweichenderzahler")
    , @NamedQuery(name = "Person.findByZahlungsmodus", query = "SELECT p FROM Person p WHERE p.zahlungsmodus = :zahlungsmodus")})
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Long mglnr;
    private Integer fnr;
    @Size(max = 128)
    @Column(length = 128)
    private String firma;
    @Size(max = 128)
    @Column(length = 128)
    private String anrede;
    @Size(max = 128)
    @Column(length = 128)
    private String titel;
    @Size(max = 128)
    @Column(length = 128)
    private String nachname;
    @Size(max = 128)
    @Column(length = 128)
    private String vorname;
    @Size(max = 128)
    @Column(length = 128)
    private String hauptkategorie;
    @Temporal(TemporalType.DATE)
    private Date geburtsdatum;
    @Size(max = 128)
    @Column(length = 128)
    private String bemerkung;
    @Size(max = 128)
    @Column(length = 128)
    private String status;
    @Temporal(TemporalType.DATE)
    private Date eintritt;
    @Temporal(TemporalType.DATE)
    private Date austritt;
    @Size(max = 128)
    @Column(length = 128)
    private String kuendigung;
    @Size(max = 128)
    @Column(length = 128)
    private String abweichenderzahler;
    @Size(max = 128)
    @Column(length = 128)
    private String zahlungsmodus;
    @OneToMany(mappedBy = "mglnr")
    private Collection<Bankverbindung> bankverbindungCollection;
    @OneToMany(mappedBy = "mglnr")
    private Collection<Contact> contactCollection;
    @OneToMany(mappedBy = "mglnr")
    private Collection<Cv> cvCollection;
    @OneToMany(mappedBy = "mglnr")
    private Collection<Openpost> openpostCollection;
    @OneToMany(mappedBy = "mglnr")
    private Collection<Rechnung> rechnungCollection;
    @OneToMany(mappedBy = "mglnr")
    private Collection<Address> addressCollection;
    @OneToMany(mappedBy = "mglnr")
    private Collection<Beitrag> beitragCollection;

    public Person() {
    }

    public Person(Long mglnr) {
        this.mglnr = mglnr;
    }

    public Long getMglnr() {
        return mglnr;
    }

    public void setMglnr(Long mglnr) {
        this.mglnr = mglnr;
    }

    public Integer getFnr() {
        return fnr;
    }

    public void setFnr(Integer fnr) {
        this.fnr = fnr;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getHauptkategorie() {
        return hauptkategorie;
    }

    public void setHauptkategorie(String hauptkategorie) {
        this.hauptkategorie = hauptkategorie;
    }

    public Date getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(Date geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEintritt() {
        return eintritt;
    }

    public void setEintritt(Date eintritt) {
        this.eintritt = eintritt;
    }

    public Date getAustritt() {
        return austritt;
    }

    public void setAustritt(Date austritt) {
        this.austritt = austritt;
    }

    public String getKuendigung() {
        return kuendigung;
    }

    public void setKuendigung(String kuendigung) {
        this.kuendigung = kuendigung;
    }

    public String getAbweichenderzahler() {
        return abweichenderzahler;
    }

    public void setAbweichenderzahler(String abweichenderzahler) {
        this.abweichenderzahler = abweichenderzahler;
    }

    public String getZahlungsmodus() {
        return zahlungsmodus;
    }

    public void setZahlungsmodus(String zahlungsmodus) {
        this.zahlungsmodus = zahlungsmodus;
    }

    @XmlTransient
    public Collection<Bankverbindung> getBankverbindungCollection() {
        return bankverbindungCollection;
    }

    public void setBankverbindungCollection(Collection<Bankverbindung> bankverbindungCollection) {
        this.bankverbindungCollection = bankverbindungCollection;
    }

    @XmlTransient
    public Collection<Contact> getContactCollection() {
        return contactCollection;
    }

    public void setContactCollection(Collection<Contact> contactCollection) {
        this.contactCollection = contactCollection;
    }

    @XmlTransient
    public Collection<Cv> getCvCollection() {
        return cvCollection;
    }

    public void setCvCollection(Collection<Cv> cvCollection) {
        this.cvCollection = cvCollection;
    }

    @XmlTransient
    public Collection<Openpost> getOpenpostCollection() {
        return openpostCollection;
    }

    public void setOpenpostCollection(Collection<Openpost> openpostCollection) {
        this.openpostCollection = openpostCollection;
    }

    @XmlTransient
    public Collection<Rechnung> getRechnungCollection() {
        return rechnungCollection;
    }

    public void setRechnungCollection(Collection<Rechnung> rechnungCollection) {
        this.rechnungCollection = rechnungCollection;
    }

    @XmlTransient
    public Collection<Address> getAddressCollection() {
        return addressCollection;
    }

    public void setAddressCollection(Collection<Address> addressCollection) {
        this.addressCollection = addressCollection;
    }

    @XmlTransient
    public Collection<Beitrag> getBeitragCollection() {
        return beitragCollection;
    }

    public void setBeitragCollection(Collection<Beitrag> beitragCollection) {
        this.beitragCollection = beitragCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mglnr != null ? mglnr.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person) object;
        if ((this.mglnr == null && other.mglnr != null) || (this.mglnr != null && !this.mglnr.equals(other.mglnr))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Person[ mglnr=" + mglnr + " ]";
    }

    public String getStrMglnr() {
        return String.format("%013d", mglnr);
    }
}
