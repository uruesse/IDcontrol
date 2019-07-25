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
package net.ruesse.idc.database.persistence.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.ruesse.idc.application.Constants;
import net.ruesse.idc.control.ApplicationControlBean;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.database.persistence.Machinepref;
import net.ruesse.idc.database.persistence.Userpref;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ApplicationScoped
public class WebDavService {

    private static final Logger LOG = Logger.getLogger(WebDavService.class.getName());

    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

    static String WebserviceURL = "";
    static String WebserviceUser = "";
    static String WebservicePassword = "";
    static boolean WebserviceAvailable = false;

    /**
     *
     */
    public WebDavService() {
        if (!WebserviceAvailable) {
            initialize();
        }
    }

    private String getMachineName() {
        String hostName = "";
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            hostName = System.getenv("COMPUTERNAME");
            // nur zum Test: Nachfolgender code kann später raus
            try {
                InetAddress iAddress = InetAddress.getLocalHost();
                LOG.log(Level.INFO, "WIN: getHostname liefert {0}", iAddress.getHostName());
            } catch (UnknownHostException ex) {
                LOG.log(Level.INFO, "WIN: Kein Hostname gefunden", ex);
            }
        } else {
            try {
                InetAddress iAddress = InetAddress.getLocalHost();
                hostName = iAddress.getHostName();
            } catch (UnknownHostException ex) {
                LOG.log(Level.INFO, "Kein Hostname gefunden", ex);
            }
        }
        LOG.log(Level.INFO, "Hostname={0}", hostName);
        return hostName;
    }

    /**
     * 
     */
    private void initialize() {
        VereinService vs = new VereinService();
        WebserviceURL = vs.getAktVerein().getUridav();
        if (WebserviceURL.isEmpty()) {
            setWebserviceAvailable(false);
            return;
        }
        
        // Versuch 1: Usercredentials
        WebserviceUser = ApplicationControlBean.getLoginMgl().getWebDavUser();
        WebservicePassword = ApplicationControlBean.getLoginMgl().getWebDavPassword();
        
        // Versuch 2: Machinencredentials
        if (WebserviceUser.isEmpty() || WebservicePassword.isEmpty()) {
            try {
                Query q = em.createNamedQuery("Machinepref.findByHostname", Machinepref.class);
                q.setParameter("hostname", getMachineName());
                Machinepref machinePref = (Machinepref) q.getSingleResult();
                if (machinePref != null) {
                    WebserviceUser = machinePref.getWebdavuser();
                    WebservicePassword = machinePref.getWebdavpwd();
                }
            } catch (NoResultException e) {
                // nix tun
            }
        }
        
        if (WebserviceUser.isEmpty() || WebservicePassword.isEmpty()) {
            setWebserviceAvailable(false);
        } else {
            setWebserviceAvailable(true);
        }
    }

    /**
     *
     * @return
     */
    public String getWebserviceURL() {
        LOG.info(WebserviceURL);
        return WebserviceURL;
    }

    /**
     *
     * @param WebserviceURL
     */
    public void setWebserviceURL(String WebserviceURL) {
        WebDavService.WebserviceURL = WebserviceURL;
    }

    /**
     *
     * @return
     */
    public String getWebserviceUser() {
        LOG.info(WebserviceUser);
        return WebserviceUser;
    }

    /**
     *
     * @param WebserviceUser
     */
    public void setWebserviceUser(String WebserviceUser) {
        WebDavService.WebserviceUser = WebserviceUser;
    }

    /**
     *
     * @return
     */
    public String getWebservicePassword() {
        LOG.log(Level.INFO, "Password exists: length= {0}", WebservicePassword.length());
        return WebservicePassword;
    }

    /**
     *
     * @param WebservicePassword
     */
    public void setWebservicePassword(String WebservicePassword) {
        WebDavService.WebservicePassword = WebservicePassword;
    }

    /**
     *
     * @return
     */
    public boolean isWebserviceAvailable() {
        LOG.info(WebserviceAvailable ? "true" : "false");
        return WebserviceAvailable;
    }

    /**
     *
     * @param WebserviceAvailable
     */
    public void setWebserviceAvailable(boolean WebserviceAvailable) {
        WebDavService.WebserviceAvailable = WebserviceAvailable;
    }

}
