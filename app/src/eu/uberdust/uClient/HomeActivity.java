package eu.uberdust.uClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import eu.uberdust.model.Capability;
import eu.uberdust.model.Room;
import eu.uberdust.model.Uberdust;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class HomeActivity extends Activity {

    private String contents;
    private static final String VIRTUAL = "virtual";
    private SharedPreferences settings;
    private NfcAdapter mAdapter;
    private static boolean writeMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DEBUG", "onCreateHomeActivity");

        // grab our NFC Adapter
        mAdapter = NfcAdapter.getDefaultAdapter(this);


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
        setContentView(R.layout.activity_home);

        TextView tv = (TextView) findViewById(R.id.urlscannedtextview);

        ImageView logo2 = (ImageView) findViewById(R.id.logoSPITFIRE);
        logo2.setImageResource(R.drawable.spitfire);


        Button startbutton = (Button) findViewById(R.id.loadbutton);
        Button rescanbutton = (Button) findViewById(R.id.rescanbutton);
        Button loadHistoryButton = (Button) findViewById(R.id.loadHistory);
        Button writeTagButton = (Button) findViewById(R.id.writeTag);
        rescanbutton.setEnabled(true);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!contents.equals("") || (!contents.contains("Waiting"))) {
                        buildData(contents);
                    }
                } catch (Exception e) {

                }
            }
        });

        rescanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        loadHistoryButton.setEnabled(true);
        writeTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableWriteMode();
            }
        });
        loadHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String urlsAll = settings.getString("positions", "");
//                Toast.makeText(getApplicationContext(), "URLS:" + urlsAll, Toast.LENGTH_SHORT);


//                Toast.makeText(view.getContext(), "Check History", Toast.LENGTH_SHORT).show();
                AlertDialog dialog;

                String urlsAll = settings.getString("positions", "");
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

        if (writeMode == false) {
            Intent intent = getIntent();
            if (intent.getType() != null && intent.getType().equals("application/eu.uberdust")) {
                Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage msg = (NdefMessage) rawMsgs[0];
                NdefRecord cardRecord = msg.getRecords()[0];
                final String result = new String(cardRecord.getPayload());

                contents = result;
                if (contents != null) {
                    tv = (TextView) findViewById(R.id.urlscannedtextview);

                    if (contents.contains(VIRTUAL)) {
                        tv.setText("You are in  a " + extractType() + " called " + extractID() + ".");
                    } else {
                        tv.setText("You change an object with ID " + extractID());
                    }
                } else {
                    Log.d("SCAN", "FAIL");
                }

//            buildData(new String(cardRecord.getPayload()));
            } else {
                tv.setText("Waiting to scan for a QR code...");
                if (contents.equals("")) {
                    scan();
                }
            }
        }

