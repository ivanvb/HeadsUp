package com.example.headsup;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    //Components
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Intent callerActivityIntent;
    private Bundle callerActivityBundle;
    private MediaPlayer correctSound, incorrectSound;
    private CountDownTimer preparationTimer, mainTimer, delayBeforeEndTimer;
    private TextView tvWord, tvTimer;

    //Variables
    private int millisecondsLeft;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private ArrayList<String> words;
    private long now, next = 0;
    private boolean soundAlreadyPlayed = false, gameTime = false;
    private String currentWord, correctText, incorrectText;
    private ArrayList<String> correctWords, incorrectWords, initialWordlist;
    private String initialTheme, initialTimer;
    private String timeEndedMessage, adjustDeviceMessage;
    private final int STEP = 2000, PREPARATION_TIME_IN_MILLIS = 5000;
    private final int MILLIS_IN_A_SECOND = 1000;
    private final long DELAY_BEFORE_RESULTS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeVariables();
        updateTheme();
        setContentView(R.layout.activity_game);

        tvWord = findViewById(R.id.tvWord);
        tvTimer = findViewById(R.id.time);

        changeWord();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startPreparationTimer();
    }

    private void initializeVariables()
    {
        callerActivityIntent = getIntent();
        callerActivityBundle = callerActivityIntent.getExtras();

        timeEndedMessage = getResources().getString(R.string.time_is_up);
        adjustDeviceMessage = getResources().getString(R.string.adjust_device);

        setGameTime();
        initializeText();

        now = System.currentTimeMillis();

        initializeSoundEffects();
        initializeSensors();
    }

    private void setGameTime()
    {
        String time = (String)callerActivityBundle.get(GameParameters.TIME_KEY);
        initialTimer = time;
        millisecondsLeft = Integer.parseInt(time) * MILLIS_IN_A_SECOND;
    }

    private void initializeText()
    {
        correctWords = new ArrayList<>(); incorrectWords = new ArrayList<>();
        correctText = getResources().getString( R.string.correct );
        incorrectText = getResources().getString( R.string.incorrect );
        words = getWords();
    }

    private ArrayList<String> getWords()
    {
        ArrayList<String> words = (ArrayList<String>) callerActivityBundle.get(GameParameters.WORDLIST_KEY);
        initialWordlist = words;
        return words;
    }

    private void initializeSoundEffects()
    {
        correctSound = MediaPlayer.create(this, R.raw.correct);
        incorrectSound = MediaPlayer.create(this, R.raw.incorrect);
    }

    private void initializeSensors()
    {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    private void updateTheme()
    {
        String theme = (String) callerActivityBundle.get(GameParameters.THEME_KEY);
        initialTheme = theme;
        changeTheme(theme);
    }

    private void changeTheme(String theme)
    {
        if(theme.equals(GameParameters.LIGHT_THEME))
        {
            setTheme(R.style.AppTheme);
        }
        else
        {
            setTheme(R.style.AppTheme_DarkMode);
        }
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
    }

    private void startPreparationTimer()
    {
        preparationTimer = new CountDownTimer(PREPARATION_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvWord.setText(Long.toString(millisUntilFinished/MILLIS_IN_A_SECOND));
            }
            @Override
            public void onFinish() {
                gameTime = true;
                registerSensorListeners();
                tvWord.setText(currentWord);
                startMainTimer();
            }
        }.start();
    }

    private void registerSensorListeners()
    {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void startMainTimer()
    {
        mainTimer = new CountDownTimer(millisecondsLeft, MILLIS_IN_A_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {

                updateTimer(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                gameTime = false;
                tvTimer.setText("");
                tvWord.setText(timeEndedMessage);
                delayTimer(DELAY_BEFORE_RESULTS);
            }
        }.start();
    }

    private void updateTimer(long milliseconds)
    {
        if(tvTimer != null)
        {
            tvTimer.setText(Long.toString(milliseconds/MILLIS_IN_A_SECOND));
        }
        else
        {
            tvTimer = findViewById(R.id.tvTime);
            tvTimer.setText(Long.toString(milliseconds/MILLIS_IN_A_SECOND));
        }
    }

    private void delayTimer(long delay)
    {
        delayBeforeEndTimer = new CountDownTimer(delay, MILLIS_IN_A_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                openResultsFragment();
            }
        }.start();
    }

    private void openResultsFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        ResultsDialogFragment resultsDialogFragment =
                ResultsDialogFragment.newInstance(correctWords, incorrectWords);
        resultsDialogFragment.show(fm, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            cancelAllTimers();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void cancelAllTimers()
    {
        if(preparationTimer != null) preparationTimer.cancel();
        if(mainTimer != null) mainTimer.cancel();
        if(delayBeforeEndTimer != null) delayBeforeEndTimer.cancel();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        checkForGameTime();
        if(!allSensorsWorking(event)) return;

        Pair<Integer, Integer> degrees = getDegrees();
        int pitchDeg = degrees.first; int rollDeg = degrees.second;

        if(pitchDeg > -GameParameters.OUT_OF_POSITION_PITCH_DEGREE &&
                pitchDeg < GameParameters.OUT_OF_POSITION_PITCH_DEGREE)
        {
            now = System.currentTimeMillis();
            if(now >= next && gameTime)
            {
                if(rollDeg < GameParameters.CORRECT_ROLL_DEGREE ) { correctEvent(); }
                else if(rollDeg > GameParameters.INCORRECT_ROLL_DEGREE) { incorrectEvent(); }
                else
                {
                    tvWord.setText(currentWord);
                    soundAlreadyPlayed = false;
                }
            } else
            {
                updateDuringDeadTime(rollDeg);
            }
        } else { tvWord.setText(adjustDeviceMessage); }

    }

    private void checkForGameTime()
    {
        if(!gameTime)
        {
            sensorManager.unregisterListener(this);
        }
    }

    private boolean allSensorsWorking(SensorEvent event)
    {
        if (event.values == null) {
            return false;
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

                return false;
        }

        if (mGravity == null) {

            return false;
        }
        if (mGeomagnetic == null) {

            return false;
        }
        return true;
    }

    private Pair<Integer, Integer> getDegrees()
    {
        float R[] = new float[9];
        if (! SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic)) {

            return null;
        }

        float orientation[] = new float[9];
        SensorManager.getOrientation(R, orientation);


        float roll = orientation[2];
        float pitch = orientation[1];
        int rollDeg = (int) Math.round(Math.toDegrees(roll));
        int pitchDeg = (int) Math.round(Math.toDegrees(pitch));

        return new Pair<>(pitchDeg, rollDeg);
    }

    private void correctEvent()
    {
        tvWord.setText(correctText);
        if(!soundAlreadyPlayed)
        {
            correctSound.start();
            soundAlreadyPlayed = true;
            correctWords.add(currentWord);
            changeWord();
        }

        next = now + STEP;
    }

    private void incorrectEvent()
    {
        tvWord.setText(incorrectText);
        if(!soundAlreadyPlayed)
        {
            incorrectSound.start();
            soundAlreadyPlayed = true;
            incorrectWords.add(currentWord);
            changeWord();
        }

        next = now + STEP;
    }

    private void updateDuringDeadTime(int rollDeg)
    {
        if(tvWord != null && tvWord.getText().toString().equals(adjustDeviceMessage))
        {
            tvWord.setText(currentWord);
        }
        if(rollDeg <= GameParameters.INCORRECT_ROLL_DEGREE &&
                rollDeg >= GameParameters.CORRECT_ROLL_DEGREE && gameTime)
        {
            soundAlreadyPlayed = false;
            tvWord.setText(currentWord);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void restart()
    {
        Intent restartIntent = new Intent(getApplicationContext(), GameActivity.class);

        restartIntent.putExtra(GameParameters.THEME_KEY, initialTheme);
        restartIntent.putExtra(GameParameters.WORDLIST_KEY, initialWordlist);
        restartIntent.putExtra(GameParameters.TIME_KEY, initialTimer);

        startActivity(restartIntent);
        finish();
    }

}
