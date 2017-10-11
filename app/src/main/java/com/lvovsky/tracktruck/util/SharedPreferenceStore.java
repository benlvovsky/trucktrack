package com.lvovsky.tracktruck.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by piyush on 30/09/16.
 */
public class SharedPreferenceStore {
    private static final String PREF_NAME = "com.hypertrack.example_android";
    private static final String DRIVER_ID_KEY = "driver_id";
    private static final String VISIT_ACTION_ID_KEY = "vist_action_id";
    private static final String STOPOVER_ACTION_ID_KEY = "stop_action_id";
    private static final String ACTION_ID_KEY = "action_id";
    private static final String USE_CASE_KEY = "use_case";



    public static void setActionID(Context context, String actionID) {
        if (TextUtils.isEmpty(actionID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(ACTION_ID_KEY, actionID);
        editor.commit();
    }

    public static void setUseCase(Context context, int useCase) {
        if (useCase == 0)
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putInt(USE_CASE_KEY, useCase);
        editor.commit();
    }

    public static void setDriverId(Context context, String driverID) {
        if (TextUtils.isEmpty(driverID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(DRIVER_ID_KEY, driverID);
        editor.commit();
    }

    public static String getDriverId(Context context) {
        return getSharedPreferences(context).getString(DRIVER_ID_KEY, null);
    }

    public static int getUseCase(Context context) {
        return getSharedPreferences(context).getInt(USE_CASE_KEY, 0);
    }

    public static String getActionID(Context context) {
        return getSharedPreferences(context).getString(ACTION_ID_KEY, null);
    }

    public static void clearIDs(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(DRIVER_ID_KEY);
        editor.remove(ACTION_ID_KEY);
        editor.remove(STOPOVER_ACTION_ID_KEY);
        editor.remove(VISIT_ACTION_ID_KEY);
        editor.remove(USE_CASE_KEY);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }
}
