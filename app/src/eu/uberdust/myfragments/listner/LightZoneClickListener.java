package eu.uberdust.myfragments.listner;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import eu.uberdust.model.Uberdust;
import eu.uberdust.myfragments.MyCommandsAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 11/25/12
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class LightZoneClickListener implements View.OnClickListener {

    private Context context;
    private MyCommandsAdapter.ViewHolder finalHolder;
    private String state;
    private String path;
    private String uri;

    public LightZoneClickListener(Context context, String path, String uri, MyCommandsAdapter.ViewHolder finalHolder, String state) {
        this.context = context;
        this.path = path;
        this.uri = uri;
        this.finalHolder = finalHolder;
        this.state = state;
    }

    @Override
    public void onClick(View view) {
        try {
            sendRequest(path, uri, state);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        sendRequest(path, uri, state);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }, 300);

            Toast toast = Toast.makeText(context, "Turning " + ("1".equals(state) ? "On" : "Off"), Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Log.d("DEBUG", "rest");
        }
        if (state.equals("1")) {
            finalHolder.buttonON.setEnabled(false);
            finalHolder.buttonOFF.setEnabled(true);
        } else {
            finalHolder.buttonON.setEnabled(true);
            finalHolder.buttonOFF.setEnabled(false);
        }

    }

    private void sendRequest(String path, String uri, String payload) throws IOException, UnknownHostException {
        DatagramSocket clientSocket = new DatagramSocket();

        Log.d("DEBUG", Uberdust.getInstance().getUberdustURL());
        InetAddress IPAddress = InetAddress.getByName(Uberdust.getInstance().getUberdustURL());
        byte[] sendData;
        Request request = new Request(CodeRegistry.METHOD_POST, false);
        request.setURI(path);
        request.setMID((new Random()).nextInt() % 1024);
        Option urihost = new Option(OptionNumberRegistry.URI_HOST);
        urihost.setStringValue(uri);
        request.addOption(urihost);
        request.setPayload(payload);
        sendData = request.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5683);
        clientSocket.send(sendPacket);
        clientSocket.close();
    }

//    public static void main(String[] args) {
//        Request request = new Request(CodeRegistry.METHOD_POST, false);
//        request.setURI("lz1");
//        request.setMID(50);
//        Option urihost = new Option(OptionNumberRegistry.URI_HOST);
//        urihost.setStringValue("4ec");
//        request.addOption(urihost);
//        request.setPayload("1");
//        byte[] sendData = request.toByteArray();
//        System.out.println(sendData.length);
//        for (byte b : sendData) {
//            System.out.print(b + ",");
//        }
//        System.out.println("");
//        request.prettyPrint();
//
//    }


}
