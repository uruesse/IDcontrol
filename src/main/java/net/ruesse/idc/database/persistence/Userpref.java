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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Entity
@Table(catalog = "", schema = "IDCLOCAL")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Userpref.findAll", query = "SELECT u FROM Userpref u")
    , @NamedQuery(name = "Userpref.findByMglnr", query = "SELECT u FROM Userpref u WHERE u.mglnr = :mglnr")
    , @NamedQuery(name = "Userpref.findByPrinter", query = "SELECT u FROM Userpref u WHERE u.printer = :printer")
    , @NamedQuery(name = "Userpref.findByPdfheight", query = "SELECT u FROM Userpref u WHERE u.pdfheight = :pdfheight")})
public class Userpref implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Long mglnr;
    @Size(max = 128)
    @Column(length = 128)
    private String printer;
    private Integer pdfheight;

    public Userpref() {
    }

    public Userpref(Long mglnr) {
        this.mglnr = mglnr;
    }

    public Long getMglnr() {
        return mglnr;
    }

    public void setMglnr(Long mglnr) {
        this.mglnr = mglnr;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public Integer getPdfheight() {
        return pdfheight;
    }

    public void setPdfheight(Integer pdfheight) {
        this.pdfheight = pdfheight;
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
        if (!(object instanceof Userpref)) {
            return false;
        }
        Userpref other = (Userpref) object;
        if ((this.mglnr == null && other.mglnr != null) || (this.mglnr != null && !this.mglnr.equals(other.mglnr))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Userpref[ mglnr=" + mglnr + " ]";
    }
    
}
