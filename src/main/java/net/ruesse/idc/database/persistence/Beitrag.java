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
@Table(catalog = "", schema = "DLRG")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Beitrag.findAll", query = "SELECT b FROM Beitrag b")
    , @NamedQuery(name = "Beitrag.findById", query = "SELECT b FROM Beitrag b WHERE b.id = :id")
    , @NamedQuery(name = "Beitrag.findByFnr", query = "SELECT b FROM Beitrag b WHERE b.fnr = :fnr")
    , @NamedQuery(name = "Beitrag.findByBeitragsposition", query = "SELECT b FROM Beitrag b WHERE b.beitragsposition = :beitragsposition")
    , @NamedQuery(name = "Beitrag.findByAbrechnungsstatus", query = "SELECT b FROM Beitrag b WHERE b.abrechnungsstatus = :abrechnungsstatus")
    , @NamedQuery(name = "Beitrag.findByZahlungsweise", query = "SELECT b FROM Beitrag b WHERE b.zahlungsweise = :zahlungsweise")})
public class Beitrag implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    private Integer fnr;
    @Size(max = 128)
    @Column(length = 128)
    private String beitragsposition;
    @Size(max = 128)
    @Column(length = 128)
    private String abrechnungsstatus;
    @Size(max = 128)
    @Column(length = 128)
    private String zahlungsweise;
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

    public Integer getFnr() {
        return fnr;
    }

    public void setFnr(Integer fnr) {
        this.fnr = fnr;
    }

    public String getBeitragsposition() {
        return beitragsposition;
    }

    public void setBeitragsposition(String beitragsposition) {
        this.beitragsposition = beitragsposition;
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
