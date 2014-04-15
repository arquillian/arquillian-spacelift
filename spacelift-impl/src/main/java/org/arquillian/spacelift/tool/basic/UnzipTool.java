package org.arquillian.spacelift.tool.basic;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.arquillian.spacelift.tool.InternalTool;

public class UnzipTool implements InternalTool<UnzipTool, File> {

    private File source;
    private File dest;

    @Override
    public Collection<String> aliases() {
        return Arrays.asList("unzip");
    }

    public UnzipTool from(String pathToFile) {
        return from(new File(pathToFile));
    }

    public UnzipTool from(File source) {
        this.source = source;
        return this;
    }

    public UnzipTool to(String pathToDestination) {
        return to(new File(pathToDestination));
    }

    public UnzipTool to(File destination) {
        this.dest = destination;
        return this;
    }

    @Override
    public Callable<File> getCallable() {

        return new Callable<File>() {

            @Override
            public File call() throws Exception {
                try {
                    ZipFile zipFile = new ZipFile(source);
                    zipFile.extractAll(dest.getAbsolutePath());
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                return dest;
            }
        };
    }

}
