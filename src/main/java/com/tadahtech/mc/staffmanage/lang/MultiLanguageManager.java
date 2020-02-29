package com.tadahtech.mc.staffmanage.lang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class MultiLanguageManager {

    public static final Locale DEFAULT = Locale.ENGLISH;
    public static final Locale NORWEGIAN = new Locale("no");

    private static final Locale[] OTHERLOCALES = new Locale[]{
      Locale.GERMAN,
      NORWEGIAN,
    };

    private static final Map<Locale, ResourceBundle> localeResourceBundles = Collections.synchronizedMap(new HashMap<Locale, ResourceBundle>());

    public static void init(String path) {
        try {
            for (Locale loc : OTHERLOCALES) {
                if (loc == Locale.GERMAN || loc == NORWEGIAN) {
                    continue;
                }

                ResourceBundle bundle = ResourceBundle.getBundle(path, loc, new UTF8Handler());
                localeResourceBundles.put(loc, bundle);
            }

        } catch (MissingResourceException e) {
            throw new RuntimeException(e);
        }
    }

    public static Locale getPlayerLocale() {
        return DEFAULT;
    }

    public static ResourceBundle getResourceBundle(Locale locale) {
        synchronized (localeResourceBundles) {
            if (localeResourceBundles.containsKey(locale)) {
                return localeResourceBundles.get(locale);
            } else {
                return localeResourceBundles.get(Locale.ENGLISH);
            }
        }
    }

    public static ResourceBundle getBestResourceBundle(Locale locale) {
        ResourceBundle bundle = getResourceBundle(locale);

        if (bundle == null && !locale.equals(DEFAULT)) {
            bundle = getResourceBundle(DEFAULT);
        }

        if (bundle == null && !locale.equals(Locale.ENGLISH)) {
            bundle = getResourceBundle(Locale.ENGLISH);
        }

        return bundle;
    }

    public static String getVersion(MessageKey key, Locale locale) {
        if (locale == null) {
            locale = DEFAULT;
        }

        if (locale.equals(DEFAULT)) {
            if (key.getEnglishVersion() != null) {
                return key.getEnglishVersion();
            }
        }

        ResourceBundle bundle = getBestResourceBundle(locale);
        if (bundle == null) {
            return null;
        }

        try {
            return bundle.getString(key.getKey());

        } catch (MissingResourceException ex) {
            return key.getEnglishVersion();
        }
    }

}