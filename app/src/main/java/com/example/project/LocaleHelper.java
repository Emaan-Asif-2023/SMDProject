package com.example.project;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class LocaleHelper {

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, "en");
        return setLocale(context, lang);
    }

    public static Context setLocale(Context context, String language) {
        persist(context, language);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            if (Build.VERSION.SDK_INT >= 25) {
                config.setLocales(new LocaleList(locale));
                context = context.createConfigurationContext(config);
            } else {
                resources.updateConfiguration(config, resources.getDisplayMetrics());
            }
        } else {
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        return context;
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        android.content.SharedPreferences preferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        return preferences.getString("language_code", defaultLanguage);
    }

    private static void persist(Context context, String language) {
        android.content.SharedPreferences preferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language_code", language);
        editor.apply();
    }
}