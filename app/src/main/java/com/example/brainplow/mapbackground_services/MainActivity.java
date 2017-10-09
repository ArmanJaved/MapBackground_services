package com.example.brainplow.mapbackground_services;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

public class MainActivity extends Activity {

    private double PROXIMITY_RADIUS = 45;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //



        if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
            else
            {
                startService(new Intent(this, MyService.class));
            }
        }

    }




    public void nearbyrest()
    {
        String search = "restaurant";
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=31.5142043,74.344806&radius=45.0&type=restaurant&sensor=true&key=AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0";
        //  String url = getUrl(latitude, longitude, search);
        Object[] DataTransfer = new Object[2];
        DataTransfer[1] = url;
        GetNearbyBanksData getNearbyBanksData = new GetNearbyBanksData();
        getNearbyBanksData.execute(DataTransfer);


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
}
