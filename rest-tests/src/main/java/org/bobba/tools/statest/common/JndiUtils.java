package org.bobba.tools.statest.common;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;


public class JndiUtils {

    private static InitialContext NAMING_CONTEXT;

    public static <T> T lookupJndi(String jndiName, String jndiProviderUrl) {
        try {
            return (T) initializeNamingContext(jndiProviderUrl).lookup(jndiName);
        } catch (Exception e) {
            throw new RuntimeException("Jndi lookup failed", e);
        }
    }

    private static Context initializeNamingContext(String jndiProviderUrl) throws NamingException {
        if (NAMING_CONTEXT == null) {
            NAMING_CONTEXT = new InitialContext(jndiProperties(jndiProviderUrl));
        }
        return NAMING_CONTEXT;
    }


    public static Properties jndiProperties(String providerUrl) {
        Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        properties.setProperty("java.naming.provider.url", providerUrl);
        properties.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        return properties;
    }
}
