package com.example.headsup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IFragmentListener{

    Button btn;
    ImageView settingsImg;
    Dialog dialog;
    Intent intent;
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;
    String theme, music, sound, time;
    String chosenCategory;
    ArrayList <String> chosenWordList;
    SettingsDialogFragment editNameDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSharedPreferences();
        updateTheme();
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
        cards.add(new CardContent("GYM", drawables.get(3), "13",
                            "12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.gym)))));
        cards.add(new CardContent("ANIMALS", drawables.get(0),
                "13","12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.animals)))));
        cards.add(new CardContent("CHAVON", drawables.get(2), "13",
                "12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.professions)))));
        cards.add(new CardContent("CULTURES", drawables.get(4), "12",
                "12", "12",new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.countries)))));
        cards.add(new CardContent("ACTIONS", drawables.get(1), "12",
                "12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.actions)))));
        cards.add(new CardContent("ACCENTS", drawables.get(4), "12",
                "12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.countries)))));

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
            drawables.add(a.getDrawable(R.styleable.appTheme_animalRes));
            drawables.add(a.getDrawable(R.styleable.appTheme_actionRes));
            drawables.add(a.getDrawable(R.styleable.appTheme_professionRes));
            drawables.add(a.getDrawable(R.styleable.appTheme_gymRes));
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


    @Override
    public void onInputSent(String input) {

        editNameDialogFragment.receiveData(input);

    }

}
