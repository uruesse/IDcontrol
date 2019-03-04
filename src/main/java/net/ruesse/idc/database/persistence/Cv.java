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
    @NamedQuery(name = "Cv.findAll", query = "SELECT c FROM Cv c")
    , @NamedQuery(name = "Cv.findById", query = "SELECT c FROM Cv c WHERE c.id = :id")
    , @NamedQuery(name = "Cv.findByCvkey", query = "SELECT c FROM Cv c WHERE c.cvkey = :cvkey")
    , @NamedQuery(name = "Cv.findByCvvalue", query = "SELECT c FROM Cv c WHERE c.cvvalue = :cvvalue")
    , @NamedQuery(name = "Cv.findByValidfrom", query = "SELECT c FROM Cv c WHERE c.validfrom = :validfrom")
    , @NamedQuery(name = "Cv.findByValidto", query = "SELECT c FROM Cv c WHERE c.validto = :validto")})
public class Cv implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Size(max = 128)
    @Column(length = 128)
    private String cvkey;
    @Size(max = 128)
    @Column(length = 128)
    private String cvvalue;
    @Temporal(TemporalType.DATE)
    private Date validfrom;
    @Temporal(TemporalType.DATE)
    private Date validto;
    @JoinColumn(name = "MGLNR", referencedColumnName = "MGLNR")
    @ManyToOne
    private Person mglnr;

    public Cv() {
    }

    public Cv(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCvkey() {
        return cvkey;
    }

    public void setCvkey(String cvkey) {
        this.cvkey = cvkey;
    }

    public String getCvvalue() {
        return cvvalue;
    }

    public void setCvvalue(String cvvalue) {
        this.cvvalue = cvvalue;
    }

    public Date getValidfrom() {
        return validfrom;
    }

    public void setValidfrom(Date validfrom) {
        this.validfrom = validfrom;
    }

    public Date getValidto() {
        return validto;
    }

    public void setValidto(Date validto) {
        this.validto = validto;
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
        if (!(object instanceof Cv)) {
            return false;
        }
        Cv other = (Cv) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Cv[ id=" + id + " ]";
    }
    
}
