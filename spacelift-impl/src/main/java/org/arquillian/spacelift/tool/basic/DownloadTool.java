/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.spacelift.tool.basic;

import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.tool.Tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File downloader
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public class DownloadTool extends Tool<Object, File> {

    private URL url;

    private File output;

    private int timeout = 5000;
    private Map<String, String> properties = new HashMap<String, String>();
    private boolean followRedirects = true;

    @Override
    public Collection<String> aliases() {
        return Arrays.asList("download");
    }

    public DownloadTool from(String url) throws IllegalArgumentException {
        try {
            return from(new URL(url));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public DownloadTool from(URL url) {
        this.url = url;
        return this;
    }

    public DownloadTool to(String filePath) {
        return to(new File(filePath));
    }

    public DownloadTool to(File file) {
        this.output = file;
        return this;
    }

    public DownloadTool timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public DownloadTool property(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public DownloadTool followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    @Override
    protected File process(Object input) throws Exception {
        if (url == null) {
            throw new IllegalStateException("Source URL was not set");
        }
        if (output == null) {
            throw new IllegalStateException("Destination file was not set");
        }

        tryDownload(url);

        return output;
    }

    private void tryDownload(URL url) {
        tryDownload(url, new ArrayList<String>());
    }

    private void tryDownload(URL url, List<String> redirectUrls) {
        String urlExternalForm = url.toExternalForm();
        if (redirectUrls.contains(urlExternalForm)) {
            throw new IllegalStateException("The site contains an infinite redirect loop! Duplicate url: " +
                    urlExternalForm);
        } else {
            redirectUrls.add(urlExternalForm);
        }

        InputStream is = null;
        FileOutputStream fos = null;

        try {
            try {
                URLConnection connection = url.openConnection(); // connect
                connection.setReadTimeout(timeout);

                for (Map.Entry<String, String> property : properties.entrySet()) {
                    connection.setRequestProperty(property.getKey(), property.getValue());
                }

                if (connection instanceof HttpURLConnection) {
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    int responseCode = httpConnection.getResponseCode();
                    if (responseCode > 300 && responseCode < 400 && followRedirects) {
                        String redirectLocation = connection.getHeaderField("Location");
                        if (redirectLocation == null || redirectLocation.equals("")) {
                            throw new IllegalStateException("The site response code was a redirect one (" +
                                    responseCode + ") but no 'Location' header was sent.");
                        }
                        tryDownload(new URL(redirectLocation));
                        return;
                    }
                }

                is = connection.getInputStream(); // get connection inputstream
                fos = new FileOutputStream(output); // open outputstream to local file

                byte[] buffer = new byte[4096]; // declare 4KB buffer
                int read;

                // while we have availble data, continue downloading and storing to local file
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }

            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
        } catch (IOException e) {
            throw new ExecutionException(e, "Unable to download from {0} to {1}", url, output);
        }
    }
}
