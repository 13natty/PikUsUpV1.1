package com.example.peter.pikusup;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Peter on 2016-04-28.
 */
public class MainActivity extends ActionBarActivity {
    Button b1,b2;
    EditText ed1,ed2;

    TextView tx1;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1=(Button)findViewById(R.id.button);
        ed1=(EditText)findViewById(R.id.editText);

        b2=(Button)findViewById(R.id.button2);
        tx1=(TextView)findViewById(R.id.textView3);
        tx1.setVisibility(View.GONE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ed1.getText().toString();
                if(!name.isEmpty())
                {
                    // call AsynTask to perform network operation on separate thread
                    new HttpAsyncTask().execute("http://ec2-52-23-236-78.compute-1.amazonaws.com/dropOffLogin.php?username="+name);
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Login",Toast.LENGTH_LONG).show();
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String GET(String urlparam){
        HttpURLConnection urlConnection = null;

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlparam);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }


        return result.toString();

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String results) {
           // Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            if(results != null || !results.isEmpty()){
                showList(results);
            }else{
                showErr();
            }
        }
    }

    private void showErr() {
        Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_LONG).show();

        tx1.setVisibility(View.VISIBLE);

        counter--;
        tx1.setText(Integer.toString(counter));

        if (counter == 0) {
            b1.setEnabled(false);
        }
    }

    private void showList(String results) {
       // Log.d(this.getLocalClassName(),"results "+results);
        results = results.substring(results.indexOf('['),results.indexOf("]")+1);
        Log.d(this.getLocalClassName(),"results final >>>>>>>>>>> "+results);

        JSONArray childrenList = null;
        HashMap<String, String> kids = new HashMap<>();
        try {
            childrenList = new JSONArray(results);
            Log.e(childrenList.toString(),"string");
            JSONObject mainobj = null;
            for( int i=0;i<childrenList.length();i++)
            {
                mainobj = childrenList.getJSONObject(i);
                if(mainobj.has("CHILD_NAME") && mainobj.has("TEL"))
                {
                   // kids.put("CHILD_NAME",mainobj.getString("CHILD_NAME"));
                  //  kids.put("TEL", mainobj.getString("TEL"));
                    kids.put(mainobj.getString("CHILD_NAME"), mainobj.getString("TEL"));
                    System.out.println("building map......................."+mainobj.getString("CHILD_NAME")+"::::"+ mainobj.getString("TEL"));
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       // Toast.makeText(getApplicationContext(), "results "+results,Toast.LENGTH_LONG).show();
       // Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent("android.intent.action.MemberList");
        intent.putExtra("KIDS", kids);
        startActivity(intent);
    }
}