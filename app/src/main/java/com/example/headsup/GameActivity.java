package com.example.headsup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    int millisecondsLeft;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    Intent intent;
    ArrayList<String> words;
    long now, next;
    MediaPlayer mpc, mpi;
    boolean soundAlreadyPlayed, gameTime = true;
    SensorManager sensorManager;
    String currentWord;
    TextView tvWord;
    TextView tvTimer;
    Context sensorContext;
    String correctText, incorrectText;

    int STEP = 2000;
    int correct = 0, incorrect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
        setContentView(R.layout.activity_game);

        setGameTime();
        correctText = getResources().getString( R.string.correct );
        incorrectText = getResources().getString( R.string.incorrect );
        words = getWords();
        now = System.currentTimeMillis();
        changeWord();
        next = 0;
        mpc = MediaPlayer.create(this, R.raw.correct);
        mpi = MediaPlayer.create(this, R.raw.incorrect);

        soundAlreadyPlayed = false;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        tvWord = findViewById(R.id.tvWord);
        tvTimer =findViewById(R.id.time);

        startTimer();

    }

    private void updateTheme()
    {
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        String theme = (String) bundle.get("theme");
        changeTheme(theme);
    }

    private void setGameTime()
    {
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        millisecondsLeft = Integer.parseInt((String)bundle.get("time")) * 1000;
    }

    private ArrayList<String> getWords()
    {
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<String> words = (ArrayList<String>) bundle.get("wordList");
        return words;
    }

    private void changeTheme(String theme)
    {
        if(theme.equals("light"))
        {
            setTheme(R.style.AppTheme);
        }
        else
        {
            setTheme(R.style.AppTheme_DarkMode);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d(TAG, "onSensorChanged()");
        sensorContext = this;
        if (event.values == null) {

            return;
        }
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                break;
            default:

                return;
        }
        if (mGravity == null) {

            return;
        }
        if (mGeomagnetic == null) {

            return;
        }
        float R[] = new float[9];
        if (! SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic)) {

            return;
        }

        float orientation[] = new float[9];
        SensorManager.getOrientation(R, orientation);
        float roll = orientation[2];
        float pitch = orientation[1];
        float azimuth = orientation[0];

        int rollDeg = (int) Math.round(Math.toDegrees(roll));
        int pitchDeg = (int) Math.round(Math.toDegrees(pitch));
        int azimuthDeg = (int) Math.round(Math.toDegrees(azimuth));

        if(pitchDeg > -15 && pitchDeg < 15)
        {
            now = System.currentTimeMillis();
            if(now >= next )
            {
                if(rollDeg < -120 )
                {
                    tvWord.setText(correctText);
                    if(!soundAlreadyPlayed)
                    {
                        mpc.start();
                        soundAlreadyPlayed = true;
                        changeWord();
                        correct++;
                    }

                    next = now + STEP;

                }
                else if(rollDeg > -35 )
                {

                    tvWord.setText(incorrectText);
                    if(!soundAlreadyPlayed)
                    {
                        mpi.start();
                        soundAlreadyPlayed = true;
                        changeWord();
                        incorrect++;
                    }

                    next = now + STEP;

                }
                else
                {
                    //mpc.start();
                    tvWord.setText(currentWord);
                    soundAlreadyPlayed = false;
                }
            }

            else
            {
                //mpc.start();
                if(tvWord.getText().toString().equals("Please adjust your device"))
                {
                    tvWord.setText(currentWord);
                }
                if(rollDeg <= -35 && rollDeg >= -120)
                {
                    soundAlreadyPlayed = false;
                    tvWord.setText(currentWord);
                }

            }
        }
        else
        {
            tvWord.setText("Please adjust your device");
        }


        if(!gameTime)
        {
            sensorManager.unregisterListener(this);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void changeWord()
    {
        Random r = new Random();
        if(words.size() > 0)
        {
            int index = r.nextInt(words.size());
            currentWord = words.get(index);
            words.remove(currentWord);
        }
        else
        {
            currentWord = "BAZINGA";
        }


    }

    private void startTimer()
    {
        CountDownTimer timer = new CountDownTimer(millisecondsLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                updateTimer(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                gameTime = false;
                tvWord.setText("TIME'S UP");
                gameTime = false;
                waitNSeconds(3);
            }
        }.start();
    }

    public void updateTimer(long m)
    {
        if(tvTimer != null)
        {
            tvTimer.setText(Long.toString(m/1000));
        }
        else
        {
            tvTimer = findViewById(R.id.tvTime);
            tvTimer.setText(Long.toString(m/1000));
        }


    }

   

    public void waitNSeconds(long n)
    {
        CountDownTimer t = new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //TO DO
                //IMPLEMENT FRAGMENT POP UP WITH SCORES
            }
        }.start();
    }
}
