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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Entity
@Table(catalog = "", schema = "DLRG")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rechnung.findAll", query = "SELECT r FROM Rechnung r")
    , @NamedQuery(name = "Rechnung.findByFnr", query = "SELECT r FROM Rechnung r WHERE r.fnr = :fnr")
    , @NamedQuery(name = "Rechnung.findByRechnungsdatum", query = "SELECT r FROM Rechnung r WHERE r.rechnungsdatum = :rechnungsdatum")
    , @NamedQuery(name = "Rechnung.findByRechnungsnummer", query = "SELECT r FROM Rechnung r WHERE r.rechnungsnummer = :rechnungsnummer")
    , @NamedQuery(name = "Rechnung.findByRechnungssumme", query = "SELECT r FROM Rechnung r WHERE r.rechnungssumme = :rechnungssumme")})
public class Rechnung implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer fnr;
    @Temporal(TemporalType.DATE)
    private Date rechnungsdatum;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Long rechnungsnummer;
    private Integer rechnungssumme;
    @JoinColumn(name = "MGLNR", referencedColumnName = "MGLNR")
    @ManyToOne
    private Person mglnr;

    public Rechnung() {
    }

    public Rechnung(Long rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }

    public Integer getFnr() {
        return fnr;
    }

    public void setFnr(Integer fnr) {
        this.fnr = fnr;
    }

    public Date getRechnungsdatum() {
        return rechnungsdatum;
    }

    public void setRechnungsdatum(Date rechnungsdatum) {
        this.rechnungsdatum = rechnungsdatum;
    }

    public Long getRechnungsnummer() {
        return rechnungsnummer;
    }

    public void setRechnungsnummer(Long rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }

    public Integer getRechnungssumme() {
        return rechnungssumme;
    }

    public void setRechnungssumme(Integer rechnungssumme) {
        this.rechnungssumme = rechnungssumme;
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
