package weplay.auptsoft.locationtracker.controllers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import weplay.auptsoft.locationtracker.models.AppState;

/**
 * Created by Andrew on 6.2.19.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "boot complete", Toast.LENGTH_SHORT).show();
        AppState.sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);
        boolean b = AppState.sharedPreferences.getBoolean(AppState.WORK_IN_BACKGROUND_PREF, false);
        if(b) {
            Intent serviceIntent = new Intent(context, BackService.class);
            context.startService(serviceIntent);
        } else {
            Toast.makeText(context, "work in background not active", Toast.LENGTH_LONG).show();
        }
    }
}
