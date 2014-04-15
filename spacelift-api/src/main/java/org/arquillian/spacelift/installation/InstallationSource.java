package org.arquillian.spacelift.installation;

public interface InstallationSource {

    boolean isCached();

    void download();
}
