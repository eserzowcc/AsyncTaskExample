package edu.wccnet.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(5);

        final TextView myResult=findViewById(R.id.editTextResult);

        Button retrievePasswordButton = (Button) findViewById(R.id.buttonRetrievePassword);
        retrievePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast myToast = Toast.makeText( MainActivity.this,"Retrieving your password...", Toast.LENGTH_SHORT );
                myToast.show();
                progressBar.setVisibility(View.VISIBLE);

                RetrievePasswordTask myTask = new RetrievePasswordTask(myResult);
                myTask.execute("http://echo.jsontest.com/user/eserzo/password/notmypassword");
            }
        });

        Button clearPasswordButton = (Button) findViewById(R.id.buttonClearPassword);
        clearPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myResult.setText("");
            }
        });

    }

    // Simple example first..
    // On break now try to morph this to retrieve the weather!
    private class RetrievePasswordTask extends AsyncTask<String, Integer, String> {
        private TextView textView;

        public RetrievePasswordTask(TextView textView) {
            this.textView=textView;
        }

        @Override
        protected String doInBackground(String... urls) {
            String password = "UNKNOWN";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                password = topLevel.getString("password");

                urlConnection.disconnect();

                // Putting a fake sleep in here
                for( int counter=0; counter<5;counter++ ) {
                    // Sleep for one second
                    Thread.sleep(1000);
                    publishProgress(counter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return password;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            textView.setText(result);
        }

    }
}