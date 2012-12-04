package eu.uberdust.myfragments.listner;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import eu.uberdust.myfragments.MyCommandsAdapter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 11/25/12
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateValueClickListener implements View.OnClickListener {

    private Context context;
    private MyCommandsAdapter.ViewHolder finalHolder;
    private String url;
    private String name;
    private TextView textView2;

    public UpdateValueClickListener(Context context, String url, String name, TextView textView2) {
        this.context = context;
        this.url = url;
        this.name = name;
        this.textView2 = textView2;
    }

    @Override
    public void onClick(View view) {

        Toast toast = Toast.makeText(context, "Pressed Row", Toast.LENGTH_SHORT);
        String strval = null;
        try {
//            textView2.setText(getFromJSON(url, name));
            textView2.setText(getFromTAB(url, name));
        } catch (Exception e) {
            toast = Toast.makeText(context, "Error:" + e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getFromTAB(final String url, final String name) {
        try {
            return getStringContent(url + "capability/urn:wisebed:node:capability:" + name + "/latestreading").split("\t")[1];
        } catch (Exception e) {
            return "error";
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

    public static String getStringContent(String uri) throws Exception {
        try {
            URL url = new URL(uri);
            url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            in.close();
            return buffer.toString();

        } catch (Exception e) {
        }
        return "";
    }
}
