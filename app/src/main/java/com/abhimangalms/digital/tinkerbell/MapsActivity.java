package com.abhimangalms.digital.tinkerbell;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;


import java.io.IOException;
import java.util.List;
import java.util.Locale;


import android.os.AsyncTask;

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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        android.location.LocationListener,com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    String busid="";
    String str;
    double lat,lot;
    double latBus;
    double lotBus;

    private Marker currentLocationmMarker;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private GoogleApiClient client;
    public static final int REQUEST_LOCATION_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        Bundle b=getIntent().getExtras();

        //Getting busId, Latitude, Longitude from BusDetails.java

        str = b.getString("bus");  //this value can be changed in busid variable ofBusDetails.java
                                        //value of bus is set to 9 in BusDetails.java
        latBus = Double.parseDouble(b.getString("lat"));
        lotBus = Double.parseDouble(b.getString("lon"));

        //above commended code are required

        Toast.makeText(this, "Reached MapActivity with "+ str + latBus + lotBus, Toast.LENGTH_SHORT).show();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MapsActivity();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, (android.location.LocationListener) locationListener);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;

        // Add a marker in Sydney, Australia, and move the camera.

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);


            //making a marker in the map
            //LatLng latLng = new LatLng(lat ,lot );



            //for testing only
            LatLng latLng = new LatLng( latBus,lotBus );
            mMap.getUiSettings().setZoomControlsEnabled(true);

            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.
                    fromResource(R.drawable.schoolbusmarker)).title("BUS"));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Add a marker in location and move the camera
                            new SendRequest1().execute();


                            //  tvTime.setText(""+((System.currentTimeMillis()-startTime)/1000));
                        }
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//
                }
            }
        }).start();
        //
        // Add a marker in Sydney and move the camera

    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }



    @Override
    public void onLocationChanged(Location location) {


        //pb.setVisibility(View.INVISIBLE);
        Toast.makeText(
                getBaseContext(),
                "Location changed: Lat: " + location.getLatitude() + " Lng: "
                        + location.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + location.getLongitude();
        //Log.v(TAG, longitude);
        String latitude = "Latitude: " + location.getLatitude();
        //Log.v(TAG, latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

        lat = location.getLatitude();
        lot = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+lat);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions.title("You are here"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(17.0f));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Animating the marker

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {

                final LatLng startPosition = currentLocationmMarker.getPosition();
                final LatLng finalPosition = new LatLng(12.7801569, 77.4148528);
                final Handler handler = new Handler();
                final long start = SystemClock.uptimeMillis();
                final Interpolator interpolator = new AccelerateDecelerateInterpolator();
                final float durationInMs = 3000;
                final boolean hideMarker = false;

                handler.post(new Runnable() {
                    long elapsed;
                    float t;
                    float v;

                    @Override
                    public void run() {
                        // Calculate progress using interpolator
                        elapsed = SystemClock.uptimeMillis() - start;
                        t = elapsed / durationInMs;

                        LatLng currentPosition = new LatLng(
                                startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                                startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                        currentLocationmMarker.setPosition(currentPosition);

                        // Repeat till progress is complete.
                        if (t < 1) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16);
                        } else {
                            if (hideMarker) {
                                currentLocationmMarker.setVisible(false);
                            } else {
                                currentLocationmMarker.setVisible(true);
                            }
                        }
                    }
                });

                return true;

            }

        });

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest,this);
        }

    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public class SendRequest1 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                //  URL url = new URL("http://10.0.2.2:8080/test/test2.jsp");
                Ip i=new Ip();
                String ip=i.getIp();
                URL url = new URL(ip+"/viewloc.php");
                JSONObject postDataParams = new JSONObject();

                //passing busId to the database
                postDataParams.put("bid",str);



                //below code is for testing only
                //postDataParams.put("bid",9);


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

                //  Toast.makeText(gps, "OS"+ os, Toast.LENGTH_SHORT).show();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                // wr.write(getPostDataString(postDataParams));
                writer.flush ();
                writer.close ();
//Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String dbrfid;
                StringBuffer response = new StringBuffer();
                while((dbrfid = rd.readLine()) != null) {
                    response.append(dbrfid);
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

            try {

                mMap.clear();
                String s1[] = s.split("\\*");
                String a = s1[0];
                String b = s1[1];
                if (s1.length > 1) {
                    Toast.makeText(MapsActivity.this, s, Toast.LENGTH_LONG).show();
                    LatLng latLng = new LatLng(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]));
                    Toast.makeText(MapsActivity.this, "latlng" + s, Toast.LENGTH_SHORT).show();


                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.
                            fromResource(R.drawable.schoolbusmarker)).title("BUS"));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                } else {
                    Toast.makeText(MapsActivity.this, "Location not found", Toast.LENGTH_LONG).show();
                }


            }
            catch (Exception e){
                e.printStackTrace();
                finish();
            }
        }


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
}
