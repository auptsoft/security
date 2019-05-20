package weplay.auptsoft.locationtracker;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import weplay.auptsoft.locationtracker.databinding.ActivityAlertDialogBinding;
import weplay.auptsoft.locationtracker.databinding.ActivityEnsureSettingsBinding;
import weplay.auptsoft.locationtracker.models.Alert;
import weplay.auptsoft.locationtracker.models.AppState;

/**
 * Created by Andrew on 14.2.19.
 */

public class AlertDialogActivity extends Activity {

    ActivityAlertDialogBinding activityAlertDialogBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAlertDialogBinding = DataBindingUtil.setContentView(this, R.layout.activity_alert_dialog);

        //debug start
        /*AppState.currentMessages = new ArrayList<>();
        AppState.currentMessages.add(new Alert(0, "title", "this is the content of the message", "", new GregorianCalendar()));
        AppState.currentMessages.add(new Alert(0, "title", "this is the content of the message", "", new GregorianCalendar()));
        AppState.currentMessages.add(new Alert(0, "title", "this is the content of the message", "", new GregorianCalendar()));
        final String type = "message"; */
        //debug end

        final String type = getIntent().getStringExtra("type");
        if(type.equals("alert")) {
            if(AppState.currentAlerts != null && AppState.currentAlerts.size()>0) {
                activityAlertDialogBinding.setAlert(AppState.currentAlerts.get(0));

                activityAlertDialogBinding.alertDateTime.setText(AppState.currentAlerts.get(0).getDateTimeString());
                if (AppState.currentAlerts.size()>1) {
                    activityAlertDialogBinding.numberOfMsgView.setText(""+AppState.currentAlerts.size()+"  more");
                }
            }
        } else if (type.equals("message")) {
            if(AppState.currentMessages != null && AppState.currentMessages.size()>0) {
                activityAlertDialogBinding.setAlert(AppState.currentMessages.get(0));
                activityAlertDialogBinding.alertDateTime.setText("On "+AppState.currentMessages.get(0).getDateTimeString());
                if (AppState.currentMessages.size()>1) {
                    activityAlertDialogBinding.numberOfMsgView.setText(""+AppState.currentMessages.size()+"  more");
                }
            }
        }

        activityAlertDialogBinding.numberOfMsgView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                intent.putExtra("type", type);

                startActivity(intent);

                finish();
            }
        });
    }
}
