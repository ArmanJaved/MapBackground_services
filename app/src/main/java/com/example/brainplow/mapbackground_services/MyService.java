package com.example.brainplow.mapbackground_services;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private Context context;

    public static final String MY_NEAR_Rest = "settings";


    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private double PROXIMITY_RADIUS = 45;

    Double latitude ;
    Double longitude;


    public static final String Latlng = "latlng";

    public static final String FIRSTTIME = "TimeFirst";


    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };




    public static final int notify = 6000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handlingr

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            SharedPreferences.Editor editor = context.getSharedPreferences(Latlng, MODE_PRIVATE).edit();
            String lalng= latitude + "-" + longitude;
            editor.putString("Latlng", lalng);
            editor.apply();


            SharedPreferences prefs1 = getSharedPreferences(Latlng, MODE_PRIVATE);
            String lalng_name1 = prefs1.getString("Latlng", "No name defined");


            String[] latlngArray = lalng_name1.split("-");

            String latnear1= latlngArray[0];
            String lngnear1 = latlngArray[1];

            Location locationA = new Location("point A");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);

            Location locationB = new Location("point B");
            locationB.setLatitude(Double.parseDouble(latnear1));
            locationB.setLongitude(Double.parseDouble(lngnear1));

            float distance = locationA.distanceTo(locationB);
            int distchange = (int) distance;


            SharedPreferences prefs2 = getSharedPreferences(FIRSTTIME, MODE_PRIVATE);
            String Check = prefs2.getString("CHECKFIRST", "No name defined");

            if (Check.equals("fIRST"))
            {

                        nearbyrest();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // yourMethod();

                        if (mTimer != null) // Cancel if already existed
                            mTimer.cancel();
                        else
                            mTimer = new Timer();   //recreate new
                        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task



                    }
                }, 7000);

                SharedPreferences.Editor editor1 = context.getSharedPreferences(FIRSTTIME, MODE_PRIVATE).edit();
                editor1.putString("CHECKFIRST", "Second");
                editor1.apply();



            }
            else {

                if (distchange>20)
                {
                    nearbyrest();


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // yourMethod();

                            if (mTimer != null) // Cancel if already existed
                                mTimer.cancel();
                            else
                                mTimer = new Timer();   //recreate new
                            mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task


                        }
                    }, 7000);
                }

            }

        }


        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {


        context = getApplicationContext();

        SharedPreferences.Editor editor = context.getSharedPreferences(FIRSTTIME, MODE_PRIVATE).edit();
        editor.putString("CHECKFIRST", "fIRST");
        editor.apply();

        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }




    }
    @Override
    public void onDestroy() {

        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
        Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show();
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast
                    Toast.makeText(MyService.this, "Service is running", Toast.LENGTH_SHORT).show();

                    AlertDisplay();



                }
            });
        }
    }


    private void AlertDisplay()
    {



        SharedPreferences prefs = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = prefs.getString("key", null);
        String[] text = gson.fromJson(jsonText, String[].class);

        if (text.length>1) {


            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            builderSingle.setIcon(R.drawable.alert);
            builderSingle.setTitle("Have you been to this place today ? ");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice, text);

            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setPositiveButton("Don't show", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

//                            SharedPreferences.Editor editor = getSharedPreferences(DontWantTAG, MODE_PRIVATE).edit();
//                            editor.putString("Alertdisplay", "DONT");
//                            editor.apply();
//
//                            simpleSwitch.setChecked(false);

                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strName = arrayAdapter.getItem(which);
                    Toast.makeText(context, strName, Toast.LENGTH_LONG).show();

                    avc(strName);
//                            avc(strName);
                }
            });


            AlertDialog alert = builderSingle.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert.show();


            ArrayList<String> list = new ArrayList<String>();
            SharedPreferences.Editor editor = context.getSharedPreferences(MY_NEAR_Rest, MODE_PRIVATE).edit();
            Gson gson1 = new Gson();
            List<String> textList = new ArrayList<String>();
            textList.addAll(list);
            String jsonText1 = gson1.toJson(textList);
            editor.putString("key", jsonText1);
            editor.commit();
        }
        else
        {

        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        return (googlePlacesUrl.toString());

    }
    public void nearbyrest()
    {
        String search = "restaurant";
        //String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=31.5142043,74.344806&radius=45.0&type=restaurant&sensor=true&key=AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0";
        String url = getUrl(latitude, longitude, search);
        Object[] DataTransfer = new Object[2];
        DataTransfer[1] = url;
        GetNearbyBanksData getNearbyBanksData = new GetNearbyBanksData(context);
        getNearbyBanksData.execute(DataTransfer);
    }


    public void avc(final String res_name) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = inflater.inflate(R.layout.amount_dialogue, null);
        final EditText amount_txt = (EditText) alertLayout.findViewById(R.id.et_username);


        AlertDialog.Builder  alert = new AlertDialog.Builder (this);
        alert.setTitle("How much did you spend in "+ res_name+ " ? ");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = amount_txt.getText().toString();
                // String pass = etPassword.getText().toString();

                Toast.makeText(getBaseContext(), "Amount: " + amount  , Toast.LENGTH_SHORT).show();
                AddData obj = new AddData(context);
                obj.addartist (res_name, amount);

            }
        });

//        android.support.v7.app.AlertDialog dialog = alert.create();
//        dialog.show();

        AlertDialog alert1 = alert.create();
        alert1.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert1.show();




    }
}