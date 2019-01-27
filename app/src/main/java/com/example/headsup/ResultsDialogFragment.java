package com.example.headsup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class ResultsDialogFragment extends DialogFragment implements View.OnClickListener{

    private static ArrayList<String> fragCorrectWords, fragIncorrectWords;
    boolean playAgainClicked;
    public ResultsDialogFragment() { }

    public static ResultsDialogFragment newInstance(ArrayList<String> correctWords,
                                                    ArrayList<String> incorrectWords) {
        fragCorrectWords = correctWords;
        fragIncorrectWords = incorrectWords;

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

    private void setScore()
    {
        TextView scoreTv = getView().findViewById(R.id.score);
        scoreTv.setText(getResources().getString(R.string.score_text)+ fragCorrectWords.size());
    }

    private void publishResults()
    {
        TextView tvCorrect, tvIncorrect;
        tvCorrect = getView().findViewById(R.id.tvCorrectWords);
        tvIncorrect = getView().findViewById(R.id.tvIncorrectWords);

        StringBuilder correct = new StringBuilder();
        StringBuilder incorrect = new StringBuilder();

        for(int i = 0; i < fragCorrectWords.size(); i++)
        {
            correct.append(fragCorrectWords.get(i));
            correct.append("\n\n");
        }

        for(int i = 0; i < fragIncorrectWords.size(); i++)
        {
            incorrect.append(fragIncorrectWords.get(i));
            incorrect.append("\n\n");
        }

        tvCorrect.setText(correct.toString());
        tvIncorrect.setText(incorrect.toString());
    }

    private void saveScores()
    {
        GameScoresManager.processScore(fragCorrectWords.size());
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnPlayAgain)
        {
            playAgainClicked = true;
            ((GameActivity)getActivity()).restart();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(!playAgainClicked)
        {
            ((GameActivity)getActivity()).finish();
        }
        saveScores();

    }


}
