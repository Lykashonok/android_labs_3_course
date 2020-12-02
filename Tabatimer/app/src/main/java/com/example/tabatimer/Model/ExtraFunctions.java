package com.example.tabatimer.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.tabatimer.MainActivity;

public class ExtraFunctions {
    public static void triggerRebirth(Context context/*, Intent nextIntent*/) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("KEY_RESTART_INTENT", nextIntent);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }

        Runtime.getRuntime().exit(0);
    }
}
