package org.arquillian.spacelift.tool.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.arquillian.spacelift.tool.InternalTool;

public class DownloadTool implements InternalTool<DownloadTool, File> {

    private URL url;

    private File output;

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

    @Override
    public Callable<File> getCallable() {

        if (url == null) {
            throw new IllegalStateException("Source URL was not set");
        }
        if (output == null) {
            throw new IllegalStateException("Destination file was not set");
        }

        return new Callable<File>() {
            @Override
            public File call() throws Exception {
                InputStream is = null;
                FileOutputStream fos = null;

                try {
                    URLConnection urlConn = url.openConnection();// connect

                    is = urlConn.getInputStream(); // get connection inputstream
                    fos = new FileOutputStream(output); // open outputstream to local file

                    byte[] buffer = new byte[4096]; // declare 4KB buffer
                    int len;

                    // while we have availble data, continue downloading and storing to local file
                    while ((len = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
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

                return output;
            }

        };
    }

}
