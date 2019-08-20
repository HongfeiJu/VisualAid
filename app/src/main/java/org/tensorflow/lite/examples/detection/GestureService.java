package org.tensorflow.lite.examples.detection;

/**
 * modified based on jlee375's version.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class GestureService extends Service implements SensorEventListener {

    private SensorManager accelManage;
    private Sensor senseAccel;
    float accelValuesX[] = new float[128];
    float accelValuesY[] = new float[128];
    float accelValuesZ[] = new float[128];
    int index = 0;
    int k=0;
    Bundle b;

    Handler handler;

    Intent sendingIntent;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            accelValuesX[index] = sensorEvent.values[0];
            accelValuesY[index] = sensorEvent.values[1];
            accelValuesZ[index] = sensorEvent.values[2];
            if(index >= 127){
                index = 0;
                accelManage.unregisterListener(this);
                shakeRecognition();
                accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }


    public void shakeRecognition(){
        float minX = 0, maxX = 0;
        for(int i=11;i<128;i++){
            minX = Math.min(minX, accelValuesX[i]);
            maxX = Math.max(maxX, accelValuesX[i]);
        }
        if(Math.abs(maxX - minX) > 15 ){
            Log.i("Hongfei_test", "shake");
            sendMessage("shake");
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate(){
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);

        sendingIntent = new Intent(DetectorActivity.BROADCAST_ACTION);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        b = intent.getExtras();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    protected void sendMessage(final String msg) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                sendingIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendingIntent.putExtra("gesture", msg);
                Log.i("info", "service: "+sendingIntent.getStringExtra("consoleMsg"));
                sendBroadcast(sendingIntent);
            }
        };
        Thread msgThread = new Thread(runnable);
        msgThread.start();

    }
}
