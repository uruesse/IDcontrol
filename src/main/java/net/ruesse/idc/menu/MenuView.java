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
package net.ruesse.idc.menu;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import static net.ruesse.idc.control.ApplicationControlBean.getLoginMglUserRights;
import static net.ruesse.idc.control.FileService.getDocumentsDir;
import net.ruesse.idc.control.VereinService;
import net.ruesse.idc.documentation.DocumentView;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 * Stellt Routinen zum Hauptmenu zur Verfügung
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
public class MenuView {

    private final static Logger LOGGER = Logger.getLogger(MenuView.class.getName());

    private MenuModel model;

    /**
     * Initialisiert das Hauptmenu
     */
    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();
        DefaultMenuItem item;
        DefaultSubMenu firstLevelSubmenu;

        //<p:menuitem value="Über IDControl" outcome="info" icon="pi pi-info"/>
        item = new DefaultMenuItem("Über IDControl");
        item.setIcon("pi pi-info");
        item.setOutcome("index");
        item.setTitle("Informationen zu Copyright und Systemvariable");
        model.addElement(item);
        model.addElement(new DefaultSeparator());

        // <p:menuitem value="Einstellungen" outcome="index" icon="pi pi-cog"/>
        item = new DefaultMenuItem("Einstellungen");
        item.setIcon("pi pi-cog");
        item.setOutcome("settings");
        item.setTitle("grundsätzliche Programmeinstellungen vornehmen");
        item.setDisabled(!(getLoginMglUserRights() > 0));
        model.addElement(item);
        model.addElement(new DefaultSeparator());

        item = new DefaultMenuItem("Einlasskontrolle");
        item.setIcon("pi pi-user-plus");
        item.setOutcome("scan");
        item.setTitle("Einlasskontrolle durch Scannen der Ausweise");
        item.setDisabled(!(getLoginMglUserRights() > 1));
        model.addElement(item);

        //<p:menuitem process="@this" value="Scanlog zurücksetzten" action="#{applicationControlBean.resetScanLog}" icon="pi pi-users"/>
        item = new DefaultMenuItem("Scanlog zurücksetzten");
        item.setIcon("pi pi-user-minus");
        item.setCommand("#{applicationControlBean.resetScanLog}");
        item.setTitle("Log der gescannten Ausweise wird komplett geleert");
        item.setDisabled(!(getLoginMglUserRights() > 1));
        model.addElement(item);

        item = new DefaultMenuItem("Mitgliederinfo");
        item.setIcon("pi pi-users");
        item.setOutcome("mglinfo");
        item.setTitle("Recherche von Mitgliederdaten");
        item.setDisabled(!(getLoginMglUserRights() > 2));

        model.addElement(item);
        model.addElement(new DefaultSeparator());

        //<h:outputLink value="https://dlrg-mv.sewobe.de/" target="_blank" title="Link zu SEWOBE">Mitgliederverwaltung</h:outputLink>
        VereinService vs = new VereinService();
        String URI = vs.getAktVerein().getUrimgv();
        if (URI == null || URI.isEmpty()) {
            URI = "http://null.null";
        }
        item = new DefaultMenuItem("Mitgliederverwaltung");
        item.setIcon("pi pi-external-link");
        item.setTarget("_blank");
        item.setUrl(URI);
        item.setTitle("Mitgliederverwaltung öffenet in einem neuen Browserfenster");
        item.setDisabled(!(getLoginMglUserRights() > 2));
        model.addElement(item);
        model.addElement(new DefaultSeparator());

        //<p:submenu label="Anleitungen" icon="pi pi-folder-open">
        //  <p:menuitem outcome="documentation"  value="ZEBRA ZXP1" icon="pi pi-file"/>
        //</p:submenu>
        firstLevelSubmenu = new DefaultSubMenu("Anleitungen");
        //firstLevelSubmenu.setIcon("pi pi-folder-open");

        Path p = getDocumentsDir();
        DirectoryStream<Path> stream;
        try {
            stream = Files.newDirectoryStream(p, "*.pdf");
            for (Path entry : stream) {
                String fn = entry.getFileName().toString().replace("_", " ");
                fn = fn.replace(".pdf", "");
                item = new DefaultMenuItem(fn);
                item.setIcon("pi pi-file");
                item.setStyle("width:500px");
                item.setOutcome("document?document=" + entry.getFileName().toString());
                firstLevelSubmenu.addElement(item);
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentView.class.getName()).log(Level.SEVERE, null, ex);
        }

        model.addElement(firstLevelSubmenu);

        //<p:submenu label="Drucken" icon="pi pi-folder-open">
        //  <p:menuitem value="Ausweisrückseite" outcome="idcardback" icon="pi pi-print"/>
        //</p:submenu> 
        firstLevelSubmenu = new DefaultSubMenu("Drucken");
        //firstLevelSubmenu.setIcon("pi pi-folder-open");

        item = new DefaultMenuItem("Ausweisrückseite");
        item.setIcon("pi pi-print");
        item.setOutcome("idcardback");
        item.setTitle("Drucken der statischen Ausweisrückseite");
        firstLevelSubmenu.addElement(item);

        //<p:menuitem process="@this" value="Anwesenheitsliste" action="#{applicationControlBean.printActionAL}" icon="pi pi-users"/>
        item = new DefaultMenuItem("Anwesenheitsliste");
        item.setIcon("pi pi-users");
        item.setCommand("#{applicationControlBean.printActionAL}");
        item.setTitle("Report Anwesenheitsliste ausgeben");
        firstLevelSubmenu.addElement(item);

        model.addElement(firstLevelSubmenu);

        model.addElement(new DefaultSeparator());
        //<p:menuitem value="Beenden" action="#{menuView.logout}" icon="pi pi-power-off"/>  
        item = new DefaultMenuItem("Beenden");
        item.setIcon("pi pi-power-off");
        item.setCommand("#{menuView.logout}");
        item.setTitle("Beenden der aktuellen Sitzung");
        model.addElement(item);
    }

    /**
     * Liefert das Menumodel
     *
     * @return Menumodel
     */
    public MenuModel getModel() {
        return model;
    }

    /**
     * setzt das Menumodel
     *
     * @param model Menumodel
     */
    public void setModel(MenuModel model) {
        this.model = model;
    }

    /**
     * Logout durch Aufruf des externen Links closekiosk. Dieser Mechanismus
     * basiert auf der Browsererweiterung closekiosk von Google-Chrome und
     * schließt den Kiosk-Modus. Auf allen anderen Brouwsern führt der Aufruf zu
     * einem "Page not found" Fehler.
     *
     * @see https://chrome.google.com/webstore/detail/close-kiosk
     *
     * @throws IOException
     */
    public void logout() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect("http://closekiosk");
    }

}
