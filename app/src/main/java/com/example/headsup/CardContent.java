package com.example.headsup;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class CardContent {

    String cardText;
    Drawable image;
    String score60;
    String score90;
    String score120;
    ArrayList<String> words;

    public CardContent(String cardText, Drawable image, String cardScore60, String cardScore90,
                       String cardScoe120, ArrayList<String> words)
    {
        this.cardText = cardText;
        this.image = image;
        this.score60 = cardScore60;
        this.score90 = cardScore90;
        this.score120 = cardScoe120;
        this.words = words;
    }

    public String getCardText() {
        return cardText;
    }

    public void setCardText(String cardText) {
        this.cardText = cardText;
    }

    public Drawable getCardImage() {
        return image;
    }

    public void setCardImage(Drawable image) {
        this.image = image;
    }

    public String getScore60() {
        return score60;
    }

    public void setScore60(String score60) {
        this.score60 = score60;
    }

    public String getScore90() {
        return score90;
    }

    public void setScore90(String score90) {
        this.score90 = score90;
    }

    public String getScore120() {
        return score120;
    }

    public void setScore120(String score120) {
        this.score120 = score120;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }
}
