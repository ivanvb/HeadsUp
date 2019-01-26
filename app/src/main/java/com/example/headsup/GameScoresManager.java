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
    private static int highscoreToBeat;
    private static String workingCategory;
    private static String workingTime;
    private static boolean scoreChanged;

    public static String getWorkingTime() {
        return workingTime;
    }

    public static void setWorkingTime(String workingTime) {
        GameScoresManager.workingTime = workingTime;
    }

    public static String getWorkingCategory() {
        return workingCategory;
    }

    public static void setWorkingCategory(String workingCategory) {
        GameScoresManager.workingCategory = workingCategory;
    }

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
    public static int getHighscoreToBeat() {
        return highscoreToBeat;
    }

    public static void setHighscoreToBeat(int highscoreToBeat) {
        GameScoresManager.highscoreToBeat = highscoreToBeat;
    }

    public static boolean isScoreChanged() {
        return scoreChanged;
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

    public static boolean processScore(int score)
    {
        if(score > highscoreToBeat)
        {
            saveScore(workingCategory, workingTime, Integer.toString(score));
            scoreChanged = true;
            return true;
        }

        scoreChanged = false;
        return false;
    }
}
