package org.arquillian.nativeplatform.process.impl;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ExtensionLoadTest {

    @Test
    public void ensureExtensionCanBeLoaded() {
        // this does nothing
        Assert.assertTrue(true);
    }
}
