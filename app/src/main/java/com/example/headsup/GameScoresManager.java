package com.example.headsup;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class GameScoresManager {

    private static HashMap<String, String[]> scores;
    private static SharedPreferences scoreSharedPreferences;
    private static SharedPreferences.Editor scoreEditor;

    public static void setScores(HashMap<String, String[]> receivedScores)
    {
        if(scores == null)
        {
            scores = new HashMap<>();
            scores = receivedScores;
        }
    }

    public static void setSharedPreferences(SharedPreferences sharedPreferences, SharedPreferences.Editor editor)
    {
        scoreSharedPreferences = sharedPreferences;
        scoreEditor = editor;
    }

    public static HashMap<String, String[]> getScores()
    {
        return scores;
    }

    public static void saveScore(String category, String time, String score)
    {
        String[] scoresArray = scores.get(category.toUpperCase());
        short changePosition = 0;
        if(time.equals("60")) changePosition = 0;
        else if(time.equals("90")) changePosition = 1;
        else if(time.equals("120")) changePosition = 2;

        scores.put(category.toUpperCase(), substitueValueInArray(changePosition, score, scoresArray));
        saveIntoSharedPreferences(category);
    }

    private static String[] substitueValueInArray(int position, String value, String[] array)
    {
        String[] returnArray = new String[array.length];
        for(int i = 0; i < array.length; i++)
        {
            if(i != position)
            {
                returnArray[i] = array[i];
            }
            else
            {
                returnArray[i] = value;
            }
        }

        return returnArray;
    }

    private static void saveIntoSharedPreferences(String category)
    {
        Gson gson = new Gson();
        String json = gson.toJson(scores.get(category.toUpperCase()));
        scoreEditor.putString(category.toUpperCase(), json);
        scoreEditor.apply();
    }

}
