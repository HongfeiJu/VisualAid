package org.tensorflow.lite.examples.detection;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import javax.net.ssl.HttpsURLConnection;

public class LocationTask extends AsyncTask {
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder = null;
    private double latitude, longitude;
    private TextToSpeech textToSpeech = null;
    private Activity activity = null;

    public LocationTask(Activity activity) {
        super();
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        geocoder = new Geocoder(activity, Locale.getDefault());
        latitude = 0;
        longitude = 0;
        textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int res = textToSpeech.setLanguage(Locale.ENGLISH);
                    if(res==TextToSpeech.LANG_MISSING_DATA||res==TextToSpeech.LANG_NOT_SUPPORTED){

                    }
                }
            }
        });
    }

    @Override
    protected String doInBackground(Object[] objects) {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Executor) activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            } catch (IOException e) {




















































































                                e.printStackTrace();
                            }

                            String address = addresses.get(0).getAddressLine(0);
                            String street = address.substring(0, address.indexOf(','));
                            try {
                                String weather = (String) new WeatherTask(latitude, longitude).execute().get();
                                speakText("the weather is "+weather);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            speakText("you are at "+street);
                        }
                    }
                });
        textToSpeech.shutdown();
        return latitude+" "+longitude;
    }

    private void speakText(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

}
