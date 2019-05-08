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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import static net.ruesse.idc.control.ApplicationControlBean.getPersistenceParameters;
import net.ruesse.idc.control.Constants;
import static net.ruesse.idc.control.Constants.INTERNAL_PWD;
import static net.ruesse.idc.control.FileService.createDatedExportsDir;
import static net.ruesse.idc.control.FileService.deleteDirectoryStream;
import static net.ruesse.idc.control.FileService.getExportsDir;
import static net.ruesse.idc.control.FileService.getReportsDir;
import static net.ruesse.idc.control.FileService.getVereinBaseDir;
import static net.ruesse.idc.control.FileService.getVereinDir;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class SqlSupport {

    private final static Logger LOGGER = Logger.getLogger(SqlSupport.class.getName());

    EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME, getPersistenceParameters()).createEntityManager();

    /**
     * Liefert die SQL-Connection aus dem Entity Manager
     *
     * @return
     */
    public java.sql.Connection getSqlConnection() {
        return em.unwrap(java.sql.Connection.class);
    }

    /**
     *
     * @param schema
     * @return
     */
    public List<String> getTables(String schema) {
        String stmt = "SELECT TABLENAME "
                + "FROM SYS.SYSTABLES T, SYS.SYSSCHEMAS S "
                + "WHERE T.TABLETYPE='T' "
                + "AND S.SCHEMAID=T.SCHEMAID "
                + "AND S.SCHEMANAME='" + schema.toUpperCase() + "'";
        Query q = em.createNativeQuery(stmt);
        return q.getResultList();
    }

    /**
     * 
     * @param schema
     * @param name
     * @return 
     */
    public long getTableSize(String schema, String name) {
        String stmt = "SELECT count(*) FROM " + schema.toUpperCase() + "." + name.toUpperCase();

        Query q = em.createNativeQuery(stmt);
        Object result = q.getSingleResult();

        Long count = (result != null ? Long.parseLong(result.toString()) : 0);

        LOGGER.log(Level.INFO, "SQL: {0}: {1}", new Object[]{stmt, count});
        return count;
    }

    public void beginTransaction() {
        em.getTransaction().begin();
    }

    public void commitTransaction() {
        em.getTransaction().commit();
    }

    /**
     *
     * @param schema
     * @return
     */
    public String exportSchema(String schema) {

        Path exportPath = createDatedExportsDir();
        Path exportFile = Paths.get(exportPath.toString() + ".IDC");
        getTables(schema).forEach((table) -> {
            exportTable(schema, table, exportPath);
        });

        try {
            ZipFile zippedFiles = new ZipFile((exportPath.resolve("files.zip")).toFile());
            ZipParameters parameters = new ZipParameters();

            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
            zippedFiles.addFolder(getVereinDir().toFile(), parameters);
        } catch (ZipException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            ZipFile zipFile = new ZipFile((exportPath.resolve(exportFile.toString())).toFile());
            ZipParameters parameters = new ZipParameters();

            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            // Setting password
            parameters.setPassword(INTERNAL_PWD);

            zipFile.addFolder(exportPath.toFile(), parameters);

        } catch (ZipException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        deleteDirectoryStream(exportPath);

        return exportFile.toString();

    }

    private Path unCompressPasswordProtectedFiles(Path sourcePath) {

        Path destPath = sourcePath.getParent();

        LOGGER.log(Level.INFO, "Destination {0}", destPath.toString());
        try {
            ZipFile zipFile = new ZipFile(sourcePath.toFile());
            // If it is encrypted then provide password
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(INTERNAL_PWD);
            }
            zipFile.extractAll(destPath.toString());
        } catch (ZipException ex) {
            Logger.getLogger(SqlSupport.class.getName()).log(Level.SEVERE, null, ex);
        }

        String fn = sourcePath.toString();

        int lastIndex = fn.lastIndexOf('.');
        if (lastIndex == -1) {

        }
        return Paths.get(fn.substring(0, lastIndex));
    }

    public void importSchema(String schema, Path importZip) {
        Path path = unCompressPasswordProtectedFiles(importZip);

        executeSQLScript("createdb.sql");

        getTables(schema).forEach((table) -> {
            importTable(schema, table, path);
        });

        LOGGER.log(Level.INFO, "Destination for file.zip  {0}", getVereinBaseDir().toString());
        try {
            ZipFile zipFile = new ZipFile((path.resolve("files.zip")).toFile());
            zipFile.extractAll(getVereinBaseDir().toString());
        } catch (ZipException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            Files.walk(getReportsDir())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach(File::delete);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void importTable(String schema, String table, Path importPath) {

        Path importFile = importPath.resolve(table.toLowerCase() + ".dat");
        long fs = 0;
        try {
            // schauen, ob in der Datei Inhalt ist, da ansonsten der import-Befehl scheitert.
            fs = Files.size(importFile);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        if (fs > 0) {

            em.getTransaction().begin();

            String stmt = "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE ("
                    + "'" + schema.toUpperCase() + "',"
                    + "'" + table.toUpperCase() + "',"
                    + "'" + importFile.toString() + "',"
                    + "'%',"
                    + "'§',"
                    + "'UTF-8',"
                    + "1"
                    + ")";
            nativeSQL(stmt);

            em.getTransaction().commit();
        }
    }

    public void exportTable(String schema, String table, Path exportPath) {

        em.getTransaction().begin();

        Path exportFile = exportPath.resolve(table.toLowerCase() + ".dat");

        String stmt = "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE ("
                + "'" + schema.toUpperCase() + "',"
                + "'" + table.toUpperCase() + "',"
                + "'" + exportFile.toString() + "',"
                + "'%',"
                + "'§',"
                + "'UTF-8'"
                + ")";
        nativeSQL(stmt);

        em.getTransaction().commit();
    }

    public void exportTable(String table) {
        exportTable("IDCREMOTE", table, getExportsDir());
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
