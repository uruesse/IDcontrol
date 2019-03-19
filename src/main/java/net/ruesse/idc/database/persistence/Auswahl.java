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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
    @NamedQuery(name = "Auswahl.findAll", query = "SELECT a FROM Auswahl a")
    , @NamedQuery(name = "Auswahl.findByMglnr", query = "SELECT a FROM Auswahl a WHERE a.mglnr = :mglnr")})
public class Auswahl implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Long mglnr;

    public Auswahl() {
    }

    public Auswahl(Long mglnr) {
        this.mglnr = mglnr;
    }

    public Long getMglnr() {
        return mglnr;
    }

    public void setMglnr(Long mglnr) {
        this.mglnr = mglnr;
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
        if (!(object instanceof Auswahl)) {
            return false;
        }
        Auswahl other = (Auswahl) object;
        if ((this.mglnr == null && other.mglnr != null) || (this.mglnr != null && !this.mglnr.equals(other.mglnr))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Auswahl[ mglnr=" + mglnr + " ]";
    }
    
}
