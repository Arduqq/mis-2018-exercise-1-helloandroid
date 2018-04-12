/*
Assignment 1
Artur Solomonik 115715
 */

package com.example.mis.helloandroid;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.TimeoutException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void error(final String e) {
        // Toast can access View only on the UI thread
        // https://stackoverflow.com/questions/3134683/android-toast-in-a-thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void connectToURL(View view) throws IOException {
        Thread thread = new Thread(new Runnable() {
            // Networking won't work on the main thread
            // Async might be better than the thread approach
            // https://stackoverflow.com/questions/17365646/android-os-network-on-main-thread-exception
            @Override
            public void run() {
                // find all of the needed components by ID
                EditText editText = findViewById(R.id.textInput);
                final TextView textView = findViewById(R.id.textView);
                String searchURL = editText.getText().toString();
                textView.setMovementMethod(new ScrollingMovementMethod());
                String html = "";
                try {
                    URL url = new URL(searchURL);
                    // establish connection and initialize input stream
                    // https://stackoverflow.com/questions/2075836/read-contents-of-a-url-in-android
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null) {
                        str.append(line + "\n");
                    }
                    in.close();
                    html = str.toString();
                    final String output = html;
                    // Again, only the UI thread can modify the view
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(output);
                        }
                    });
                    // Exception handling
                } catch(UnknownHostException e) {
                    error("Unknown host: the URL might not be correct");
                }
                catch (MalformedURLException e) {
                    error("Malformed URL: URL is invalid");
                }
                catch (IOException e) {
                    error("Network I/O error: " + e.toString());
                }
            }
        });
        thread.start();


    }
}
