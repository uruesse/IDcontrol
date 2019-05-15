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
import static net.ruesse.idc.control.FileService.getDocumentsDir;
import net.ruesse.idc.documentation.DocumentView;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
public class MenuView {

    private final static Logger LOGGER = Logger.getLogger(MenuView.class.getName());

    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();
        DefaultMenuItem item;
        DefaultSubMenu firstLevelSubmenu;
        //<p:menuitem value="Über IDControl" outcome="info" icon="pi pi-info"/>
        item = new DefaultMenuItem("Über IDControl");
        item.setIcon("pi pi-info");
        item.setOutcome("index");
        model.addElement(item);
        model.addElement(new DefaultSeparator());
        // <p:menuitem value="Einstellungen" outcome="index" icon="pi pi-cog"/>
        item = new DefaultMenuItem("Einstellungen");
        item.setIcon("pi pi-cog");
        item.setOutcome("settings");
        model.addElement(item);
        model.addElement(new DefaultSeparator());
        //<p:menuitem process="@this" value="Scanlog zurücksetzten" action="#{applicationControlBean.resetScanLog}" icon="pi pi-users"/>
        item = new DefaultMenuItem("Scanlog zurücksetzten");
        item.setIcon("pi pi-users");
        item.setCommand("#{applicationControlBean.resetScanLog}");
        model.addElement(item);
        //<p:menuitem process="@this" value="Anwesenheitsliste" action="#{applicationControlBean.printActionAL}" icon="pi pi-users"/>
        item = new DefaultMenuItem("Anwesenheitsliste");
        item.setIcon("pi pi-users");
        item.setCommand("#{applicationControlBean.printActionAL}");
        model.addElement(item);

        //<p:submenu label="Anleitungen" icon="pi pi-folder-open">
        //  <p:menuitem outcome="documentation"  value="ZEBRA ZXP1" icon="pi pi-file"/>
        //</p:submenu>
        firstLevelSubmenu = new DefaultSubMenu("Anleitungen");
        firstLevelSubmenu.setIcon("pi pi-folder-open");

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
        firstLevelSubmenu.setIcon("pi pi-folder-open");

        item = new DefaultMenuItem("Ausweisrückseite");
        item.setIcon("pi pi-print");
        item.setOutcome("idcardback");
        firstLevelSubmenu.addElement(item);
        model.addElement(firstLevelSubmenu);

        model.addElement(new DefaultSeparator());
        //<p:menuitem value="Beenden" action="#{menuView.logout}" icon="pi pi-power-off"/>  
        item = new DefaultMenuItem("Beenden");
        item.setIcon("pi pi-power-off");
        item.setCommand("#{menuView.logout}");
        model.addElement(item);
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }

    public void logout() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect("http://closekiosk");
    }

}
