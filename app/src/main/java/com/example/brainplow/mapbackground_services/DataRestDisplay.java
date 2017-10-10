package com.example.brainplow.mapbackground_services;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataRestDisplay extends AppCompatActivity {



    DatabaseReference artistreference;
    ListView listViewartists;
    List<Resaurants> resaurantsList;

    private ObjectAnimator waveOneAnimator;
    private ObjectAnimator waveTwoAnimator;
    private ObjectAnimator waveThreeAnimator;
    private ObjectAnimator waveFourAnimator;
    private ObjectAnimator waveFiveAnimator;


    private TextView hangoutTvOne;
    private TextView hangoutTvTwo;
    private TextView hangoutTvThree;
    private TextView hangoutTvfour;
    private TextView hangoutTvfive;

    private int screenWidth;
    public static final String TotalAmount = "totalamount";

    private int amounttotal=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_list);



        SharedPreferences.Editor editor = getSharedPreferences(TotalAmount, MODE_PRIVATE).edit();
        editor.putInt("Tamount", 0);
        editor.apply();

        //********* hangout ************
        hangoutTvOne = (TextView) findViewById(R.id.hangoutTvOne);
        hangoutTvTwo = (TextView) findViewById(R.id.hangoutTvTwo);
        hangoutTvThree = (TextView) findViewById(R.id.hangoutTvThree);
        hangoutTvfour = (TextView) findViewById(R.id.hangoutTvFour);
        hangoutTvfive = (TextView) findViewById(R.id.hangoutTvFive);



        hangoutTvOne.setVisibility(View.GONE);
        hangoutTvTwo.setVisibility(View.GONE);
        hangoutTvThree.setVisibility(View.GONE);
        hangoutTvfour.setVisibility(View.GONE);
        hangoutTvfive.setVisibility(View.GONE);



        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;


        hangoutTvOne.setVisibility(View.VISIBLE);
        hangoutTvTwo.setVisibility(View.VISIBLE);
        hangoutTvThree.setVisibility(View.VISIBLE);
        hangoutTvfour.setVisibility(View.VISIBLE);
        hangoutTvfive.setVisibility(View.VISIBLE);


        waveAnimation();

        artistreference = FirebaseDatabase.getInstance().getReference("restaurants").child("arman");
        listViewartists = (ListView)findViewById(R.id.listviewitems);

        resaurantsList = new ArrayList<>();
        data_artist();




    }

    public void data_artist()
    {

        artistreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        hangoutTvOne.setVisibility(View.GONE);
                        hangoutTvTwo.setVisibility(View.GONE);
                        hangoutTvThree.setVisibility(View.GONE);
                        hangoutTvfour.setVisibility(View.GONE);
                        hangoutTvfive.setVisibility(View.GONE);


                        resaurantsList.clear();

                        resaurantsList.size();
//
                        for (DataSnapshot artistsnapshot: dataSnapshot.getChildren())
                        {

                            Resaurants resaurants = artistsnapshot.getValue(Resaurants.class);
                            resaurantsList.add(resaurants);

                            RestaurantsList adaptor = new RestaurantsList(DataRestDisplay.this, resaurantsList);
                            listViewartists.setAdapter(adaptor);
                        }

                        resaurantsList.size();

                        int amount = 0;
                        for (int i = 0; i< resaurantsList.size();i++)
                        {
                            String abc = resaurantsList.get(i).getRestamount();

                        SharedPreferences prefs = getSharedPreferences(TotalAmount, MODE_PRIVATE);
                         amount= prefs.getInt("Tamount", 0); //0 is the default value.
                        amount = amount+ Integer.parseInt(abc);



                        SharedPreferences.Editor editor = getSharedPreferences(TotalAmount, MODE_PRIVATE).edit();
                        editor.putInt("Tamount", amount);
                        editor.apply();
                        }


                        Button btnamount = (Button)findViewById(R.id.amount_button);
                        String strI = Integer.toString(amount);
                        btnamount.setText("Total Amount : "+ strI + " PKR");
                       //0 is the default value.

                    }
                }, 3000);




               

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void waveAnimation() {
        PropertyValuesHolder tvOne_Y = PropertyValuesHolder.ofFloat(hangoutTvOne.TRANSLATION_Y, -40.0f);
        PropertyValuesHolder tvOne_X = PropertyValuesHolder.ofFloat(hangoutTvOne.TRANSLATION_X, 0);
        waveOneAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvOne, tvOne_X, tvOne_Y);
        waveOneAnimator.setRepeatCount(-1);
        waveOneAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveOneAnimator.setDuration(600);
        waveOneAnimator.start();

        PropertyValuesHolder tvTwo_Y = PropertyValuesHolder.ofFloat(hangoutTvTwo.TRANSLATION_Y, -40.0f);
        PropertyValuesHolder tvTwo_X = PropertyValuesHolder.ofFloat(hangoutTvTwo.TRANSLATION_X, 0);
        waveTwoAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvTwo, tvTwo_X, tvTwo_Y);
        waveTwoAnimator.setRepeatCount(-1);
        waveTwoAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveTwoAnimator.setDuration(600);
        waveTwoAnimator.setStartDelay(300);
        waveTwoAnimator.start();

        PropertyValuesHolder tvThree_Y = PropertyValuesHolder.ofFloat(hangoutTvThree.TRANSLATION_Y, -40.0f);
        PropertyValuesHolder tvThree_X = PropertyValuesHolder.ofFloat(hangoutTvThree.TRANSLATION_X, 0);
        waveThreeAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvThree, tvThree_X, tvThree_Y);
        waveThreeAnimator.setRepeatCount(-1);
        waveThreeAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveThreeAnimator.setDuration(600);
        waveThreeAnimator.setStartDelay(400);
        waveThreeAnimator.start();

        PropertyValuesHolder tvFour_Y = PropertyValuesHolder.ofFloat(hangoutTvfour.TRANSLATION_Y, -40.0f);
        PropertyValuesHolder tvFour_X = PropertyValuesHolder.ofFloat(hangoutTvfour.TRANSLATION_X, 0);
        waveFourAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvfour, tvFour_Y, tvFour_X);
        waveFourAnimator.setRepeatCount(-1);
        waveFourAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveFourAnimator.setDuration(600);
        waveFourAnimator.setStartDelay(500);
        waveFourAnimator.start();

        PropertyValuesHolder tvFive_Y = PropertyValuesHolder.ofFloat(hangoutTvfive.TRANSLATION_Y, -40.0f);
        PropertyValuesHolder tvFive_X = PropertyValuesHolder.ofFloat(hangoutTvfive.TRANSLATION_X, 0);
        waveFiveAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvfive, tvFive_Y, tvFive_X);
        waveFiveAnimator.setRepeatCount(-1);
        waveFiveAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveFiveAnimator.setDuration(600);
        waveFiveAnimator.setStartDelay(600);
        waveFiveAnimator.start();


    }



}
