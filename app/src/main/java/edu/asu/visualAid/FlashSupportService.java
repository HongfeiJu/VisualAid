package edu.asu.visualAid;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class FlashSupportService extends Service implements SensorEventListener {
    private boolean hasFlash, flashOn;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    Intent sendingIntent;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
            Log.i("Hongfei", "lightness: " +sensorEvent.values[0] );
            hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if(sensorEvent.values[0]<10){
                if(!flashOn){
                    flashOn = true;
                    CameraManager cameraManager = null;
                    String cameraId = null;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Log.i("Hongfei", "turn on flash");
                            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                            cameraId = cameraManager.getCameraIdList()[0];
                            cameraManager.setTorchMode(cameraId, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{
                if(flashOn){
                    flashOn = false;
                    CameraManager cameraManager = null;
                    String cameraId = null;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Log.i("Hongfei", "turn off flash");
                            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                            cameraId = cameraManager.getCameraIdList()[0];
                            cameraManager.setTorchMode(cameraId, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        Log.i("Hongfei", "flash support start");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        flashOn = false;
        sendingIntent = new Intent(DetectorActivity.BROADCAST_ACTION);

    }
}
