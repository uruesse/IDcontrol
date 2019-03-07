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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Entity
@Table(name = "BANKVERBINDUNG")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bankverbindung.findAll", query = "SELECT b FROM Bankverbindung b")
    , @NamedQuery(name = "Bankverbindung.findById", query = "SELECT b FROM Bankverbindung b WHERE b.id = :id")
    , @NamedQuery(name = "Bankverbindung.findByIban", query = "SELECT b FROM Bankverbindung b WHERE b.iban = :iban")
    , @NamedQuery(name = "Bankverbindung.findByBic", query = "SELECT b FROM Bankverbindung b WHERE b.bic = :bic")
    , @NamedQuery(name = "Bankverbindung.findByKontoinhaber", query = "SELECT b FROM Bankverbindung b WHERE b.kontoinhaber = :kontoinhaber")
    , @NamedQuery(name = "Bankverbindung.findByMandatsreferenz", query = "SELECT b FROM Bankverbindung b WHERE b.mandatsreferenz = :mandatsreferenz")
    , @NamedQuery(name = "Bankverbindung.findByMandatVorhanden", query = "SELECT b FROM Bankverbindung b WHERE b.mandatVorhanden = :mandatVorhanden")})
public class Bankverbindung implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Size(max = 128)
    @Column(name = "IBAN")
    private String iban;
    @Size(max = 128)
    @Column(name = "BIC")
    private String bic;
    @Size(max = 128)
    @Column(name = "KONTOINHABER")
    private String kontoinhaber;
    @Size(max = 128)
    @Column(name = "MANDATSREFERENZ")
    private String mandatsreferenz;
    @Column(name = "MANDAT_VORHANDEN")
    private Boolean mandatVorhanden;
    @JoinColumn(name = "FNR", referencedColumnName = "FNR")
    @ManyToOne
    private Family fnr;
    @JoinColumn(name = "MGLNR", referencedColumnName = "MGLNR")
    @ManyToOne
    private Person mglnr;

    public Bankverbindung() {
    }

    public Bankverbindung(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getKontoinhaber() {
        return kontoinhaber;
    }

    public void setKontoinhaber(String kontoinhaber) {
        this.kontoinhaber = kontoinhaber;
    }

    public String getMandatsreferenz() {
        return mandatsreferenz;
    }

    public void setMandatsreferenz(String mandatsreferenz) {
        this.mandatsreferenz = mandatsreferenz;
    }

    public Boolean getMandatVorhanden() {
        return mandatVorhanden;
    }

    public void setMandatVorhanden(Boolean mandatVorhanden) {
        this.mandatVorhanden = mandatVorhanden;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bankverbindung)) {
            return false;
        }
        Bankverbindung other = (Bankverbindung) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Bankverbindung[ id=" + id + " ]";
    }
    
}
