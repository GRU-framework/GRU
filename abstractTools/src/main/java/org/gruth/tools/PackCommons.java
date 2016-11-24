package org.gruth.tools;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Common data and utilities for this package.
 * @author bamade
 */
// Date: 10/05/15

public class PackCommons {
    /**
     * The current JUL logger
     */
    public static final Logger CURLOG = Logger.getLogger("org.gruth.tools") ;
    /**
     * the internationalisation resource Bundle
     * used to format Assertion messages.
     * The name of the bundle  should be specified
     * with System property <TT>gruth.tools.bundle</TT>
     * (otherwise no internationalisation will be performed)
     */
    public static final ResourceBundle I18N_BUNDLE ;

    static {
        ResourceBundle tempResource = null ;
        try {
            String bundleName = System.getProperty("gruth.tools.bundle") ;
            if(bundleName != null) {

                tempResource = ResourceBundle.getBundle(bundleName) ;
            }

        } catch (Exception exc) {
            CURLOG.warning(" i18N Bundle" +exc);
        }
        I18N_BUNDLE = tempResource ;
    }

    /**
     * utility for formatting message.
     * if internationalisation bundle is set it will be used to
     * format messages, otherwise they will be formatted
     * "as is" .
     * @param message the MessageFormat used for formating
     * @param args the arguments to format
     * @return the formatted message)
     */
    public static String formatMessage(String message, Object... args) {
        String format = message ;
        if(PackCommons.I18N_BUNDLE != null) {
            try {
                format = PackCommons.I18N_BUNDLE.getString(message);
            } catch (Exception exc) {
                PackCommons.CURLOG.warning(" no i18N key found for :" + message);
            }
        }
        return MessageFormat.format(format, args) ;
    }
}
