
package com.example.toeic.vocabulary;

import java.io.Serializable;

public class EnglishWord implements Serializable {

    private int id;
    private String word;
    private String note;
    private String type;
    private String score;
    private String subject;
    private String check;
    private String wrong_number;
    private int word_number;
    private int activity;
    private String is_favourite;

    public String getIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(String is_favourite) {
        this.is_favourite = is_favourite;
    }

    public int getActivity() {
        return activity;
    }
    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getWord_number() {
        return word_number;
    }

    public void setWord_number(int word_number) {
        this.word_number = word_number;
    }



    public String getWrong_number() {
        return wrong_number;
    }

    public void setWrong_number(String wrong_number) {
        this.wrong_number = wrong_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }
}
