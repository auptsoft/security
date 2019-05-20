package weplay.auptsoft.locationtracker.adapters;


import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import weplay.auptsoft.locationtracker.R;
import weplay.auptsoft.locationtracker.models.Alert;

/**
 * Created by Andrew on 10.2.19.
 */

public class AlertItemAdapter extends RecyclerView.Adapter<AlertItemAdapter.AlertItemAdapterViewHolder> {

    ArrayList<Alert> alerts;
    Context context;

    public AlertItemAdapter(ArrayList<Alert> alerts, Context context) {
        this.alerts = alerts;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    @Override
    public AlertItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert, parent, false);
        return new AlertItemAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlertItemAdapterViewHolder holder, int position) {
        holder.title.setText(alerts.get(position).getTitle());

        holder.content.setText(alerts.get(position).getContent());
        holder.dateTime.setText(alerts.get(position).getDateTimeString());
    }

    public class AlertItemAdapterViewHolder extends RecyclerView.ViewHolder {
        //ImageView itemImage;
        TextView title, content, dateTime;
        public AlertItemAdapterViewHolder(View itemView) {
            super(itemView);
            //itemImage = (ImageView)itemView.findViewById(R.id.item_simple_image);
            title = (TextView)itemView.findViewById(R.id.item_alert_title);
            content = (TextView)itemView.findViewById(R.id.item_alert_content);
            dateTime = (TextView)itemView.findViewById(R.id.item_alert_date_time);
        }
    }
}
