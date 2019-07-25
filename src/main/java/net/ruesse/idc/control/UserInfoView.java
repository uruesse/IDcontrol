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

import net.ruesse.idc.database.persistence.service.VereinService;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.inject.Named;
import static net.ruesse.idc.control.ApplicationControlBean.getLoginMgl;
import net.ruesse.idc.database.persistence.service.PersonUser;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@Named
public class UserInfoView implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserInfoView.class.getName());
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @return 
     */
    public String getFileInfo() {
        LOGGER.info("Lese letztes Aktualisierungsdatum ein");
        VereinService vs = new VereinService();
        return vs.getFileInfo();
    }

    /**
     * 
     * @return 
     */
    public String getUserInfo() {
        PersonUser pe = getLoginMgl();
        if (pe != null) {
            return pe.getPersonMgl().getFullname();
        } else {
            return "";
        }
    }
}
