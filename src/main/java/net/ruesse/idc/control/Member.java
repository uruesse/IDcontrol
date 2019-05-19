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

import net.ruesse.idc.database.persistence.Person;
import net.ruesse.idc.database.persistence.service.PersonExt;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class Member {

    private int id;
    private String mglnr;
    private String displayName;
    private String name;

    public Member() {
    }

    public Member(int id, String mglnr, String displayName, String name) {
        this.id = id;
        this.mglnr = mglnr;
        this.displayName = displayName;
        this.name = name;
    }

    public Member(int id, Person p) {
        this.id = id;
        PersonExt pe = new PersonExt(p);
        this.mglnr = pe.getStrMglnr();
        this.displayName = pe.formatMglnr() + " - " + pe.getFullname();
        this.name = pe.getStrMglnr() + " " + pe.getFullname().toLowerCase();
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMglnr() {
        return mglnr;
    }

    public void setMglnr(String mglnr) {
        this.mglnr = mglnr;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
