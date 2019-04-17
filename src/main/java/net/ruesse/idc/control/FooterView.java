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

import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import static net.ruesse.idc.control.ApplicationControlBean.getLoginMgl;
import net.ruesse.idc.database.persistence.service.PersonExt;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@ManagedBean
public class FooterView {

    private static final Logger LOGGER = Logger.getLogger(FooterView.class.getName());

    public String getFileInfo() {
        VereinService vs = new VereinService();
        return vs.getFileInfo();
    }

    public String getUserInfo() {
        PersonExt pe = getLoginMgl();
        if (pe != null) {
            return getLoginMgl().getFullname();
        } else {
            return "";
        }
    }
}
