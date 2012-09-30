package eu.uberdust.myfragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import eu.uberdust.model.Capability;
import eu.uberdust.uClient.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MyCommandsAdapter extends ArrayAdapter<Capability> {
    Context context;
    int layoutResourceId;
    ArrayList<Capability> commands;

    public MyCommandsAdapter(Context context, int layoutResourceId, ArrayList<Capability> commands) {
        super(context, layoutResourceId, commands);
        this.context = context;
        this.commands = commands;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        int theType = getItemViewType(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();


            if (theType == 0) {
                row = inflater.inflate(R.layout.list_row_commands, parent, false);
                holder.textView1 = (TextView) row.findViewById(R.id.textView1);
                holder.buttonON = (Button) row.findViewById(R.id.button1);
                holder.buttonOFF = (Button) row.findViewById(R.id.button2);

                holder.textView1.setText(commands.get(position).getName());

                final ViewHolder finalHolder = holder;
                final ViewHolder finalHolder2 = holder;
                holder.buttonON.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String zone = commands.get(position).getName().replaceAll("lz", "");
//                        finalHolder1.buttonON.setText("Zone:" + zone);
//                        http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x42f/payload/7f,69,70,33,51,2,ff,ff,93,6c,7a,3{zone},30,a,30,31
                        String newBase = commands.get(position).getUrl().substring(0, commands.get(position).getUrl().indexOf("rest"));
//                        finalHolder1.buttonON.setText("newBase:" + newBase);
                        String node = transform(commands.get(position).getUrl().substring(commands.get(position).getUrl().indexOf("/node/")));
//                        finalHolder1.buttonON.setText("Node:" + node);
//                        finalHolder2.textView1.setText("Zone:" + zone + "," + node);
                        try {
//                            String url = newBase + "/sendCommand/destination/urn:wisebed:ctitestbed:" + node + "/payload/7f,69,70,33,51,2,ff,ff,93,6c,7a,3" + zone + ",30,a,30,31";
                            String strval = getStringContent(newBase + "rest/sendCommand/destination/urn:wisebed:ctitestbed:" + node + "/payload/7f,69,70,33,51,2,ff,ff,93,6c,7a,3" + zone + ",31,a,30,31");
//                            finalHolder2.textView1.setText(strval);
                            Toast toast = Toast.makeText(context, strval, Toast.LENGTH_SHORT);
                            toast.show();

                        } catch (Exception e) {
                            finalHolder2.textView1.setText("Error:" + e.getMessage());
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        finalHolder.buttonON.setEnabled(false);
                        finalHolder.buttonOFF.setEnabled(true);
                    }
                });
                final ViewHolder finalHolder1 = holder;
                holder.buttonOFF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String zone = commands.get(position).getName().replaceAll("lz", "");
//                        finalHolder1.buttonON.setText("Zone:" + zone);
//                        http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x42f/payload/7f,69,70,33,51,2,ff,ff,93,6c,7a,3{zone},30,a,30,31
                        String newBase = commands.get(position).getUrl().substring(0, commands.get(position).getUrl().indexOf("rest"));
//                        finalHolder1.buttonON.setText("newBase:" + newBase);
                        String node = transform(commands.get(position).getUrl().substring(commands.get(position).getUrl().indexOf("/node/")));
//                        finalHolder1.buttonON.setText("Node:" + node);

                        try {
                            String url = newBase + "/sendCommand/destination/urn:wisebed:ctitestbed:" + node + "/payload/7f,69,70,33,51,2,ff,ff,93,6c,7a,3" + zone + ",30,a,30,31";
                            String strval = getStringContent(newBase + "rest/sendCommand/destination/urn:wisebed:ctitestbed:" + node + "/payload/7f,69,70,33,51,2,ff,ff,93,6c,7a,3" + zone + ",30,a,30,31");
                            Toast toast = Toast.makeText(context, strval, Toast.LENGTH_SHORT);
                            toast.show();
//
                        } catch (Exception e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        finalHolder.buttonOFF.setEnabled(false);
                        finalHolder.buttonON.setEnabled(true);
                    }


                });
            } else if (theType == 1) {

            }


            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        return row;
    }

    private String transform(String substring) {
        if (substring.contains("0.I.11")) {
            return "0x494";
        } else if (substring.contains("0.I.2")) {
            return "0x2df";
        } else if (substring.contains("0.I.3")) {
            return "0x42f";
        } else if (substring.contains("0.I.9")) {
            return "0x4ec";
        } else if (substring.contains("0.I.1")) {
            return "0x99c";
        }
        return "";
    }

    static class ViewHolder {
        TextView textView1;
        Button buttonON;
        Button buttonOFF;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        /*if(values.get(position).contains("1") || values.get(position).contains("2") ||values.get(position).contains("3")){
              return 1;
          }
          else return 0;*/
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

        } finally {
            // any cleanup code...
        }
    }
}