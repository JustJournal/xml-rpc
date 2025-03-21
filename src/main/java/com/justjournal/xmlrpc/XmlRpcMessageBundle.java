/*
    Copyright (c) 2006 by Redstone Handelsbolag
    All Rights Reserved.

    The copyright to the source code herein is the property of
    Redstone Handelsbolag. The source code may be used and/or copied only
    with written permission from Redstone or in accordance with
    the terms and conditions stipulated in the agreement/contract
    under which the source code has been supplied.
*/

package com.justjournal.xmlrpc;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class XmlRpcMessageBundle {
    private static final String BUNDLE_NAME = "com.justjournal.xmlrpc.XmlRpcMessages";
    private static ResourceBundle RESOURCE_BUNDLE;

    static {
        try {
            RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
        } catch (MissingResourceException e) {
            System.err.println("Warning: " + BUNDLE_NAME + " not found. Using fallback messages.");
            RESOURCE_BUNDLE = null;
        }
    }

    private XmlRpcMessageBundle() {
    }

    /**
     * Retrieves a localized string from the resource bundle using the provided key.
     * If the resource bundle is not available or the key is not found, it returns
     * the key enclosed in exclamation marks.
     *
     * @param key The key to look up in the resource bundle.
     * @return The localized string if found, otherwise '!key!'.
     */
    public static String getString(String key) {
        if (RESOURCE_BUNDLE == null) {
            return '!' + key + '!';
        }
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
