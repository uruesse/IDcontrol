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

import net.ruesse.idc.database.persistence.Person;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class PersonExt extends Person {
    

    public String getFullname() {
        return this.getVorname() + " " + this.getNachname();
    }

    public String getStrMglnr() {
        return String.format("%013d", this.getMglnr());
    }

    public int getOpenwaterbill() {
        return 0;
    }

    public int getOpenposts() {
        return 0;
    }

    public String getState() {
        return this.getStatus();
    }

}
