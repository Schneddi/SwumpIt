package it.swump.grilborzer.swumpit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.LinkedList;

public class DirectionClass implements SensorEventListener {

    SensorManager sensorManager;
    private int samplingFrequency = 10, interval = 10;
    private MainActivity activity;
    private Context context;
    private Sensor mMagnetometer;
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastMagnetometerSet = false;

    public DirectionClass(Context context) {
        //register Magnetometer
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.unregisterListener(this, mMagnetometer);
        sensorManager.registerListener(this, mMagnetometer, samplingFrequency * 1000);
    }

    public double getDirection() { //get the direction as an angle in degrees, the smartphone is pointing to
        double richtungsvektor = 0;
        if ((mLastMagnetometer[0] == 0)) { //if x is 0 the angle must be calculated with y
            if (mLastMagnetometer[1] > 0) { //positive y
                richtungsvektor = 90;
            } else if (mLastMagnetometer[1] < 0) { //negative y
                richtungsvektor = 270;
            }
        } else {
            richtungsvektor = Math.atan2(mLastMagnetometer[1], mLastMagnetometer[0]); //atan2 calcs the angle between two point (y,x) in rad
            richtungsvektor = Math.toDegrees(richtungsvektor); // calc to degrees
        }

        //TestNachricht Anfang
        Log.d("X und Y: ", Double.toString(mLastMagnetometer[0]) + ", " + Double.toString(mLastMagnetometer[1]));
        Log.d("Richtungsvektor: ", Double.toString(richtungsvektor));
        //TestNachricht Ende

        return richtungsvektor; //if return = 0, x and y of the Magnetometer are 0 (wrong!)
    }

    public boolean compareDirection(double compare) { //gives true if two smartphones point in the same direction
        double currentAngle = getDirection();
        if (currentAngle - 20 < compare && compare < currentAngle + 20) {
            Log.d("Vergleich: ", "True"); //TestNachricht
            return true;
        }
        Log.d("Vergleich: ", "False"); //TestNachricht
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == mMagnetometer) {
            System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
            mLastMagnetometerSet = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void register() { //falls man die Klasse nicht die ganze Zeit Sensordaten des Gyroscops abfragen lassen will, kann die Aufzeichnung starten/stoppen
        sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }
}

