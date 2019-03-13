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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Entity
@Table(catalog = "", schema = "DLRG")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Beitrag.findAll", query = "SELECT b FROM Beitrag b")
    , @NamedQuery(name = "Beitrag.findById", query = "SELECT b FROM Beitrag b WHERE b.id = :id")
    , @NamedQuery(name = "Beitrag.findByBeitragsposition", query = "SELECT b FROM Beitrag b WHERE b.beitragsposition = :beitragsposition")
    , @NamedQuery(name = "Beitrag.findByBeitragskommentar", query = "SELECT b FROM Beitrag b WHERE b.beitragskommentar = :beitragskommentar")
    , @NamedQuery(name = "Beitrag.findByFaelligStart", query = "SELECT b FROM Beitrag b WHERE b.faelligStart = :faelligStart")
    , @NamedQuery(name = "Beitrag.findByFaelligEnde", query = "SELECT b FROM Beitrag b WHERE b.faelligEnde = :faelligEnde")
    , @NamedQuery(name = "Beitrag.findByAbrechnungsstatus", query = "SELECT b FROM Beitrag b WHERE b.abrechnungsstatus = :abrechnungsstatus")
    , @NamedQuery(name = "Beitrag.findByZahlungsweise", query = "SELECT b FROM Beitrag b WHERE b.zahlungsweise = :zahlungsweise")})
public class Beitrag implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Size(max = 128)
    @Column(length = 128)
    private String beitragsposition;
    @Size(max = 128)
    @Column(length = 128)
    private String beitragskommentar;
    @Column(name = "FAELLIG_START")
    @Temporal(TemporalType.DATE)
    private Date faelligStart;
    @Column(name = "FAELLIG_ENDE")
    @Temporal(TemporalType.DATE)
    private Date faelligEnde;
    @Size(max = 128)
    @Column(length = 128)
    private String abrechnungsstatus;
    @Size(max = 128)
    @Column(length = 128)
    private String zahlungsweise;
    @JoinColumn(name = "FNR", referencedColumnName = "FNR")
    @ManyToOne
    private Family fnr;
    @JoinColumn(name = "MGLNR", referencedColumnName = "MGLNR")
    @ManyToOne
    private Person mglnr;

    public Beitrag() {
    }

    public Beitrag(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeitragsposition() {
        return beitragsposition;
    }

    public void setBeitragsposition(String beitragsposition) {
        this.beitragsposition = beitragsposition;
    }

    public String getBeitragskommentar() {
        return beitragskommentar;
    }

    public void setBeitragskommentar(String beitragskommentar) {
        this.beitragskommentar = beitragskommentar;
    }

    public Date getFaelligStart() {
        return faelligStart;
    }

    public void setFaelligStart(Date faelligStart) {
        this.faelligStart = faelligStart;
    }

    public Date getFaelligEnde() {
        return faelligEnde;
    }

    public void setFaelligEnde(Date faelligEnde) {
        this.faelligEnde = faelligEnde;
    }

    public String getAbrechnungsstatus() {
        return abrechnungsstatus;
    }

    public void setAbrechnungsstatus(String abrechnungsstatus) {
        this.abrechnungsstatus = abrechnungsstatus;
    }

    public String getZahlungsweise() {
        return zahlungsweise;
    }

    public void setZahlungsweise(String zahlungsweise) {
        this.zahlungsweise = zahlungsweise;
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
        if (!(object instanceof Beitrag)) {
            return false;
        }
        Beitrag other = (Beitrag) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Beitrag[ id=" + id + " ]";
    }
    
}
