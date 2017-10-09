package com.example.brainplow.mapbackground_services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GetNearbyBanksData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    String url;

    Context context;
    ArrayList<String> list = new ArrayList<String>();
    public static final String MY_PREFS_NAME = "settings";

    public static final String LatlngNearBY = "restlatlng";



    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyBanksData", "doInBackground entered");

            url = (String) params[1];
            UrlConnection urlConnection = new UrlConnection();
            googlePlacesData = urlConnection.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {


        for (int i = 0; i < nearbyPlacesList.size(); i++) {

            Log.d("onPostExecute","Entered into showing locations");
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            list.add(placeName);

            if (i==0)
            {
                SharedPreferences.Editor editor = context.getSharedPreferences(LatlngNearBY, MODE_PRIVATE).edit();
                String lalng= lat + "-" + lng;
                editor.putString("Latlnfg", lalng);
                editor.apply();
            }


        }



    }




}
