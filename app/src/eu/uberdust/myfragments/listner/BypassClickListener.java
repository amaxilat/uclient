package eu.uberdust.myfragments.listner;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import eu.uberdust.myfragments.MyCommandsAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 11/25/12
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class BypassClickListener implements View.OnClickListener {

    private Context context;
    private String buttonNode;
    private MyCommandsAdapter.ViewHolder finalHolder;
    private int state;

    public BypassClickListener(Context context, String buttonNode, MyCommandsAdapter.ViewHolder finalHolder, int state) {
        this.context = context;
        this.buttonNode = buttonNode;
        this.finalHolder = finalHolder;
        this.state = state;
    }

    @Override
    public void onClick(View view) {
        try {
            Toast toast = Toast.makeText(context, "Command Sent: " + changeBypass(buttonNode, state), Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (state == 1) {
            finalHolder.buttonON.setEnabled(false);
            finalHolder.buttonOFF.setEnabled(true);
        } else {
            finalHolder.buttonON.setEnabled(true);
            finalHolder.buttonOFF.setEnabled(false);
        }
    }

    public String changeBypass(String node, int i) {
        String[] parts = node.split(":");
        node = parts[parts.length - 2] + parts[parts.length - 1];
        node = node.replaceAll(":", "").replaceAll("/", "");
        try {
            String inurl = "http://150.140.16.31/api/v1/foi?identifier=" + node;
            URL getURL = new URL(inurl);
            URLConnection con = getURL.openConnection();
            InputStream out = con.getInputStream();
            StringBuilder response = new StringBuilder();
            int c = out.read();
            while (c != -1) {
                response.append((char) c);
                c = out.read();
            }

            //Toast toast = Toast.makeText(this.context, response.toString(), Toast.LENGTH_SHORT);
            //toast.show();
            if (response.toString().equals("No information found for this FOI.")) {
                response = new StringBuilder("[{\"identifier\":\"" + node + "\"}]");
            }

            try {
                JSONObject responseJSON = new JSONObject(new JSONArray(response.toString()).getString(0));
                responseJSON.put("bypass", i == 0 ? "false" : "true");
                responseJSON.remove("_id");

                URL postURL = new URL("http://150.140.16.31/api/v1/foi");
                HttpURLConnection con2 = (HttpURLConnection) postURL.openConnection();
                con2.setRequestMethod("POST");
                con2.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con2.getOutputStream());
                writer.write(responseJSON.toString().toCharArray());
                writer.flush();
                writer.close();
                return con2.getResponseCode() == 201 ? "Ok!" : "Error!";
            } catch (Exception e) {
                return "error";
            }
        } catch (Exception e) {
            return "error";
        }

    }

}
