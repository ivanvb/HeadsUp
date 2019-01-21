package com.example.headsup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn;
    ImageView settingsImg;
    Dialog dialog;
    Intent intent;
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;
    String theme;
    String chosenCategory;
    ArrayList <String> chosenWordList;
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
        sharedPref = getSharedPreferences("wawawa", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        theme = sharedPref.getString("theme", "light");
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
            showSettings();
        }
        else if(v.getId() == R.id.btnPlay)
        {
            intent = new Intent(this, GameActivity.class);
            intent.putExtra("theme", theme);
            intent.putExtra("category", chosenCategory);
            intent.putExtra("wordList", chosenWordList);
            startActivity(intent);
        }
    }

    private void changeTheme()
    {
        btn.setEnabled(false);
        if(theme.equals("dark")){

            editor.putString("theme", "light");
            editor.commit();
            restart();
        }
        else
        {
            editor.putString("theme", "dark");
            editor.commit();
            restart();
        }

    }

    private void restart()
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

        cards.add(new CardContent("MOVIES", R.drawable.cinema, "13",
                            "12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.gym)))));
        cards.add(new CardContent("ANIMALS", R.drawable.animal_icon,
                "13","12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.animals)))));
        cards.add(new CardContent("SINGERS", R.drawable.micro, "13",
                "12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.proffesions)))));
        cards.add(new CardContent("CULTURES", R.drawable.globe, "12",
                "12", "12",new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.countries)))));
        cards.add(new CardContent("ACTIONS", R.drawable.exercise, "12",
                "12", "12", new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.actions)))));
        cards.add(new CardContent("ACCENTS", R.drawable.voice, "12",
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
            img.setImageResource(card.getCardImage());

            TextView tv = cv.findViewById(R.id.desc);
            tv.setText(card.getCardText());

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

            int n = R.drawable.chef;
            li.addView(cv);
        }
    }

    private void showSettings()
    {
        dialog = new Dialog(this);

        dialog.setContentView(R.layout.settings_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        final Switch s = dialog.findViewById(R.id.switchDarkMode);
        String state = sharedPref.getString(Integer.toString(s.getId()), "false");
        if(state.equals("true"))
        {
            s.setChecked(true);
        }

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dialog.dismiss();
                saveState(Integer.toString(s.getId()), Boolean.toString(s.isChecked()));
                changeTheme();
            }
        });

    }

    private void saveState(String id, String state)
    {
        editor.putString(id, state);
        editor.commit();
    }


}
