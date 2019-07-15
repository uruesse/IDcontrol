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
package net.ruesse.idc.webclient;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class FileServiceWebDav {

    private static final Logger LOG = Logger.getLogger(FileServiceWebDav.class.getName());

    /**
     *
     */
    class DropFile extends Thread {

        Sardine sardine;
        String myUrl;

        /**
         *
         * @param myURL
         */
        public DropFile(String myURL) {
            super();
            sardine = SardineFactory.begin();
            sardine.setCredentials("xxx", "yyyy");

            this.myUrl = myURL;
        }

        /**
         *
         */
        @Override
        public void run() {
            LOG.info("Thread started deleting file=" + myUrl);
            try {
                sardine.delete(myUrl);
                LOG.info("File=" + myUrl + " deleted.");
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "File=" + myUrl + " delete failed.", ex);
            }
        }
    }

    Sardine sardine;
    String myUrl = "";
    
    /**
     * 
     * @param WebserviceURL
     * @param user
     * @param password 
     */
    public FileServiceWebDav(String WebserviceURL, String user, String password) {
        sardine = SardineFactory.begin();
        sardine.setCredentials(user, password);
        if (!WebserviceURL.isEmpty()) {
            myUrl = WebserviceURL + "Dataupdate/";
        }
    }

    /**
     * 
     * @return 
     */
    public Sardine getSardine() {
        return sardine;
    }

    /**
     * 
     * @param sardine 
     */
    public void setSardine(Sardine sardine) {
        this.sardine = sardine;
    }

    /**
     * 
     * @param inputStream
     * @return
     * @throws IOException 
     */
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {

        //mit Java9
        //return in.readAllBytes();
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1) {
                    outputStream.write(buf, 0, readLen);
                }

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) {
                inputStream.close();
            } else {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    exception.addSuppressed(e);
                }
            }
        }
    }

    /**
     * 
     * @param file
     * @return 
     */
    public byte[] download(String file) {
        LOG.info("File=" + file);
        InputStream in = null;

        try {
            in = sardine.get(myUrl + file);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        byte[] b = null;
        try {
            b = readAllBytes(in);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return b;
    }

    /**
     * 
     * @param file
     * @param input
     * @return 
     */
    public byte[] upload(String file, byte[] input) {
        LOG.info("File=" + file);
        InputStream in = new ByteArrayInputStream(input);

        try {
            sardine.put(myUrl + file, in);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return download(file);
    }

    public boolean exists(String file) {
        boolean result = false;

        try {
            result = sardine.exists(myUrl + file);
            LOG.info("File=" + file + " exists.");
        } catch (IOException ex) {
            LOG.info("File=" + file + " does not exist or is not accessible.");
            //LOG.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    /**
     * 
     * @param file
     * @return 
     */
    public boolean delete(String file) {

        try {
            sardine.delete(myUrl + file);
            LOG.info("File=" + file + " deleted.");
            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        LOG.info("File=" + file + " delete failed.");
        return false;
    }

    /**
     * 
     * @param file
     * @return 
     */
    public boolean purge(String file) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        String toFile;
        toFile = dateFormat.format(date);

        try {
            sardine.move(myUrl + file, myUrl + "trash/" + toFile);
            LOG.info("File=" + file + " moved to trash - will be purged by asynchronous Thread");
            DropFile df = new DropFile(myUrl + "trash/" + toFile);
            df.start();
            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        LOG.info("File=" + file + " purge failed.");
        return false;
    }

    /**
     * 
     * @return 
     */
    public boolean shutdown() {
        try {
            sardine.shutdown();
            LOG.info("shutdown");
            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        LOG.info("shutdown failed.");
        return false;
    }

    /**
     * 
     * @param type
     * @return 
     */
    public ArrayList<String> list(String type) {
        ArrayList<String> files = new ArrayList<>();
        try {
            LOG.info("Start getList");
            List<DavResource> resources = sardine.list(myUrl);
            LOG.info("GotList");
            for (DavResource res : resources) {
                if (res.getName().endsWith(type)) {
                    files.add(res.getName());
                    LOG.info(res.getName());
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return files;
    }
}
