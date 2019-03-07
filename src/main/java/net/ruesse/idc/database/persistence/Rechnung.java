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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Entity
@Table(name = "RECHNUNG")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rechnung.findAll", query = "SELECT r FROM Rechnung r")
    , @NamedQuery(name = "Rechnung.findByRechnungsnummer", query = "SELECT r FROM Rechnung r WHERE r.rechnungsnummer = :rechnungsnummer")
    , @NamedQuery(name = "Rechnung.findByRechnungsdatum", query = "SELECT r FROM Rechnung r WHERE r.rechnungsdatum = :rechnungsdatum")
    , @NamedQuery(name = "Rechnung.findByRechnungssumme", query = "SELECT r FROM Rechnung r WHERE r.rechnungssumme = :rechnungssumme")
    , @NamedQuery(name = "Rechnung.findByZahlmodus", query = "SELECT r FROM Rechnung r WHERE r.zahlmodus = :zahlmodus")
    , @NamedQuery(name = "Rechnung.findByZahlungsziel", query = "SELECT r FROM Rechnung r WHERE r.zahlungsziel = :zahlungsziel")})
public class Rechnung implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "RECHNUNGSNUMMER")
    private Long rechnungsnummer;
    @Column(name = "RECHNUNGSDATUM")
    @Temporal(TemporalType.DATE)
    private Date rechnungsdatum;
    @Column(name = "RECHNUNGSSUMME")
    private Integer rechnungssumme;
    @Size(max = 128)
    @Column(name = "ZAHLMODUS")
    private String zahlmodus;
    @Column(name = "ZAHLUNGSZIEL")
    @Temporal(TemporalType.DATE)
    private Date zahlungsziel;
    @JoinColumn(name = "FNR", referencedColumnName = "FNR")
    @ManyToOne
    private Family fnr;
    @JoinColumn(name = "MGLNR", referencedColumnName = "MGLNR")
    @ManyToOne
    private Person mglnr;

    public Rechnung() {
    }

    public Rechnung(Long rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }

    public Long getRechnungsnummer() {
        return rechnungsnummer;
    }

    public void setRechnungsnummer(Long rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }

    public Date getRechnungsdatum() {
        return rechnungsdatum;
    }

    public void setRechnungsdatum(Date rechnungsdatum) {
        this.rechnungsdatum = rechnungsdatum;
    }

    public Integer getRechnungssumme() {
        return rechnungssumme;
    }

    public void setRechnungssumme(Integer rechnungssumme) {
        this.rechnungssumme = rechnungssumme;
    }

    public String getZahlmodus() {
        return zahlmodus;
    }

    public void setZahlmodus(String zahlmodus) {
        this.zahlmodus = zahlmodus;
    }

    public Date getZahlungsziel() {
        return zahlungsziel;
    }

    public void setZahlungsziel(Date zahlungsziel) {
        this.zahlungsziel = zahlungsziel;
    }

    public Family getFnr() {
        return fnr;
    }

    public void setFnr(Family fnr) {
        this.fnr = fnr;
    }

    public Person getMglnr() {
        return mglnr;
    }

    public void setMglnr(Person mglnr) {
        this.mglnr = mglnr;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rechnungsnummer != null ? rechnungsnummer.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rechnung)) {
            return false;
        }
        Rechnung other = (Rechnung) object;
        if ((this.rechnungsnummer == null && other.rechnungsnummer != null) || (this.rechnungsnummer != null && !this.rechnungsnummer.equals(other.rechnungsnummer))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Rechnung[ rechnungsnummer=" + rechnungsnummer + " ]";
    }
    
}
