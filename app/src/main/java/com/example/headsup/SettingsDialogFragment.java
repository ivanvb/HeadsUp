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

public class SettingsDialogFragment extends DialogFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;
    private static String fragTheme, fragMusic, fragSound, fragTime;
    private IFragmentListener listener;
    Switch switchTheme, switchMusic, switchSoundEffects;
    ImageView imgCheck;
    boolean resetHighscore = false;
    TextView tvTimeSetting;

    public SettingsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SettingsDialogFragment newInstance(String theme, String music, String sound, String time) {
        SettingsDialogFragment frag = new SettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString("theme", theme);
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

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.MyCustomTheme;

        switchTheme = view.findViewById(R.id.switchDarkMode);
        setSwitchesState(fragTheme, "dark", switchTheme);
        switchTheme.setOnCheckedChangeListener(this);

        switchMusic = view.findViewById(R.id.switchMusic);
        setSwitchesState(fragMusic, "on", switchMusic);
        switchMusic.setOnCheckedChangeListener(this);

        switchSoundEffects = view.findViewById(R.id.switchSoundEffects);
        setSwitchesState(fragSound, "on", switchSoundEffects);
        switchSoundEffects.setOnCheckedChangeListener(this);

        RelativeLayout rlResetHighscore = view.findViewById(R.id.layoutResetHighscore);
        rlResetHighscore.setOnClickListener(this);

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
            listener.onInputSent(fragTime + "commit");
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
            fragTheme = changeState(fragTheme, new Pair<>("dark", "light"));
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
        saveState("theme", fragTheme);
        saveState("music", fragMusic);
        saveState("sound", fragSound);
        saveState("time", fragTime);
    }

    private void saveState(String id, String state)
    {
        editor.putString(id, state);
        editor.commit();
    }

    private void changeResetHighScoreState()
    {
        resetHighscore = !resetHighscore;
        imgCheck.setAlpha((resetHighscore)? 1f : 0f);
    }

    private void openTimeSelectionFragment()
    {
        FragmentManager fm = getFragmentManager();
        TimeSelectSettingDialogFragment editNameDialogFragment = TimeSelectSettingDialogFragment.newInstance(fragTime);
        editNameDialogFragment.show(fm, "time select");
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
