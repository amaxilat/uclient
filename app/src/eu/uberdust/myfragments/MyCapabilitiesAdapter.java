package eu.uberdust.myfragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eu.uberdust.model.Capability;
import eu.uberdust.myfragments.listner.UpdateValueClickListener;
import eu.uberdust.uClient.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MyCapabilitiesAdapter extends ArrayAdapter<Capability> {
    Context context;
    int layoutResourceId;
    ArrayList<Capability> capabilities;
    private ViewHolder finalHolder;
    Activity thatActivity;
    private static final int OTHER_CAPABILITY = 0;
    private static final int TEMPERATURE_CAPABILITY = 1;
    private static final int LIGHT_CAPABILITY = 2;
    private static final int PIR_CAPABILITY = 3;
    private static final int LED_CAPABILITY = 4;
    private static final int HUMIDITY_CAPABILITY = 5;
    private static final int CH4_CAPABILITY = 6;

    public MyCapabilitiesAdapter(Context context, int layoutResourceId, ArrayList<Capability> capabilities, Activity thatActivity) {
        super(context, layoutResourceId, capabilities);
        this.context = context;
        this.capabilities = capabilities;
        this.thatActivity = thatActivity;
    }

    @Override
    public View getView(final int position, View row, ViewGroup parent) {
        int theType = getItemViewType(position);
        if (row == null) {
            ViewHolder holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row_capabilities, parent, false);
            holder.textView1 = (TextView) row.findViewById(R.id.textView1);
            holder.textView2 = (TextView) row.findViewById(R.id.textView2);
            holder.imageView1 = (ImageView) row.findViewById(R.id.imageView1);
            holder.textView1.setText(capabilities.get(position).getName());
            holder.textView2.setText(capabilities.get(position).getValue());

            if (theType == OTHER_CAPABILITY) { //default
                holder.imageView1.setImageResource(R.drawable.ic_launcher);
            } else if (theType == TEMPERATURE_CAPABILITY) { //Temperature
                holder.imageView1.setImageResource(R.drawable.thermometer);
            } else if (theType == LIGHT_CAPABILITY) { //Light
                holder.imageView1.setImageResource(R.drawable.lamp_pressed);
            } else if (theType == PIR_CAPABILITY) { //pir
                holder.imageView1.setImageResource(R.drawable.pir);
            } else if (theType == LED_CAPABILITY) { //led
                holder.imageView1.setImageResource(R.drawable.led);
            } else if (theType == HUMIDITY_CAPABILITY) { //humidity
                holder.imageView1.setImageResource(R.drawable.humidity);
            } else if (theType == CH4_CAPABILITY) { //humidity
                holder.imageView1.setImageResource(R.drawable.ch4);
            }
            row.setTag(holder);

            final ViewHolder finalHolder = holder;
            row.setOnClickListener(new UpdateValueClickListener(context, capabilities.get(position).getUrl(), capabilities.get(position).getName(), finalHolder.textView2));
        }

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                updateValue(capabilities.get(position).getUrl(), capabilities.get(position).getName(), finalHolder.textView2);
//            }
//        }, 1000 * position + 100);

        return row;
    }


    static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageView imageView1;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        if (Pattern.compile(Pattern.quote("temperature"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return TEMPERATURE_CAPABILITY;
        }
        if (Pattern.compile(Pattern.quote("light"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find() || Pattern.compile(Pattern.quote("lamp"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return LIGHT_CAPABILITY;
        }
        if (Pattern.compile(Pattern.quote("pir"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return PIR_CAPABILITY;
        }
        if (Pattern.compile(Pattern.quote("led"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return LED_CAPABILITY;
        }
        if (Pattern.compile(Pattern.quote("humidity"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return HUMIDITY_CAPABILITY;
        }
        if (Pattern.compile(Pattern.quote("ch4"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return CH4_CAPABILITY;
        }
        return OTHER_CAPABILITY;
    }


}