//        buildData("http://192.168.1.5:8080/uberdust/rest/testbed/2/node/urn:pspace:0x2eb/rdf/rdf+xml/");
//        buildData("http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:room:0.I.9/rdf/rdf+xml/");
    }

    /**
     * Force this Activity to get NFC events first
     */
    private void enableWriteMode() {
        writeMode = true;
        displayMessage("Tap tag to write...");
        // set up a PendingIntent to open the app when a tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[]{tagDetected};
        mAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);

    }


    /**
     * Called when our blank tag is scanned executing the PendingIntent
     */
    @Override
    public void onNewIntent(Intent intent) {
        // write to newly scanned tag
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        writeTag(tag);
    }

    private void scan() {
        IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        contents = "";
        if (result != null) {
            contents = result.getContents();
            if (contents != null) {
                TextView tv = (TextView) findViewById(R.id.urlscannedtextview);


                if (contents.contains(VIRTUAL)) {
                    tv.setText("You are in  a " + extractType() + " called " + extractID() + ".");
                } else {
                    tv.setText("You change an object with ID " + extractID());
                }
            } else {
                Log.d("SCAN", "FAIL");
            }
        }
    }

    private String extractID() {
        if (contents.contains(VIRTUAL)) {
            int start = contents.indexOf(VIRTUAL) + VIRTUAL.length() + 1;
            int end = contents.indexOf("/", start);
            return contents.substring(start, end).split(":")[1];
        } else {
            int start = contents.contains(":") ? contents.lastIndexOf(":") + 1 : 0;
            int end = contents.indexOf("/", start);
            return contents.substring(start, end);
        }
    }

    private String extractType() {
        if (contents.contains(VIRTUAL)) {
            int start = contents.indexOf(VIRTUAL) + VIRTUAL.length() + 1;
            int end = contents.indexOf("/", start);
            final String type = contents.substring(start, end).split(":")[0];
            return type;
        }
        return "";
    }

    private void buildData(String contents) {
        TextView tv = (TextView) findViewById(R.id.urlscannedtextview);

//        ProgressBar pbar = (ProgressBar) findViewById(R.id.progbar);
//        pbar.setProgress(0);
//        tv.setText(contents);

        if (contents.contains(VIRTUAL) || true) {
//            int start = contents.indexOf(VIRTUAL) + VIRTUAL.length() + 1;
//            int end = contents.indexOf("/", start);
//            System.out.println("start:" + start + ",end:" + end);
//            final String type = contents.substring(start, end).split(":")[0];
//            final String name = contents.substring(start, end).split(":")[1];
//            System.out.println(contents.substring(start, end));
//            tv.setText("You are in  a " + type + " called " + name + ".");
//            String urlsAll = settings.getString("positions", "");
//            if (urlsAll.equals("")) {
//                urlsAll = contents;
//            } else {
//                String[] allurls = urlsAll.split(",");
//                boolean exists = false;
//                for (String allurl : allurls) {
//                    if (allurl.equals(contents)) {
//                        exists = true;
//                        break;
//                    }
//                }
//                if (!exists) {
//                    urlsAll = urlsAll + "," + contents;
//                }
//            }
// We need an Editor object to make preference changes.
// All objects are from android.context.Context
//
//            Toast.makeText(getApplicationContext(), "URLS:" + urlsAll, Toast.LENGTH_SHORT);
//
            try {
                SharedPreferences.Editor editor = settings.edit();
//
                String oldSets = settings.getString("positions", "");
//            Toast.makeText(getApplicationContext(), oldSets, Toast.LENGTH_SHORT);

                HashSet<String> strs = new HashSet<String>();
                for (CharSequence item : oldSets.split(",")) {
                    strs.add(item.toString());
                }

                strs.add(contents);
//            if (!oldSets.contains(contents)) {
//                oldSets = oldSets + "," + contents;
//            }
                StringBuilder newSets = new StringBuilder();
                for (String str : strs) {
                    newSets.append(",").append(str);
                }
                editor.putString("positions", newSets.toString().substring(1));
//                editor.apply();
                editor.commit();
            } catch (Exception e) {
            }
            String mcontents = contents.replaceAll("http://", "");
            int semicolon = mcontents.indexOf(":");
            int slash = mcontents.indexOf("/");
            if (semicolon < slash) {
                Uberdust.getInstance().setUberdustURL(mcontents.substring(0, mcontents.indexOf(":")));
            } else {
                Uberdust.getInstance().setUberdustURL(mcontents.substring(0, mcontents.indexOf("/")));
            }

            String baseURL = contents.substring(0, contents.indexOf("rdf"));
            String capContents = contents.replace("rdf/rdf+xml/", "capabilities/json");
            try {
                Room room = new Room();
                String jsontext = getStringContent(capContents);
//                tv.append("\n contents:" + jsontext);
                JSONObject jsonObject = new JSONObject(jsontext);
                JSONArray capabilities = jsonObject.getJSONArray("capabilities");
                boolean hasControl = false;
                List<String> capList = new ArrayList<String>();
                for (int i = 0; i < capabilities.length(); i++) {
//                    pbar.setProgress(100 / capabilities.length() * i);
                    String capname = (String) capabilities.get(i);
                    if (!capname.contains(":")) continue;
                    if (capname.contains("report")) continue;
                    if (capname.contains("lqi")) continue;
                    if (capList.contains(capname)) continue;
                    else capList.add(capname);
                    capname = capname.substring(capname.lastIndexOf(":") + 1);
//                    tv.append("\n " + capabilities.get(i));
                    Capability capObj = new Capability(capname, 1);
                    if (capname.contains("lz")) {
                        hasControl = true;
                        capObj.setUrl(baseURL);
                        room.appendCommand(capObj);
                    } else {
                        capObj.setUrl(baseURL);
                        room.appendCapability(capObj);
                    }
                }
                if (hasControl) {
                    Capability capObj = new Capability("bypass", 1);
                    capObj.setUrl(baseURL);
                    room.appendCommand(capObj);
                }
                Intent inte = new Intent(getApplicationContext(), RoomActivity.class);
                inte.putExtra("MyRoom", room);
                startActivity(inte);

            } catch (Exception e) {
            }
        }
    }


    public static String getStringContent(String uri) throws Exception {
        try {
            URL url = new URL(uri);
            URLConnection urlconnection = url.openConnection();
            urlconnection.getContentLength();
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            in.close();
            return buffer.toString();

        } finally {
            // any cleanup code...
        }
    }


    /**
     * Format a tag and write our NDEF message
     *
     * @param tag
     */
    private boolean writeTag(Tag tag) {

        // record to launch Play Store if app is not installed
//        NdefRecord appRecord = NdefRecord.createMime("application/eu.uberdust", "application/eu.uberdust".getBytes());

        // record that contains our custom "retro console" game data, using custom MIME_TYPE
        byte[] payload = contents.getBytes();
        byte[] mimeBytes = "application/eu.uberdust".getBytes(Charset.defaultCharset());
        NdefRecord cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes,
                new byte[0], payload);
        NdefMessage message = new NdefMessage(new NdefRecord[]{cardRecord});

        try {
            // see if tag is already NDEF formatted
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    displayMessage("Read-only tag.");
                    return false;
                }

                // work out how much space we need for the data
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    displayMessage("Tag doesn't have enough free space.");
                    writeMode = false;
                    return false;
                }

                ndef.writeNdefMessage(message);
                displayMessage("Tag written successfully.");
                return true;
            } else {
                // attempt to format tag
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        displayMessage("Tag written successfully!\nClose this app and scan tag.");
                        writeMode = false;
                        return true;
                    } catch (IOException e) {
                        displayMessage("Unable to format tag to NDEF.");
                        writeMode = false;
                        return false;
                    }
                } else {
                    displayMessage("Tag doesn't appear to support NDEF format.");
                    writeMode = false;
                    return false;
                }
            }
        } catch (Exception e) {
            writeMode = false;
            displayMessage("Failed to write tag");
        }
        writeMode = false;
        return false;
    }

    private void displayMessage(String message) {
        TextView tv = (TextView) findViewById(R.id.urlscannedtextview);
        tv.setText(message);
    }

}
