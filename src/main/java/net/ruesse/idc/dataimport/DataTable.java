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
package net.ruesse.idc.dataimport;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class DataTable {

    private String tablename;
    private long items;
    private long csv;
    private long failure;

    public DataTable(String tablename, long items, long csv, long failure) {
        this.tablename = tablename;
        this.items = items;
        this.csv = csv;
        this.failure = failure;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public long getItems() {
        return items;
    }

    public void setItems(long items) {
        this.items = items;
    }

    public long getCsv() {
        return csv;
    }

    public void setCsv(long csv) {
        this.csv = csv;
    }

    public long getFailure() {
        return failure;
    }

    public void setFailure(long failure) {
        this.failure = failure;
    }

    public boolean getShowFailure() {
        return failure == 0;
    }

}
