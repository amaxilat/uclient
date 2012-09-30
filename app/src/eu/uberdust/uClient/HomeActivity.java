package eu.uberdust.uClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import eu.uberdust.model.Capability;
import eu.uberdust.model.Room;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;


public class HomeActivity extends Activity {

    private String contents;
    private SharedPreferences settings;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        try {
            Class strictModeClass = Class.forName("android.os.StrictMode");
            Class strictModeThreadPolicyClass = Class.forName("android.os.StrictMode$ThreadPolicy");
            Object laxPolicy = strictModeThreadPolicyClass.getField("LAX").get(null);
            Method method_setThreadPolicy = strictModeClass.getMethod(
                    "setThreadPolicy", strictModeThreadPolicyClass);
            method_setThreadPolicy.invoke(null, laxPolicy);
        } catch (Exception e) {

        }

        contents = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tv = (TextView) findViewById(R.id.urlscannedtextview);

        Button startbutton = (Button) findViewById(R.id.loadbutton);
        Button rescanbutton = (Button) findViewById(R.id.rescanbutton);
        Button loadbutton = (Button) findViewById(R.id.loadHistory);
        rescanbutton.setEnabled(true);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!contents.equals("")) {

                    buildData(contents);
                }
            }
        });

        rescanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        loadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlsAll = settings.getString("positions", "");
//                Toast.makeText(getApplicationContext(), "URLS:" + urlsAll, Toast.LENGTH_SHORT);


//                Toast.makeText(view.getContext(), "Check History", Toast.LENGTH_SHORT).show();
                AlertDialog dialog;

//                String urlsAll = settings.getString("positions", "");
//                Toast.makeText(view.getContext(), "URLS:" + urlsAll, Toast.LENGTH_SHORT).show();
                final CharSequence[] items = new CharSequence[urlsAll.split(",").length];
                final CharSequence[] urls = new CharSequence[urlsAll.split(",").length];
                int count = 0;

                for (String url : urlsAll.split(",")) {

                    if (!url.contains("virtual")) {

                        items[count] = url;
                        urls[count++] = url;
                        continue;
                    }

                    int start = url.indexOf("virtual") + "virtual".length() + 1;
                    int end = url.indexOf("/", start);
                    System.out.println("start:" + start + ",end:" + end);

                    final String name = url.substring(start, end);


                    items[count] = name;
                    urls[count++] = url;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Past Positions");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        contents = (String) urls[pos];
                        buildData(contents);
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });

        tv.setText("Waiting to scan for a QR code...");
        if (contents.equals("")) {
            scan();
        }


