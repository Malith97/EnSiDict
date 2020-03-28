package com.synnlabz.ensidict;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* D/CS/18/0002 IAMP Ileperuma */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HttpExample";
    private AppCompatEditText url;
    private AppCompatTextView textView;
    private AppCompatButton Translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = (AppCompatEditText) findViewById(R.id.myUrl);
        textView = (AppCompatTextView) findViewById(R.id.myText);
        Translate = (AppCompatButton) findViewById(R.id.translate);

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        /* D/CS/18/0002 IAMP Ileperuma */
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
        } else {
            new AlertDialog.Builder(this)
                .setTitle("Connection Failure")
                .setMessage("Please Connect to the Internet")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
        /* D/CS/18/0002 IAMP Ileperuma */
        Translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringUrl = "https://www.maduraonline.com/?find="+url.getText().toString();

                ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                /* D/CS/18/0002 IAMP Ileperuma */

                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadWebpageTask().execute(stringUrl);
                } else {
                    textView.setText("No network connection available.");
                }
            }
        });
    }

    /* D/CS/18/0002 IAMP Ileperuma */
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            String output = "";
            textView.setText(result);
            String expr = "<td class=\"td\">(.+?)</td>";

            Pattern pattern = Pattern.compile(expr);
            Matcher m = pattern.matcher(result);
            while (m.find()) {
                String stateURL = m.group(1);
                output = output + "\n" + stateURL;
            }
            textView.setText(output);
        }

        /* D/CS/18/0002 IAMP Ileperuma */
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            int len = 4000;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();

                String contentAsString = readIt(is, len);
                return contentAsString;

            } finally {
                if (is != null) {
                    is.close();
                }
            }}
        /* D/CS/18/0002 IAMP Ileperuma */
        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }




    }
}
