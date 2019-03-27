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
package net.ruesse.idc.control;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import net.ruesse.idc.database.sql.SqlSupport;
import net.ruesse.idc.report.PrintSupport;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ApplicationScoped
@ManagedBean
public class ApplicationControlBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationControlBean.class.getName());

    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME).createEntityManager();

    static private boolean isDemo;
    static private boolean isPDF;
    static private boolean isDruckerdialog;
    static private String kartendrucker;

    public ApplicationControlBean() {
        LOGGER.setLevel(Level.INFO);
        LOGGER.fine("aufgerufen");
        isDemo = false;
        isPDF = false;
        isDruckerdialog = true;
        kartendrucker = "";
    }

    public boolean isIsDemo() {
        LOGGER.log(Level.FINE, "isDemo={0}", isDemo);
        return isDemo;
    }

    public void setIsDemo(boolean isDemo) {
        LOGGER.log(Level.FINE, "isDemo={0}", isDemo);
        this.isDemo = isDemo;
    }

    public boolean isIsPDF() {
        return isPDF;
    }

    static public boolean isPDF() {
        return isPDF;
    }

    public void setIsPDF(boolean isPDF) {
        this.isPDF = isPDF;
    }

    public boolean isIsDruckerdialog() {
        return isDruckerdialog;
    }

    public void setIsDruckerdialog(boolean isDruckerdialog) {
        this.isDruckerdialog = isDruckerdialog;
    }

    public static boolean isDruckerdialog() {
        return isDruckerdialog;
    }

    public static String getKartendrucker() {
        return kartendrucker;
    }

    public static void setKartendrucker(String kartendrucker) {
        ApplicationControlBean.kartendrucker = kartendrucker;
    }

    public void printActionRS() {
        String REPORT = "IDCard-back";

        PrintSupport.printReport(REPORT, em);

        addMessage("Fertig", "Druckauftrag erledigt");
    }

    public void stageToProd() {
        SqlSupport sp = new SqlSupport();
        
        sp.executeSQLScript("stage2prod.sql");
        sp.executeSQLScript("fremdzahler.sql");
        sp.executeSQLScript("demo/entenhausen.sql");

        addMessage("Fertig", "Datenbank kopiert");
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}