//
//        buildData("http://192.168.1.108:8081/uberdust/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:room:0.I.3/rdf/rdf+xml/");
//        buildData("http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:room:0.I.2/rdf/rdf+xml/");
    }

    private void scan() {
        IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
        integrator.initiateScan();
    }


    @Override
    protected void onResume() {
        super.onResume();
//
//        int read = 0;
//        Room room1 = new Room();
//        Capability capa1 = new Capability();
//        Capability capa2 = new Capability();
//        Capability capa3 = new Capability();
//        Capability capa4 = new Capability();
//        Capability capa5 = new Capability();
//        Capability capa6 = new Capability();
//        Capability capa7 = new Capability();
//        Capability capa8 = new Capability();
//        Capability capa9 = new Capability();
//        Capability capa10 = new Capability();
//        capa1.setName("ceilinglight1");
//        capa1.setType(1);
//        capa2.setName("desklamp1");
//        capa2.setType(1);
//        capa3.setName("desklamp2");
//        capa3.setType(1);
//        capa4.setName("light1");
//        capa4.setValue("0");
//        capa4.setType(0);
//        capa5.setName("lamp2");
//        capa5.setValue("0");
//        capa5.setType(0);
//        capa6.setName("lamp1");
//        capa6.setValue("0");
//        capa6.setType(0);
//        capa7.setName("aircodition:temprature");
//        capa7.setType(1);
//        capa8.setName("aircodition:active");
//        capa8.setType(1);
//        capa9.setName("aircodition:turbo");
//        capa9.setType(1);
//        capa10.setName("aircodition:led");
//        capa10.setType(1);
//        room1.appendCommand(capa1);
//        room1.appendCommand(capa2);
//        room1.appendCommand(capa3);
//        room1.appendCapability(capa4);
//        room1.appendCapability(capa5);
//        room1.appendCapability(capa6);
//        room1.appendCommand(capa7);
//        room1.appendCommand(capa8);
//        room1.appendCommand(capa9);
//        room1.appendCommand(capa10);
//
//
//        if (read == 0) {
//            Intent i = new Intent(getApplicationContext(), RoomActivity.class);
//            i.putExtra("MyRoom", room1);
//            startActivity(i);
//        } else if (read == 1) {
//            Intent j = new Intent(getApplicationContext(), CapabilityActivity.class);
//            j.putExtra("MyCapa", capa7);
//            startActivity(j);
//        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            contents = result.getContents();
            if (contents != null) {
                TextView tv = (TextView) findViewById(R.id.urlscannedtextview);

                String vitual = "virtual";
                if (contents.contains(vitual)) {
                    int start = contents.indexOf(vitual) + vitual.length() + 1;
                    int end = contents.indexOf("/", start);
                    System.out.println("start:" + start + ",end:" + end);
                    final String type = contents.substring(start, end).split(":")[0];
                    final String name = contents.substring(start, end).split(":")[1];
                    System.out.println(contents.substring(start, end));


                    System.out.println(type);
                    System.out.println(name);

                    tv.setText("You are in  a " + type + " called " + name + ".");


                }


                Log.d("SCAN", contents);
            } else {
                Log.d("SCAN", "FAIL");
            }
        }
    }

    private void buildData(String contents) {
        TextView tv = (TextView) findViewById(R.id.urlscannedtextview);
//        ProgressBar pbar = (ProgressBar) findViewById(R.id.progbar);
//        pbar.setProgress(0);

        tv.setText(contents);

        String vitual = "virtual";
        if (contents.contains(vitual)) {
            int start = contents.indexOf(vitual) + vitual.length() + 1;
            int end = contents.indexOf("/", start);
            System.out.println("start:" + start + ",end:" + end);
            final String type = contents.substring(start, end).split(":")[0];
            final String name = contents.substring(start, end).split(":")[1];
            System.out.println(contents.substring(start, end));


            System.out.println(type);
            System.out.println(name);

            tv.setText("You are in  a " + type + " called " + name + ".");

            String urlsAll = settings.getString("positions", "");
            if (urlsAll.equals("")) {
                urlsAll = contents;
            } else {
                String[] allurls = urlsAll.split(",");
                boolean exists = false;
                for (String allurl : allurls) {
                    if (allurl.equals(contents)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    urlsAll = urlsAll + "," + contents;
                }
            }
            // We need an Editor object to make preference changes.
            // All objects are from android.context.Context

//            Toast.makeText(getApplicationContext(), "URLS:" + urlsAll, Toast.LENGTH_SHORT);

            SharedPreferences.Editor editor = settings.edit();

            String oldSets = settings.getString("positions", "");
//            Toast.makeText(getApplicationContext(), oldSets, Toast.LENGTH_SHORT);
            oldSets = oldSets + "," + contents;
//
            editor.putString("positions", urlsAll);

            editor.apply();
            editor.commit();


            String baseURL = contents.substring(0, contents.indexOf("rdf"));
            String capContents = contents.replace("rdf/rdf+xml/", "capabilities/json");
            try {
                Room room = new Room();

                String jsontext = getStringContent(capContents);
//                tv.append("\n contents:" + jsontext);
                JSONObject jsonObject = new JSONObject(jsontext);
                JSONArray capabilities = jsonObject.getJSONArray("capabilities");
                for (int i = 0; i < capabilities.length(); i++) {
//                    pbar.setProgress(100 / capabilities.length() * i);

                    String capname = (String) capabilities.get(i);
                    if (!capname.contains(":")) continue;
                    if (capname.contains("report")) continue;
                    if (capname.contains("lqi")) continue;
                    capname = capname.substring(capname.lastIndexOf(":") + 1);
//                    tv.append("\n " + capabilities.get(i));
                    Capability capObj = new Capability(capname, 1);
                    if (capname.contains("lz")) {
                        capObj.setUrl(baseURL);
                        room.appendCommand(capObj);
                    } else {
                        capObj.setUrl(baseURL);
                        room.appendCapability(capObj);
                    }


                }

                Intent inte = new Intent(getApplicationContext(), RoomActivity.class);
                inte.putExtra("MyRoom", room);
                startActivity(inte);

            } catch (Exception e) {
//                tv.append("\n Error:" + e.getMessage());
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
//                    tv.append(capContents);
//                    tv.append(stackTraceElement.toString());
                }
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

//            try {
//                URL url = new URL(capContents);
//                InputStream is = url.openStream();
//                byte[] buffer = new byte[is.available()];
//                while (is.read(buffer) != -1) ;
//                String jsontext = new String(buffer);
//                JSONArray entries = new JSONArray(jsontext);
//
//                tv.append("\n " + jsontext);
////                        String x = "JSON parsed.\nThere are [" + entries.length() + "]\n\n";
////                        int i;
////                        for (i=0;i<entries.length();i++) {
////                        }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (IOException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (JSONException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }

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
