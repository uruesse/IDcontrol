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
package net.ruesse.idc.database.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Math.log;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import net.ruesse.idc.control.Constants;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class SqlSupport {

    private final static Logger LOGGER = Logger.getLogger(SqlSupport.class.getName());

    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME).createEntityManager();

    /**
     * Liefert die SQL-Connection aus dem Entity Manager
     * @return 
     */
    public java.sql.Connection getSqlConnection() {
        return em.unwrap(java.sql.Connection.class);
    }


    /**
     *
     * @param stmt
     */
    public void nativeSQL(String stmt) {
        LOGGER.log(Level.INFO, "SQL: {0}", stmt);
        em.createNativeQuery(stmt).executeUpdate();
    }

    /**
     *
     * @param fileName
     */
    public void executeSQLScript(String fileName) {
        String s, stmt;
        StringBuilder sb = new StringBuilder();

        InputStream stream = SqlSupport.class.getResourceAsStream("../scripts/" + fileName);
        if (stream != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            em.getTransaction().begin();
            try {
                while ((s = br.readLine()) != null) {
                    if (!(s.startsWith("--") || s.startsWith("//") || s.startsWith("#"))) {
                        sb.append(" ");
                        sb.append(s.trim());
                    }
                }
                br.close();

                // Delete comments within /* */
                int a;
                int e;
                a = sb.indexOf("/*");
                e = sb.indexOf("*/");
                while (a >= 0 && e > a) {
                    sb.delete(a, e + 2);
                    a = sb.indexOf("/*");
                    e = sb.indexOf("*/");
                }

                // here is our splitter ! We use ";" as a delimiter for each request
                // then we are sure to have well formed statements
                String[] inst = sb.toString().split(";");

                for (String inst1 : inst) {
                    // we ensure that there is no spaces before or after the request string
                    // in order to not execute empty statements
                    stmt = inst1.trim();
                    if (!stmt.equals("")) {
                        //stmt = stmt.replace("@{UPDFY}", strFiscalYear);
                        LOGGER.log(Level.FINE, "SQL: {0}", stmt);
                        nativeSQL(stmt);
                    }
                }
                em.getTransaction().commit();
            } catch (IOException e) {
                em.getTransaction().rollback();
                LOGGER.log(Level.SEVERE, "Message   : {0}", e.toString());
            }
        } else {
            LOGGER.log(Level.SEVERE, "file {0} not found!", fileName);
        }
    }

    /**
     * Iterate through a stack of SQLExceptions.
     *
     * @param sqle a SQLException
     */
    static void SQLExceptionPrint(SQLException sqle) {
        while (sqle != null) {
            LOGGER.log(Level.INFO, "SQL Exception:\n"
                    + "SQL Status   : {0}\n"
                    + "ErrorCode    : {1}\n"
                    + "Nachricht    : {2}",
                    new Object[]{(sqle).getSQLState(),
                        (sqle).getErrorCode(),
                        (sqle).getLocalizedMessage()});
            sqle = sqle.getNextException();
        }
    }

}
