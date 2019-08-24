package edu.asu.visualAid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.asu.visualAid.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WeatherTask extends AsyncTask {
    private double latitude, longitude;
    private Context context;

    public WeatherTask(double latitude, double longitude, Context _context) {
        super();
        this.context = _context;
        this.latitude = latitude;
        this.longitude = longitude;
        Log.i("info", "lat and lon "+latitude+" "+longitude);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if(!isInternetAvailable()){
            return "internet not available";
        }

        try{
            URL url = new URL(  context.getString(R.string.weather_api,
                    Double.toString(latitude), Double.toString(longitude))
                    + context.getString(R.string.weather_api_key));

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            if(connection.getResponseCode() == 200){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb= new StringBuilder();
                String line = "";
                while((line=bufferedReader.readLine())!=null){
                    sb.append(line);
                }
                String response = sb.toString();
                Log.i("weather", response);
                connection.disconnect();
                return parseJson(response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String parseJson(String response){
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        JsonArray weather = jsonObject.get("weather").getAsJsonArray();
        JsonObject description = weather.get(0).getAsJsonObject();
        String res=description.get("description").getAsString();
        return res;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
