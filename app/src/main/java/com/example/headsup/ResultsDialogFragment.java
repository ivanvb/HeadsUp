package com.example.headsup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultsDialogFragment extends DialogFragment implements View.OnClickListener{

    private static ArrayList<String> fragCorrectWords, fragIncorrectWords;
    boolean playAgainClicked;
    public ResultsDialogFragment()
    {

    }

    public static ResultsDialogFragment newInstance(ArrayList<String> correct, ArrayList<String> incorrect) {


        fragCorrectWords = correct;
        fragIncorrectWords = incorrect;

        Bundle args = new Bundle();

        ResultsDialogFragment fragment = new ResultsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.round_fragment);

        playAgainClicked = false;
        return inflater.inflate(R.layout.results_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.MyCustomTheme;

        Button btn = view.findViewById(R.id.btnPlayAgain);
        btn.setOnClickListener(this);

        setScore();
        publishResults();

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnPlayAgain)
        {
            playAgainClicked = true;
            ((GameActivity)getActivity()).restart();
        }
    }

    private void publishResults()
    {
        TextView tvCorrect, tvIncorrect;
        tvCorrect = getView().findViewById(R.id.tvCorrectWords);
        tvIncorrect = getView().findViewById(R.id.tvIncorrectWords);

        String correct = "", incorrect = "";

        for(int i = 0; i < fragCorrectWords.size(); i++)
        {
            correct += fragCorrectWords.get(i);
            correct += "\n\n";
        }

        for(int i = 0; i < fragIncorrectWords.size(); i++)
        {
            incorrect += fragIncorrectWords.get(i);
            incorrect += "\n\n";
        }

        tvCorrect.setText(correct);
        tvIncorrect.setText(incorrect);
    }

    private void setScore()
    {
        TextView scoreTv = getView().findViewById(R.id.score);
        scoreTv.setText("Score: " + fragCorrectWords.size());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(!playAgainClicked)
        {
            ((GameActivity)getActivity()).finish();
        }

    }
}
