package org.arquillian.spacelift.installation;

import java.io.File;

public interface InstallationContext {

    File getWorkspace();

    File getInstallationDir();

}
