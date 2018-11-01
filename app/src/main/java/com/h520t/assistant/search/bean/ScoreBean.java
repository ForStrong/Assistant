package com.h520t.assistant.search.bean;

import java.io.Serializable;

public class ScoreBean implements Serializable{
    private String className;
    private String classNature;
    private String credit;
    private String midTermGrade;
    private String finalGrade;
    private String experimentalGrade;
    private String grade;
    private String makeupGrade;

    public ScoreBean(String className, String classNature, String credit, String midTermGrade
            , String finalGrade, String experimentalGrade, String grade, String makeupGrade) {
        this.className = className;
        this.classNature = classNature;
        this.credit = credit;
        this.midTermGrade = midTermGrade;
        this.finalGrade = finalGrade;
        this.experimentalGrade = experimentalGrade;
        this.grade = grade;
        this.makeupGrade = makeupGrade;
    }

    @Override
    public String toString() {
        return "ScoreBean{" +
                "className='" + className + '\'' +
                ", classNature='" + classNature + '\'' +
                ", credit='" + credit + '\'' +
                ", midTermGrade='" + midTermGrade + '\'' +
                ", finalGrade='" + finalGrade + '\'' +
                ", experimentalGrade='" + experimentalGrade + '\'' +
                ", grade='" + grade + '\'' +
                ", makeupGrade='" + makeupGrade + '\'' +
                '}';
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassNature() {
        return classNature;
    }

    public void setClassNature(String classNature) {
        this.classNature = classNature;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getMidTermGrade() {
        return midTermGrade;
    }

    public void setMidTermGrade(String midTermGrade) {
        this.midTermGrade = midTermGrade;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }

    public String getExperimentalGrade() {
        return experimentalGrade;
    }

    public void setExperimentalGrade(String experimentalGrade) {
        this.experimentalGrade = experimentalGrade;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMakeupGrade() {
        return makeupGrade;
    }

    public void setMakeupGrade(String makeupGrade) {
        this.makeupGrade = makeupGrade;
    }
}
