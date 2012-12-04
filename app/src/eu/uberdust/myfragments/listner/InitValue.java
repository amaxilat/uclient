//package eu.uberdust.myfragments.listner;
//
//import android.content.Context;
//import android.widget.TextView;
//import android.widget.Toast;
//import eu.uberdust.myfragments.MyCommandsAdapter;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;
//
///**
// * Created with IntelliJ IDEA.
// * User: amaxilatis
// * Date: 11/25/12
// * Time: 2:14 PM
// * To change this template use File | Settings | File Templates.
// */
//public class InitValue implements Runnable {
//
//    private Context context;
//    private MyCommandsAdapter.ViewHolder finalHolder;
//    private String url;
//    private String name;
//    private TextView textView2;
//
//    public InitValue(Context context, String url, String name, TextView textView2) {
//        this.context = context;
//        this.url = url;
//        this.name = name;
//        this.textView2 = textView2;
//    }
//
//    @Override
//    public void run() {
//
//        Toast toast = Toast.makeText(context, "Pressed Row", Toast.LENGTH_SHORT);
//        String strval = null;
//        try {
////            textView2.setText(getFromJSON(url, name));
//            textView2.setText(getFromTAB(url, name));
//        } catch (Exception e) {
//            toast = Toast.makeText(context, "Error:" + e.getMessage(), Toast.LENGTH_SHORT);
//            toast.show();
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//    }
//
////    public String getFromTAB(final String url, final String name) {
////        try {
////            return
////        } catch (Exception e) {
////            return "error";
////        }
////    }
//
//
//}
