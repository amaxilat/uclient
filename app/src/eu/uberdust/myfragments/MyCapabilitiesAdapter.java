package eu.uberdust.myfragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eu.uberdust.model.Capability;
import eu.uberdust.uClient.R;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MyCapabilitiesAdapter extends ArrayAdapter<Capability> {
    Context context;
    int layoutResourceId;
    ArrayList<Capability> capabilities;
    private ViewHolder finalHolder;

    public MyCapabilitiesAdapter(Context context, int layoutResourceId, ArrayList<Capability> capabilities) {
        super(context, layoutResourceId, capabilities);
        this.context = context;
        this.capabilities = capabilities;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        int theType = getItemViewType(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();

            row = inflater.inflate(R.layout.list_row_capabilities, parent, false);
            holder.textView1 = (TextView) row.findViewById(R.id.textView1);
            holder.textView2 = (TextView) row.findViewById(R.id.textView2);
            holder.imageView1 = (ImageView) row.findViewById(R.id.imageView1);

            holder.textView1.setText(capabilities.get(position).getName());
            holder.textView2.setText(capabilities.get(position).getValue());

            if (theType == 0) { //default
                holder.imageView1.setImageResource(R.drawable.ic_launcher);
            } else if (theType == 1) { //Temperature
                holder.imageView1.setImageResource(R.drawable.thermometer);
            } else if (theType == 2) { //Light
                holder.imageView1.setImageResource(R.drawable.lamp_pressed);
            } else if (theType == 3) { //pir
                holder.imageView1.setImageResource(R.drawable.pir);
            } else if (theType == 4) { //led
                holder.imageView1.setImageResource(R.drawable.led);
            } else if (theType == 5) { //humidity
                holder.imageView1.setImageResource(R.drawable.humidity);
            }


            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                updateValue(capabilities.get(position).getUrl(), capabilities.get(position).getName(), finalHolder.textView2);
//            }
//        }, 1000 * position + 100);

        final ViewHolder finalHolder = holder;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateValue(capabilities.get(position).getUrl(), capabilities.get(position).getName(), finalHolder.textView2);
            }
        });

        return row;
    }

    private void updateValue(final String url, final String name, final TextView textView2) {

        Toast toast = Toast.makeText(context, "Pressed Row", Toast.LENGTH_SHORT);
        String strval = null;
        try {
//            toast = Toast.makeText(context, url + "capability/urn:wisebed:node:capability:" + name + "/json/limit/1", Toast.LENGTH_LONG);
//            toast.show();

            strval = getStringContent(url + "capability/urn:wisebed:node:capability:" + name + "/json/limit/1");

            JSONObject jsonVal = new JSONObject(strval);
            JSONObject jsonArr = (JSONObject) jsonVal.getJSONArray("readings").get(0);

            String val = (String) jsonArr.get("stringReading");
//            toast = Toast.makeText(context, val, Toast.LENGTH_LONG);
//            toast.show();
            if (val.equals("")) {
                try {
                    val = Integer.toString((Integer) jsonArr.get("reading"));
                } catch (Exception e) {
                    val = Double.toString((Double) jsonArr.get("reading"));
                }
            }
            if (val == null) {
                toast = Toast.makeText(context, "Val is null", Toast.LENGTH_SHORT);
            }

            textView2.setText(val);
        } catch (Exception e) {
            toast = Toast.makeText(context, "Error:" + e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    static class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageView imageView1;
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        if (Pattern.compile(Pattern.quote("temperature"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return 1;
        }
        if (Pattern.compile(Pattern.quote("light"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find() || Pattern.compile(Pattern.quote("lamp"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return 2;
        }
        if (Pattern.compile(Pattern.quote("pir"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return 3;
        }
        if (Pattern.compile(Pattern.quote("led"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return 4;
        }
        if (Pattern.compile(Pattern.quote("humidity"), Pattern.CASE_INSENSITIVE).matcher(capabilities.get(position).getName()).find()) {
            return 5;
        }
        return 0;
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


//
//            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet(uri);
////            HttpResponse response = client.execute(request);
//            client.execute(request);
//            StringBuilder sb = new StringBuilder("Response");
////            InputStream ips = response.getEntity().getContent();
////            BufferedReader buf = new BufferedReader(new InputStreamReader(ips));
////
////            String s;
////            while (true) {
////                s = buf.readLine();
////                if (s == null || s.length() == 0)
////                    break;
////                sb.append(s);
////
////            }
////            buf.close();
////            ips.close();
//            return sb.toString();

        } finally {
            // any cleanup code...
        }
    }
}