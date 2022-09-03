package com.debuggers.apna_tutor.Models;

import java.util.ArrayList;

public class Quiz {
    private String _id;
    private String question;
    private ArrayList<String> options;
    private String answer;

    public Quiz(String question, ArrayList<String> options, String answer) {
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
