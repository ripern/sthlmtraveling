package com.markupartist.sthlmtraveling;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.markupartist.sthlmtraveling.service.DataMigrationService;
import com.markupartist.sthlmtraveling.utils.ErrorReporter;

public class MyApplication extends Application {
    public static String ANALYTICS_KEY = "JUSQKB45DEN62VRQVBU9";
    public static String APP_VERSION;
    static String TAG = "StApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        final ErrorReporter reporter = ErrorReporter.getInstance();
        reporter.init(getApplicationContext());

        reloadLocaleForApplication();

        DataMigrationService.startService(this);

        setAppVersion();
    }

    protected void setAppVersion() {
        if (APP_VERSION == null) {
            PackageManager pm = getPackageManager();
            try {
                PackageInfo pi = pm.getPackageInfo(getPackageName(),
                        0);
                APP_VERSION = pi.versionName;
            } catch (NameNotFoundException e) {
                Log.e(TAG, "Could not get the package info.");
            }
        }        
    }
    
    /* (non-Javadoc)
     * @see android.app.Application#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        reloadLocaleForApplication();
    }

    public void reloadLocaleForApplication() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        String language = sharedPreferences
                .getString("prefered_language_preference", "system");
        Log.d(TAG, "Preferred language: " + language);

        Locale locale = null;
        if ("sv".equals(language)) {
            locale = new Locale("sv", "SE");
        } else if ("es".equals(language)) {
            locale = new Locale("es", "ES");
        } else if ("en".equals(language)) {
            locale = Locale.ENGLISH;
        }

        if (locale != null) {
            Log.d(TAG, "setting locale " + locale);
            Resources res = getResources(); 
            DisplayMetrics dm = res.getDisplayMetrics(); 
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, dm);
        }
    }
    
}
