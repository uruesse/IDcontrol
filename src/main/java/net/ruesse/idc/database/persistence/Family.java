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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Entity
@Table(catalog = "", schema = "IDCREMOTE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Family.findAll", query = "SELECT f FROM Family f")
    , @NamedQuery(name = "Family.findByFnr", query = "SELECT f FROM Family f WHERE f.fnr = :fnr")})
public class Family implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Integer fnr;
    @OneToMany(mappedBy = "fnr")
    private Collection<Bankverbindung> bankverbindungCollection;
    @OneToMany(mappedBy = "fnr")
    private Collection<Person> personCollection;
    @OneToMany(mappedBy = "fnr")
    private Collection<Rechnung> rechnungCollection;
    @OneToMany(mappedBy = "fnr")
    private Collection<Offenerechnungen> offenerechnungenCollection;
    @OneToMany(mappedBy = "fnr")
    private Collection<Beitrag> beitragCollection;

    public Family() {
    }

    public Family(Integer fnr) {
        this.fnr = fnr;
    }

    public Integer getFnr() {
        return fnr;
    }

    public void setFnr(Integer fnr) {
        this.fnr = fnr;
    }

    @XmlTransient
    public Collection<Bankverbindung> getBankverbindungCollection() {
        return bankverbindungCollection;
    }

    public void setBankverbindungCollection(Collection<Bankverbindung> bankverbindungCollection) {
        this.bankverbindungCollection = bankverbindungCollection;
    }

    @XmlTransient
    public Collection<Person> getPersonCollection() {
        return personCollection;
    }

    public void setPersonCollection(Collection<Person> personCollection) {
        this.personCollection = personCollection;
    }

    @XmlTransient
    public Collection<Rechnung> getRechnungCollection() {
        return rechnungCollection;
    }

    public void setRechnungCollection(Collection<Rechnung> rechnungCollection) {
        this.rechnungCollection = rechnungCollection;
    }

    @XmlTransient
    public Collection<Offenerechnungen> getOffenerechnungenCollection() {
        return offenerechnungenCollection;
    }

    public void setOffenerechnungenCollection(Collection<Offenerechnungen> offenerechnungenCollection) {
        this.offenerechnungenCollection = offenerechnungenCollection;
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
        hash += (fnr != null ? fnr.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Family)) {
            return false;
        }
        Family other = (Family) object;
        if ((this.fnr == null && other.fnr != null) || (this.fnr != null && !this.fnr.equals(other.fnr))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Family[ fnr=" + fnr + " ]";
    }
    
}
