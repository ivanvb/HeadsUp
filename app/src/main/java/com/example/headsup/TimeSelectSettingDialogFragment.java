package com.example.headsup;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TimeSelectSettingDialogFragment extends DialogFragment implements View.OnClickListener{

    ArrayList<String>timestamps;
    ArrayList<ImageView> checks;
    private static String currentTimespamp;
    private IFragmentListener listener;
    short timeSelection;
    public TimeSelectSettingDialogFragment()
    {

    }

    public static TimeSelectSettingDialogFragment newInstance(String selectedTime) {

        Bundle args = new Bundle();

        currentTimespamp = selectedTime;
        TimeSelectSettingDialogFragment fragment = new TimeSelectSettingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.round_fragment);

        return inflater.inflate(R.layout.settings_time_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        timestamps = new ArrayList<>(Arrays.asList("20", "60", "90", "120"));
        timeSelection = getTimestampIndex();
        registerImageViews();
        updateSelection();

        RelativeLayout firstRow = view.findViewById(R.id.firstTimestamp);
        firstRow.setOnClickListener(this);
        RelativeLayout secondRow = view.findViewById(R.id.secondTimestamp);
        secondRow.setOnClickListener(this);
        RelativeLayout thirdRow = view.findViewById(R.id.thirdTimestamp);
        thirdRow.setOnClickListener(this);
        RelativeLayout fourthRow = view.findViewById(R.id.fourthTimestamp);
        fourthRow.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.firstTimestamp)
        {
            timeSelection = 0;
            updateSelection();
            currentTimespamp = "20";
        }else if(v.getId() == R.id.secondTimestamp)
        {
            timeSelection = 1;
            updateSelection();
            currentTimespamp = "60";
        }else if(v.getId() == R.id.thirdTimestamp)
        {
            timeSelection = 2;
            updateSelection();
            currentTimespamp = "90";
        }else if(v.getId() == R.id.fourthTimestamp)
        {
            timeSelection = 3;
            updateSelection();
            currentTimespamp = "120";
        }
    }

    private short getTimestampIndex()
    {
        return (short)timestamps.indexOf(currentTimespamp);
    }

    private void registerImageViews()
    {
        ImageView[] arr =
                {
                        getView().findViewById(R.id.firstCheck), getView().findViewById(R.id.secondCheck),
                        getView().findViewById(R.id.thirdCheck), getView().findViewById(R.id.fourthCheck)
                };
        checks = new ArrayList<ImageView>(Arrays.asList(arr));
    }

    private void updateSelection()
    {
        int size = checks.size();
        for(int i = 0; i < size; i++)
        {
            if(i == timeSelection)
            {
                checks.get(i).setAlpha(1f);
            }
            else
            {
                checks.get(i).setAlpha(0f);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        listener.onInputSent(currentTimespamp);
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
