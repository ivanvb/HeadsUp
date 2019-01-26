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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IFragmentListener{

    Button btn;
    ImageView settingsImg;
    Intent intent;
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;
    String theme, music, sound, time;
    String chosenCategory;
    ArrayList <String> chosenWordList;
    SettingsDialogFragment editNameDialogFragment;
    HashMap<String, String[]> scoresMap;
    ArrayList<String> categories;
    ArrayList[] wordLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSharedPreferences();


        categories = new ArrayList<>(Arrays.asList((getResources().getStringArray(R.array.categories))));
        getScores();
        updateTheme();
        setWordListIds();
        getScores();
       // saveScores();
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnPlay);
        btn.setOnClickListener(this);
        btn.setEnabled(false);

        setCards();

        settingsImg = findViewById(R.id.settings);
        settingsImg.setOnClickListener(this);
    }
    private void initializeSharedPreferences()
    {
        sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        theme = sharedPref.getString("theme", "light");
        music = sharedPref.getString("music", "on");
        sound = sharedPref.getString("sound", "on");
        time = sharedPref.getString("time", "60");
    }

    private void updateTheme()
    {
        if(theme.equals("dark"))
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
    public void onClick(View v) {

        if(v.getId() == R.id.settings)
        {
            openSettingsFragment();
        }
        else if(v.getId() == R.id.btnPlay)
        {
            intent = new Intent(this, GameActivity.class);
            intent.putExtra("theme", theme);
            intent.putExtra("category", chosenCategory);
            intent.putExtra("wordList", chosenWordList);
            intent.putExtra("time", time);
            startActivity(intent);
        }
    }


    public void restart()
    {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void setCards()
    {
        Resources res = getResources();
        ArrayList <CardContent> cards = new ArrayList<CardContent>();

        ArrayList<Drawable> drawables = getImageDrawables();
        String currentCategory = "ANIMALS";

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

        /*cards.add(new CardContent(currentCategory, drawables.get(i), scoresMap.get(currentCategory)[0],
                scoresMap.get(currentCategory)[2], scoresMap.get("GYM")[2], wordLists[0]));

        cards.add(new CardContent(currentCategory, drawables.get(3), scoresMap.get(currentCategory)[0],
                scoresMap.get("GYM")[1], scoresMap.get("GYM")[2], wordLists[0]));
        cards.add(new CardContent("ANIMALS", drawables.get(0),
                "13","12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.animals)))));
        cards.add(new CardContent("CHAVON", drawables.get(2), "13",
                "12", "120", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.professions)))));
        cards.add(new CardContent("CULTURES", drawables.get(4), "12",
                "12", "120",new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.countries)))));
        cards.add(new CardContent("ACTIONS", drawables.get(1), "12",
                "12", "1200", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.actions)))));
        cards.add(new CardContent("ACCENTS", drawables.get(4), "12",
                "12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.countries)))));*/

        registerCards(cards);
    }

    private void registerCards(ArrayList<CardContent> cards)
    {
        LinearLayout li = findViewById(R.id.li);
        LayoutInflater inf = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        for(final CardContent card : cards)
        {
            View cv = inf.inflate(R.layout.card, null, false);

            ImageView img = cv.findViewById(R.id.popcorn);
            img.setImageDrawable(card.getCardImage());

            TextView tv = cv.findViewById(R.id.desc);
            tv.setText(card.getCardText());

            TextView tvTime = cv.findViewById(R.id.cardTime);
            tvTime.setText(time);

            TextView tvScore = cv.findViewById(R.id.cardScore);
            if(time.equals("60"))
            {
                tvScore.setText(card.getScore60());
            }
            else if(time.equals("90"))
            {
                tvScore.setText(card.getScore90());
            }
            else if(time.equals("120"))
            {
                tvScore.setText(card.getScore120());
            }
            else
            {
                tvScore.setText("0");
            }

            final String currentText = card.getCardText();
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView sel = findViewById(R.id.select);

                    btn.setEnabled(true);
                    sel.setText(currentText);
                    chosenCategory = currentText;
                    chosenWordList = card.getWords();

                }
            });

            li.addView(cv);
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

    private void getScores()
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
            scores = new String[]{"0", "0", "0"};
        }
        else
        {
            Type type = new TypeToken<String[]>() {}.getType();
            scores = gson.fromJson(json, type);
        }

        return scores;
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





}
