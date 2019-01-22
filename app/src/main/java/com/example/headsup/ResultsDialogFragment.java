package com.example.headsup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultsDialogFragment extends DialogFragment implements View.OnClickListener{

    private static ArrayList<String> fragCorrectWords, fragIncorrectWords;
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

        return inflater.inflate(R.layout.results_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        publishResults();

    }

    @Override
    public void onClick(View v) {

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
}
