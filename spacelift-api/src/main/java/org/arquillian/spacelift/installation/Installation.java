package org.arquillian.spacelift.installation;

import java.util.Collection;
import java.util.List;

import org.arquillian.spacelift.tool.Tool;

public interface Installation {

    String id();

    boolean supports(String operationSystem);

    boolean isInstalled();

    Collection<Class<? extends Tool<?>>> provides();

    Collection<Class<? extends Tool<?>>> dependsOn();

    List<InstallationStep> install();

    InstallationSource getSource();
}
