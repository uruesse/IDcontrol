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
@Table(catalog = "", schema = "IDCREMOTE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Verein.findAll", query = "SELECT v FROM Verein v")
    , @NamedQuery(name = "Verein.findByMglnr", query = "SELECT v FROM Verein v WHERE v.mglnr = :mglnr")
    , @NamedQuery(name = "Verein.findByFirma", query = "SELECT v FROM Verein v WHERE v.firma = :firma")
    , @NamedQuery(name = "Verein.findByStrasse", query = "SELECT v FROM Verein v WHERE v.strasse = :strasse")
    , @NamedQuery(name = "Verein.findByPlz", query = "SELECT v FROM Verein v WHERE v.plz = :plz")
    , @NamedQuery(name = "Verein.findByOrt", query = "SELECT v FROM Verein v WHERE v.ort = :ort")
    , @NamedQuery(name = "Verein.findByLand", query = "SELECT v FROM Verein v WHERE v.land = :land")
    , @NamedQuery(name = "Verein.findByRegister", query = "SELECT v FROM Verein v WHERE v.register = :register")
    , @NamedQuery(name = "Verein.findByEmail", query = "SELECT v FROM Verein v WHERE v.email = :email")
    , @NamedQuery(name = "Verein.findByUri", query = "SELECT v FROM Verein v WHERE v.uri = :uri")
    , @NamedQuery(name = "Verein.findByDatatime", query = "SELECT v FROM Verein v WHERE v.datatime = :datatime")})
public class Verein implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Long mglnr;
    @Size(max = 128)
    @Column(length = 128)
    private String firma;
    @Size(max = 128)
    @Column(length = 128)
    private String strasse;
    @Size(max = 16)
    @Column(length = 16)
    private String plz;
    @Size(max = 128)
    @Column(length = 128)
    private String ort;
    @Size(max = 128)
    @Column(length = 128)
    private String land;
    @Size(max = 128)
    @Column(length = 128)
    private String register;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Ungültige E-Mail")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 128)
    @Column(length = 128)
    private String email;
    @Size(max = 128)
    @Column(length = 128)
    private String uri;
    @Temporal(TemporalType.TIMESTAMP)
    private Date datatime;

    public Verein() {
    }

    public Verein(Long mglnr) {
        this.mglnr = mglnr;
    }

    public Long getMglnr() {
        return mglnr;
    }

    public void setMglnr(Long mglnr) {
        this.mglnr = mglnr;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getDatatime() {
        return datatime;
    }

    public void setDatatime(Date datatime) {
        this.datatime = datatime;
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
        if (!(object instanceof Verein)) {
            return false;
        }
        Verein other = (Verein) object;
        if ((this.mglnr == null && other.mglnr != null) || (this.mglnr != null && !this.mglnr.equals(other.mglnr))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Verein[ mglnr=" + mglnr + " ]";
    }
    
}
