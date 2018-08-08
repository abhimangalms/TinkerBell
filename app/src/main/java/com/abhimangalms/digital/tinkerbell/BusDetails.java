package com.abhimangalms.digital.tinkerbell;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class BusDetails extends AppCompatActivity {
    TextView t1,t2;
    Button b1,b2,b3;
    String str = "9"; //default value for busid for testing
    String lat="",lot="";
    TextView l1;
    String bus="",busid="9";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);


        Bundle b=getIntent().getExtras(); //values from ViewBus.java
        //str=b.getString("id");


        t2=(TextView)findViewById(R.id.datefrom);
        t1=(TextView)findViewById(R.id.dateuntil);
        l1=(TextView)findViewById(R.id.busdet);
        b1=(Button)findViewById(R.id.button1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        new SendRequest1().execute();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(busid.equals("")&&lat.equals("")&&lot.equals(""))){
                Intent i=new Intent(BusDetails.this,MapsActivity.class);
                i.putExtra("bus",busid);
                i.putExtra("lat",lat);
                i.putExtra("lon",lot);

                Toast.makeText(BusDetails.this, "busid = "+ busid+ "lat = "+ lat + "lon = "+ lot, Toast.LENGTH_SHORT).show();
                startActivity(i);}


                else{
                    Toast.makeText(BusDetails.this,"No datas found", Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        });
        new SendRequest2().execute();

    }


    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    public class SendRequest1 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                //  URL url = new URL("http://10.0.2.2:8080/test/test2.jsp");
                Ip i = new Ip();
                String ip = i.getIp();
                URL url = new URL(ip+"/viewbus.php");
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("rid",str);

                Log.e("params",postDataParams.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                // connection.setRequestProperty("Content-Length", "" +
                //  Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
//Send request
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                // wr.write(getPostDataString(postDataParams));
                writer.flush ();
                writer.close ();
//Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');


                }
                return  response.toString();
            }
            catch(Exception e){
                e.printStackTrace();
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(BusDetails.this,s, Toast.LENGTH_LONG).show();
            String str1;
            s = s.trim();
            //  lst = (ListView) findViewById(R.id);

          String str[]=s.split("\\*");
            t1.setText(str[4]);
            if(str.length>5) {
                if (!(str[5].equals("") && str[5].equals(null)))
                    t2.setText(str[5]);
            }
            else
                t2.setText("Start");

            busid = str[1];
            lat = str[2];
            lot = str[3];


        }


    }
    public class SendRequest3 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                SharedPreferences MyPref;
                SharedPreferences dta = getSharedPreferences("MyPref", MODE_PRIVATE);
                String id1=dta.getString("idd2",null);
                //  URL url = new URL("http://10.0.2.2:8080/test/test2.jsp");
                Ip i=new Ip();
                String ip = i.getIp();
                URL url = new URL(ip+"/"+bus);
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("rid",id1);
                postDataParams.put("id",str);

                Log.e("params",postDataParams.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                // connection.setRequestProperty("Content-Length", "" +
                //  Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches (false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
//Send request
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                // wr.write(getPostDataString(postDataParams));
                writer.flush ();
                writer.close ();
//Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');


                }
                return  response.toString();
            }
            catch(Exception e){
                e.printStackTrace();
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(BusDetails.this,s, Toast.LENGTH_LONG).show();
            Toast.makeText(BusDetails.this,"Successfully added", Toast.LENGTH_LONG).show();


        }


    }
    public class SendRequest2 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                SharedPreferences MyPref;
                SharedPreferences dta =getSharedPreferences("MyPref", MODE_PRIVATE);
                String id1=dta.getString("idd2",null);
                //  URL url = new URL("http://10.0.2.2:8080/test/test2.jsp");
                Ip i=new Ip();
                String ip=i.getIp();
                URL url = new URL(ip+"/viewstop.php");
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("rid",str);

                Log.e("params",postDataParams.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                // connection.setRequestProperty("Content-Length", "" +
                //  Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches (false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
//Send request
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                // wr.write(getPostDataString(postDataParams));
                writer.flush ();
                writer.close ();
//Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');


                }
                return  response.toString();
            }
            catch(Exception e){
                e.printStackTrace();
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(BusDetails.this,s, Toast.LENGTH_LONG).show();
            String str1;
            s=s.trim();
            String ar[]=s.split("\\*");
            String str="";

            int i=0;
            while(i<ar.length)
            {
                str=str+"-"+ar[i];
                i++;
            }
            l1.setText(str);


        }


    }
}
