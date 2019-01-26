package com.example.headsup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IFragmentListener{

    //Components
    Button btn;
    ImageView settingsImg;
    Intent intent;
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;

    //Variables
    String theme, music, sound, time, chosenCategory;
    ArrayList <String> chosenWordList;
    SettingsDialogFragment editNameDialogFragment;
    HashMap<String, String[]> scoresMap;
    ArrayList<String> categories;
    ArrayList[] wordLists;
    HashMap<String, Integer> timeMap;
    int selectedTimeFrame;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSharedPreferences();
        timeMap = new HashMap<String, Integer>()
        {
            {
                put(GameParameters.TIMESTAMPS[0], 0);
                put(GameParameters.TIMESTAMPS[1], 1);
                put(GameParameters.TIMESTAMPS[2], 2);
            }

        };

        categories = new ArrayList<>(Arrays.asList((getResources().getStringArray(R.array.categories))));
        getSavedScoresFromMemory();
        updateTheme();
        setWordListIds();
        getSavedScoresFromMemory();

        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnPlay);
        btn.setOnClickListener(this);
        btn.setEnabled(false);

        initializeCards();

        settingsImg = findViewById(R.id.settings);
        settingsImg.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(GameScoresManager.isScoreChanged())
        {
            restart();
        }
    }

    private void initializeSharedPreferences()
    {
        sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        theme = sharedPref.getString("theme", GameParameters.DEFAULT_THEME);
        music = sharedPref.getString("music", GameParameters.DEFAULT_MUSIC);
        sound = sharedPref.getString("sound", GameParameters.DEFAULT_SOUND);
        time = sharedPref.getString("time", GameParameters.DEFAULT_TIME);
    }

    private void updateTheme()
    {
        if(theme.equals(GameParameters.DARK_THEME))
        {
            getApplicationContext().setTheme(R.style.AppTheme_DarkMode);
            setTheme(R.style.AppTheme_DarkMode);
        }
        else
        {
            getApplicationContext().setTheme(R.style.AppTheme);
        }
    }

    @Override
    public void onClick(View view) {

        Toast.makeText(this, Integer.toString(view.getId()), Toast.LENGTH_SHORT).show();
        if(view.getId() == R.id.settings)
        {
            openSettingsFragment();
        }
        else if(view.getId() == R.id.btnPlay)
        {
            launchGameActivity();
        }
        else if(view.getId() == R.id.card)
        {
            Toast.makeText(this, "WAWAWA", Toast.LENGTH_SHORT).show();
        }
    }


    public void restart()
    {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void initializeCards()
    {
        ArrayList <CardContent> cards = new ArrayList<CardContent>();

        ArrayList<Drawable> drawables = getImageDrawables();
        String currentCategory;

        for(int i = 0; i < categories.size(); i++)
        {
            currentCategory = categories.get(i);
            cards.add(new CardContent(currentCategory,
                                        drawables.get(i),
                                        scoresMap.get(currentCategory)[0],
                                        scoresMap.get(currentCategory)[1],
                                        scoresMap.get(currentCategory)[2],
                                        wordLists[i]));

        }
        registerCards(cards);
    }

    private void registerCards(ArrayList<CardContent> cards)
    {
        LinearLayout li = findViewById(R.id.li);
        LayoutInflater inf = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        for(final CardContent card : cards)
        {
            View cardView = inf.inflate(R.layout.card, null, false);

            ImageView img = cardView.findViewById(R.id.cardImage);
            img.setImageDrawable(card.getCardImage());

            TextView tv = cardView.findViewById(R.id.desc);
            tv.setText(card.getCardText());

            TextView tvTime = cardView.findViewById(R.id.cardTime);
            tvTime.setText(time);

            TextView tvScore = cardView.findViewById(R.id.cardScore);
            if(time.equals("60"))
            {
                tvScore.setText(card.getScore60());
                selectedTimeFrame = 0;
            }
            else if(time.equals("90"))
            {
                tvScore.setText(card.getScore90());
                selectedTimeFrame = 1;
            }
            else if(time.equals("120"))
            {
                tvScore.setText(card.getScore120());
                selectedTimeFrame = 2;
            }
            else
            {
                tvScore.setText("0");
                selectedTimeFrame = 0;
            }

            final String currentText = card.getCardText();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView sel = findViewById(R.id.select);

                    btn.setEnabled(true);
                    sel.setText(currentText);
                    chosenCategory = currentText;
                    chosenWordList = card.getWords();

                }
            });

            li.addView(cardView);
        }
    }

    private ArrayList<Drawable> getImageDrawables()
    {
        ArrayList<Drawable> drawables = new ArrayList<Drawable>();

        TypedArray a = this.getTheme().obtainStyledAttributes(
                null,
                R.styleable.appTheme,
                0, 0);

        try {
            drawables.add(a.getDrawable(R.styleable.appTheme_gymRes));
            drawables.add(a.getDrawable(R.styleable.appTheme_animalRes));
            drawables.add(a.getDrawable(R.styleable.appTheme_professionRes));
            drawables.add(a.getDrawable(R.styleable.appTheme_countryRes));
            drawables.add(a.getDrawable(R.styleable.appTheme_actionRes));
            drawables.add(a.getDrawable(R.styleable.appTheme_countryRes));
        } finally {
            a.recycle();
        }

        return drawables;
    }

    private void openSettingsFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        editNameDialogFragment = SettingsDialogFragment.newInstance(theme, music, sound, time);
        editNameDialogFragment.show(fm, theme);
    }

    private void getSavedScoresFromMemory()
    {
        scoresMap = new HashMap<>();
        for(String category : categories)
        {
            String[] s = getScoreArray(category);
            scoresMap.put(category, s);
        }

        GameScoresManager.setScores(scoresMap);
        GameScoresManager.setSharedPreferences(sharedPref, editor);
    }


    @Override
    public void onInputSent(String input) {

        editNameDialogFragment.receiveData(input);

    }

    private String[] getScoreArray(String category)
    {
        String[] scores;
        Gson gson = new Gson();
        String json = sharedPref.getString(category, null);
        if(json == null)
        {
            scores = GameParameters.DEFAULT_SCORE_ARRAY;
        }
        else
        {
            Type type = new TypeToken<String[]>() {}.getType();
            scores = gson.fromJson(json, type);
        }

        return scores;
    }

    private void launchGameActivity()
    {
        intent = new Intent(this, GameActivity.class);
        loadIntent(intent);
        fillGameScoreManager();
        startActivity(intent);
    }


    private void setWordListIds()
    {
        wordLists = new ArrayList[categories.size()];
        wordLists[0] = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.gym)));
        wordLists[1] = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.animals)));
        wordLists[2] = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.professions)));
        wordLists[3] = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.countries)));
        wordLists[4] = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.actions)));
        wordLists[5] = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.countries)));
    }

    private void loadIntent(Intent intent)
    {
        intent.putExtra(GameParameters.THEME_KEY, theme);
        intent.putExtra(GameParameters.CATEGORY_KEY, chosenCategory);
        intent.putExtra(GameParameters.WORDLIST_KEY, chosenWordList);
        intent.putExtra(GameParameters.TIME_KEY, time);

    }

    private void fillGameScoreManager()
    {
        GameScoresManager.setHighscoreToBeat(Integer.parseInt(scoresMap.get(chosenCategory)[selectedTimeFrame]));
        GameScoresManager.setWorkingCategory(chosenCategory);
        GameScoresManager.setWorkingTime(time);
    }


}
