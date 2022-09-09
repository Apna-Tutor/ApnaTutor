package com.debuggers.apnatutor.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.parceler.Parcel;

@Parcel
public class Quiz {
    private String _id;
    private String question;
    private List<String> options;
    private String answer;

    public Quiz() {
    }

    public Quiz(String question, List<String> options, String answer) {
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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return _id.equals(quiz._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}
