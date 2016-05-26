package com.example.peter.pikusup;

        import android.app.Activity;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.ActionBarActivity;
        import android.support.v7.app.AlertDialog;
        import android.util.Log;
        import android.view.ContextMenu;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.HashMap;

/**
 * Created by Peter on 2016-04-28.
 */
public class MemberList extends ActionBarActivity {
    private ListView myList;
    public static final int REG_REQ_CODE = 55;
    public static final int REG_RESULT_CODE = 56;
    public static final int Home = 111;
    public static final int School = 112;
    private HashMap<String, String> kids;
    private TaxiSendMessage sendMessage;
    private String TAG =  MemberList.class.getName();
    private View clickedView;
    private int backButtonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        myList = (ListView) findViewById(R.id.mylist);
        kids = (HashMap)getIntent().getSerializableExtra("KIDS");
        getAllFromDB();
        registerForContextMenu(myList);
        ActionBar actionBar = getSupportActionBar();
        sendMessage = new TaxiSendMessage();
    }

    private void getAllFromDB() {
        MyAdapter adapter = new MyAdapter(kids);
        myList.setAdapter(adapter);
        myList.setClickable(true);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                clickedView = view;
                TextView textview =(TextView)view.findViewById(R.id.tellnumholder);
                String telnum = textview.getText().toString();

                TextView nametextview =(TextView)view.findViewById(R.id.nameholder);
                String name = nametextview.getText().toString();
                Log.d(TAG, "<<<<<<<<<<<<<, clicked tel = "+telnum);
                showDialog(telnum, name);
                /* write you handling code like...
                String st = "sdcard/";
                File f = new File(st+o.toString());
                // do whatever u want to do with 'f' File object
                */
            }
        });
    }

    public void showDialog(final String number, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String msg = "Good Day, "+ name+" Has been dropped off at";
        builder.setMessage("Is the child delivered to")
                .setCancelable(false)
                .setPositiveButton("School", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String [] message = {number,msg+" school. Regards Idol Transport"};
                        new HttpAsyncTask().execute(message);
                        TextView name =(TextView)clickedView.findViewById(R.id.nameholder);
                        name.setTextColor(Color.GRAY);
                        TextView textview =(TextView)clickedView.findViewById(R.id.tellnumholder);
                        textview.setTextColor(Color.GRAY);
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                "COLOR", getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(textview.getText().toString(),"SCHOOL");
                        editor.commit();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TextView name =(TextView)clickedView.findViewById(R.id.nameholder);
                        name.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView textview =(TextView)clickedView.findViewById(R.id.tellnumholder);
                        textview.setTextColor(getResources().getColor(R.color.colorPrimary));
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                "COLOR", getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(textview.getText().toString(),"HOME");
                        editor.commit();
                        dialog.cancel();
                        String [] message = {number,msg+" Home. Regards Idol Transport"};
                        new HttpAsyncTask().execute(message);
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();

        alert.show();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                "COLOR", getApplicationContext().MODE_PRIVATE);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
        if(sharedPref!=null){
            String isHome = sharedPref.getString(number,"HOME");
            if(isHome.equalsIgnoreCase("HOME")) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
            }else if (isHome.equalsIgnoreCase("SCHOOL")){
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(TAG, "<<<<<<<<<<<<<, clicked ID = "+v.getId());
        menu.setHeaderTitle("Is the child delivered to");
        menu.add(Menu.NONE, School, 0, "Delivered to School");

        menu.add(Menu.NONE, Home, 0, "Delivered to Home");
        super.onCreateContextMenu(menu, v, menuInfo);
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... msg) {


            return  ""+sendMessage.sendSms(msg[0], msg[1]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String results) {

            if(results.equalsIgnoreCase("true")) {
                Toast.makeText(getBaseContext(), "Message sent to Guardian", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getBaseContext(), "Message not sent :(", Toast.LENGTH_LONG).show();
            }
//            if(results != null || !results.isEmpty()){
//                //
//            }else{
//                //showErr();
//            }
        }
    }

    public static String POST(String urlparam){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REG_REQ_CODE) {
            if (resultCode == REG_RESULT_CODE) {
                getAllFromDB();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}
