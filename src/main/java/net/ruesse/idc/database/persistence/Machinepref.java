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
@Table(catalog = "", schema = "IDCLOCAL")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Machinepref.findAll", query = "SELECT m FROM Machinepref m")
    , @NamedQuery(name = "Machinepref.findById", query = "SELECT m FROM Machinepref m WHERE m.id = :id")
    , @NamedQuery(name = "Machinepref.findByHostname", query = "SELECT m FROM Machinepref m WHERE m.hostname = :hostname")
    , @NamedQuery(name = "Machinepref.findByWebdavuser", query = "SELECT m FROM Machinepref m WHERE m.webdavuser = :webdavuser")
    , @NamedQuery(name = "Machinepref.findByWebdavpwd", query = "SELECT m FROM Machinepref m WHERE m.webdavpwd = :webdavpwd")})
public class Machinepref implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Size(max = 128)
    @Column(length = 128)
    private String hostname;
    @Size(max = 128)
    @Column(length = 128)
    private String webdavuser;
    @Size(max = 128)
    @Column(length = 128)
    private String webdavpwd;

    public Machinepref() {
    }

    public Machinepref(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getWebdavuser() {
        return webdavuser;
    }

    public void setWebdavuser(String webdavuser) {
        this.webdavuser = webdavuser;
    }

    public String getWebdavpwd() {
        return webdavpwd;
    }

    public void setWebdavpwd(String webdavpwd) {
        this.webdavpwd = webdavpwd;
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
        if (!(object instanceof Machinepref)) {
            return false;
        }
        Machinepref other = (Machinepref) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.ruesse.idc.database.persistence.Machinepref[ id=" + id + " ]";
    }
    
}
