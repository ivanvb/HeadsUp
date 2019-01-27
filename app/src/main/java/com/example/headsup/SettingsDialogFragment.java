package com.example.headsup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsDialogFragment extends DialogFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    //Components
    private SharedPreferences sharedPref ;
    private SharedPreferences.Editor editor;
    private static String fragTheme, fragMusic, fragSound, fragTime;
    private IFragmentListener listener;
    private Switch switchTheme, switchMusic, switchSoundEffects;
    private ImageView imgCheck;
    private boolean resetHighScore = false;
    private TextView tvTimeSetting;

    public SettingsDialogFragment() { }

    public static SettingsDialogFragment newInstance(String theme, String music, String sound, String time) {
        SettingsDialogFragment frag = new SettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString(GameParameters.THEME_KEY, theme);
        frag.setArguments(args);

        fragTheme = theme;
        fragMusic = music;
        fragSound = sound;
        fragTime = time;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.round_fragment);

        return inflater.inflate(R.layout.settings_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        initializeSharedPreferences();

        switchTheme = view.findViewById(R.id.switchDarkMode);
        setSwitchesState(fragTheme, GameParameters.DARK_THEME, switchTheme);
        switchTheme.setOnCheckedChangeListener(this);

        switchMusic = view.findViewById(R.id.switchMusic);
        setSwitchesState(fragMusic, GameParameters.DEFAULT_MUSIC, switchMusic);
        switchMusic.setOnCheckedChangeListener(this);

        switchSoundEffects = view.findViewById(R.id.switchSoundEffects);
        setSwitchesState(fragSound, GameParameters.DEFAULT_SOUND, switchSoundEffects);
        switchSoundEffects.setOnCheckedChangeListener(this);

        RelativeLayout rlResetHighScore = view.findViewById(R.id.layoutResetHighscore);
        rlResetHighScore.setOnClickListener(this);

        RelativeLayout rlTime = view.findViewById(R.id.layoutTime);
        rlTime.setOnClickListener(this);

        tvTimeSetting = view.findViewById(R.id.tvTimeSetting);
        tvTimeSetting.setText(fragTime);

        imgCheck = view.findViewById(R.id.imgCheck);
        Button btnApplyChanges = view.findViewById(R.id.btnApplyChanges);
        btnApplyChanges.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnApplyChanges)
        {
            saveChanges();
            if(resetHighScore)
            {
                ArrayList<String> categories = ((MainActivity)getActivity()).getCategories();
                resetValues(categories);
            }
            ((MainActivity)getActivity()).restart();
            dismiss();
        }
        else if(v.getId() == R.id.layoutResetHighscore)
        {
            changeResetHighScoreState();
        }
        else if(v.getId() == R.id.layoutTime)
        {
            openTimeSelectionFragment();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.switchDarkMode)
        {
            fragTheme = changeState(fragTheme,
                    new Pair<>(GameParameters.DARK_THEME, GameParameters.LIGHT_THEME));
        }
        else if(buttonView.getId() == R.id.switchMusic)
        {
            fragMusic = changeState(fragMusic, new Pair<>("on", "off"));
        }
        else if(buttonView.getId() == R.id.switchSoundEffects)
        {
            fragSound = changeState(fragSound, new Pair<>("on", "off"));
        }
    }



    private String changeState(String currentState, Pair<String, String> states)
    {
        String newState;
        if(currentState.equals(states.first))
        {
            newState = states.second;
        }
        else
        {
            newState = states.first;
        }

        return newState;
    }

    private void initializeSharedPreferences()
    {
        sharedPref = this.getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    private void setSwitchesState(String currentState, String activeState, Switch vSwicth)
    {
        if(currentState.equals(activeState))
        {
            vSwicth.setChecked(true);
        }
    }

    private void saveChanges()
    {
        saveState(GameParameters.THEME_KEY, fragTheme);
        saveState(GameParameters.MUSIC_KEY, fragMusic);
        saveState(GameParameters.SOUND_KEY, fragSound);
        saveState(GameParameters.TIME_KEY, fragTime);
    }

    private void saveState(String id, String state)
    {
        editor.putString(id, state);
        editor.commit();
    }

    private void changeResetHighScoreState()
    {
        resetHighScore = !resetHighScore;
        imgCheck.setAlpha((resetHighScore)? 1f : 0f);
    }

    private void openTimeSelectionFragment()
    {
        FragmentManager fm = getFragmentManager();
        TimeSelectSettingDialogFragment editNameDialogFragment = TimeSelectSettingDialogFragment.newInstance(fragTime);
        editNameDialogFragment.show(fm, null);
    }

    private void resetValues(ArrayList<String> categories)
    {
        for(String category : categories)
        {
            sharedPref = getActivity().getSharedPreferences(GameParameters.SETTINGS_KEY, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.remove(category);
            editor.apply();
        }
    }


    public void receiveData(String data)
    {
        tvTimeSetting.setText(data);
        fragTime = data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (IFragmentListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
