package org.arquillian.spacelift.installation;

public interface InstallationStep {

    InstallationContext perform(InstallationContext previousState);

}
