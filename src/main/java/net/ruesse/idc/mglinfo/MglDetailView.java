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
package net.ruesse.idc.mglinfo;

import java.io.Serializable;
import java.util.logging.Logger;
import net.ruesse.idc.database.persistence.service.PersonMgl;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean(name="mglDetailView")
@ViewScoped
public class MglDetailView implements Serializable {

    private static final Logger LOG = Logger.getLogger(MglDetailView.class.getName());

    private static final long serialVersionUID = 5L;

    private PersonMgl selectedPerson;

    /*
    public MglDetailView() {
        LOG.info("aufgerufen");
    }
     */
    public void init() {
        LOG.info("aufgerufen");
        if (selectedPerson != null) {
            LOG.info("Person: " + selectedPerson.getFullname());
        }
    }

    public PersonMgl getSelectedPerson() {
        LOG.info("aufgerufen");
        return selectedPerson;
    }

    public void setSelectedPerson(PersonMgl selectedPerson) {
        LOG.info("aufgerufen");
        this.selectedPerson = selectedPerson;
    }

    public void SelectedPerson(PersonMgl selectedPerson) {
        LOG.info("aufgerufen");
        this.selectedPerson = selectedPerson;
    }

    public void selectedPerson(PersonMgl selectedPerson) {
        LOG.info("aufgerufen");
        this.selectedPerson = selectedPerson;
    }

    public String selectPerson(PersonMgl pp) {
        LOG.info("aufgerufen");
        if (pp != null) {
            LOG.info(pp.getFullname());
        }

        return "";

    }

}
