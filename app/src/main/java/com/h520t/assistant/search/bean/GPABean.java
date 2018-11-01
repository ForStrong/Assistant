package com.h520t.assistant.search.bean;


public class GPABean {
    private String className;
    private String classNature;
    private String credit;
    private String gpa;
    private String grade;
    private String makeupGrade;
    private String retakenGrade;

    public GPABean(String className, String classNature, String credit, String gpa, String grade, String makeupGrade, String retakenGrade) {
        this.className = className;
        this.classNature = classNature;
        this.credit = credit;
        this.gpa = gpa;
        this.grade = grade;
        this.makeupGrade = makeupGrade;
        this.retakenGrade = retakenGrade;
    }

    @Override
    public String toString() {
        return "GPABean{" +
                "className='" + className + '\'' +
                ", classNature='" + classNature + '\'' +
                ", credit='" + credit + '\'' +
                ", gpa='" + gpa + '\'' +
                ", grade='" + grade + '\'' +
                ", makeupGrade='" + makeupGrade + '\'' +
                ", retakenGrade='" + retakenGrade + '\'' +
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

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
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

    public String getRetakenGrade() {
        return retakenGrade;
    }

    public void setRetakenGrade(String retakenGrade) {
        this.retakenGrade = retakenGrade;
    }
}
