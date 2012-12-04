package eu.uberdust.myfragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import eu.uberdust.model.Capability;
import eu.uberdust.myfragments.listner.BypassClickListener;
import eu.uberdust.myfragments.listner.LightZoneClickListener;
import eu.uberdust.uClient.R;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MyCommandsAdapter extends ArrayAdapter<Capability> {
    Context context;
    int layoutResourceId;
    ArrayList<Capability> commands;
    private static int LIGHT_ZONE_TYPE = 0;
    private static int BYPASS_TYPE = 1;

    public MyCommandsAdapter(Context context, int layoutResourceId, ArrayList<Capability> commands) {
        super(context, layoutResourceId, commands);
        this.context = context;
        this.commands = commands;
    }

    @Override
    public View getView(final int position, View row, ViewGroup parent) {
        int theType = getItemViewType(position);
        if (row == null) {
            ViewHolder holder = null;
            if (theType == LIGHT_ZONE_TYPE) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_row_commands, parent, false);
                holder = lightZoneItem(position, row, parent);
            } else if (theType == BYPASS_TYPE) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_row_commands, parent, false);
                holder = bypassItem(position, row, parent);
            }
            row.setTag(holder);
        }
        return row;
    }

    private ViewHolder bypassItem(final int position, View row, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.textView1 = (TextView) row.findViewById(R.id.textView1);
        holder.buttonON = (Button) row.findViewById(R.id.button1);
        holder.buttonOFF = (Button) row.findViewById(R.id.button2);
        holder.imageView1 = (ImageView) row.findViewById(R.id.imageView1);

        holder.imageView1.setImageResource(R.drawable.lightbulb);
        holder.textView1.setText(commands.get(position).getName());

        final ViewHolder finalHolder = holder;

        String node = commands.get(position).getUrl().substring(commands.get(position).getUrl().indexOf("/node/"));
        if (getValue(commands.get(position).getUrl(), node)) {
            finalHolder.buttonON.setEnabled(false);
            finalHolder.buttonOFF.setEnabled(true);
        } else {
            finalHolder.buttonON.setEnabled(true);
            finalHolder.buttonOFF.setEnabled(false);
        }

        String buttonNode = commands.get(position).getUrl().substring(commands.get(position).getUrl().indexOf("/node/"));

        holder.buttonON.setOnClickListener(new BypassClickListener(context, buttonNode, finalHolder, 1));
        holder.buttonOFF.setOnClickListener(new BypassClickListener(context, buttonNode, finalHolder, 0));
        return holder;
    }

    private ViewHolder lightZoneItem(final int position, View row, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        holder.textView1 = (TextView) row.findViewById(R.id.textView1);
        holder.buttonON = (Button) row.findViewById(R.id.button1);
        holder.buttonOFF = (Button) row.findViewById(R.id.button2);
        holder.imageView1 = (ImageView) row.findViewById(R.id.imageView1);
        holder.imageView1.setImageResource(R.drawable.lightbulb);
        holder.textView1.setText(commands.get(position).getName());

        final ViewHolder finalHolder = holder;
        final ViewHolder finalHolder2 = holder;

        String node = commands.get(position).getUrl().substring(commands.get(position).getUrl().indexOf("/node/"));

        if (getValue(commands.get(position).getUrl(), commands.get(position).getName())) {
            finalHolder.buttonON.setEnabled(false);
            finalHolder.buttonOFF.setEnabled(true);
        } else {
            finalHolder.buttonON.setEnabled(true);
            finalHolder.buttonOFF.setEnabled(false);
        }

        final String path = commands.get(position).getName();
        final String uri = transform(commands.get(position).getUrl().substring(commands.get(position).getUrl().indexOf("/node/")));

        holder.buttonON.setOnClickListener(new LightZoneClickListener(context, path, uri, finalHolder, "1"));
        final ViewHolder finalHolder1 = holder;
        holder.buttonOFF.setOnClickListener(new LightZoneClickListener(context, path, uri, finalHolder, "0"));
        return holder;
    }

    private boolean getValue(String url, String node) {
        String val = getFromTAB(url, node);
        Log.d("VAL", val);
        return "1.0".equals(val);
    }


    public String getFromTAB(final String url, final String name) {
        try {
            Log.d("URL", url + "capability/urn:wisebed:node:capability:" + name + "/latestreading");
            return getStringContent(url + "capability/urn:wisebed:node:capability:" + name + "/latestreading").split("\t")[1];
        } catch (Exception e) {
            return "error";
        }
    }


    private String transform(String substring) {
        if (substring.contains("0.I.1")) return "99c";
        if (substring.contains("0.I.2")) return "2df";
        if (substring.contains("0.I.3")) return "42f";
        if (substring.contains("0.I.9")) return "4ec";
        if (substring.contains("0.I.11")) return "494";
        if (substring.contains("0x")) {
            return substring.substring(substring.indexOf("0x") + 2).replaceAll("/", "");
        } else {
            return substring;
        }
    }

    public static class ViewHolder {
        ImageView imageView1;
        TextView textView1;
        public Button buttonON;
        public Button buttonOFF;
    }

    @Override
    public int getViewTypeCount() {
        return 2;

    }

    @Override
    public int getItemViewType(int position) {
        if (commands.get(position).getName().equals("bypass")) {
            return BYPASS_TYPE;
        } else {
            return LIGHT_ZONE_TYPE;
        }
    }

    public static String getStringContent(String uri) throws Exception {

        try {

            URL url = new URL(uri);
            URLConnection urlconnection = url.openConnection();

            long l = urlconnection.getContentLength();

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            in.close();
            System.out.println("bufer=" + buffer.toString());
            return buffer.toString();

        } finally {
        }
    }


    public String getFromJSON(final String url, final String name) {
        String strval = null;
        try {
            strval = getStringContent(url + "capability/urn:wisebed:node:capability:" + name + "/json/limit/1");

            JSONObject jsonVal = new JSONObject(strval);
            JSONObject jsonArr = (JSONObject) jsonVal.getJSONArray("readings").get(0);

            String val = (String) jsonArr.get("stringReading");
            if (val.equals("")) {
                try {
                    val = Integer.toString((Integer) jsonArr.get("reading"));
                } catch (Exception e) {
                    val = Double.toString((Double) jsonArr.get("reading"));
                }
            }
            return val;
        } catch (Exception e) {
            return "error";
        }
    }

}