package weplay.auptsoft.locationtracker.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import weplay.auptsoft.locationtracker.HomeActivity;
import weplay.auptsoft.locationtracker.R;
import weplay.auptsoft.locationtracker.adapters.AlertItemAdapter;
import weplay.auptsoft.locationtracker.controllers.Utility;
import weplay.auptsoft.locationtracker.models.Alert;
import weplay.auptsoft.locationtracker.models.AppState;

/**
 * Created by Andrew on 11.2.19.
 */

public class AlertsFragment extends Fragment {
    RecyclerView alertsRecyclerView;
    ArrayList<Alert> alerts = new ArrayList<>();

    AlertItemAdapter alertItemAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    TextView noMessagesView;

    String email = "";
    String title = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        email = getArguments().getString("email");
        title = getArguments().getString("title");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        /*AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.getSupportActionBar().setTitle(title); */

        ((HomeActivity)getActivity()).toolbar.setTitle(title);

        alertsRecyclerView = (RecyclerView)view.findViewById(R.id.alert_recycler_view);
        noMessagesView = (TextView)view.findViewById(R.id.no_message_text_view);
        noMessagesView.setText("No "+title);

        //initializeTestData();

        initializeData();


        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeData();
            }
        });


        return view;
    }

    void initializeTestData() {
        alerts.clear();
        alerts.add(new Alert(0, "Message one", "This is content of the message", "", new GregorianCalendar()));
        alerts.add(new Alert(0, "Message two", "This is content of the message", "", new GregorianCalendar()));
        alerts.add(new Alert(0, "Message three", "This is content of the message", "", new GregorianCalendar()));
        alerts.add(new Alert(0, "Message four", "This is content of the message", "", new GregorianCalendar()));
        alerts.add(new Alert(0, "Message five", "Always turn off your phone at filling stations", "", new GregorianCalendar()));
    }

    void initializeData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting All "+title+"...");
        progressDialog.show();

        Utility.getAlerts(email, "", "", new Utility.OnAlertListener() {
            @Override
            public void onAlert(ArrayList<Alert> alertsList, String msg) {
                alerts = alertsList;
                //Toast.makeText(getContext(), msg+alertsList.size(), Toast.LENGTH_LONG).show();

                //alerts.add(new Alert(0, "Message one", "This is content of the message", "", new GregorianCalendar()));
                //alerts.add(new Alert(0, "Message two", "This is content of the message", "", new GregorianCalendar()));

                swipeRefreshLayout.setRefreshing(false);

                alertItemAdapter = new AlertItemAdapter(alerts, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                alertsRecyclerView.setAdapter(alertItemAdapter);
                alertsRecyclerView.setLayoutManager(linearLayoutManager);

                progressDialog.hide();

                //com.wang.avi.indicators.BallZigZagDeflectIndicator;
            }

            @Override
            public void onError(String errorMsg) {
                if(errorMsg.equals("no messages")) {
                    noMessagesView.setVisibility(alerts.size()<=0? View.VISIBLE : View.GONE);
                }
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();

                swipeRefreshLayout.setRefreshing(false);

                //https://drive.google.com/open?id=1RjVGKOHUvP4HmNorr11HFWvw8wn5drB7

                //https://drive.google.com/open?id=15jjwRJRBew5Bqr7q7tvzjjXFxSooQxGQ

                progressDialog.hide();
            }
        });
    }

    public static AlertsFragment newInstance(String email, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("title", title);

        AlertsFragment alertsFragment = new AlertsFragment();
        alertsFragment.setArguments(bundle);


        return alertsFragment;
    }
}
