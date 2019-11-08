package ca.bcit.a3717assignment2;

import java.time.LocalTime;
import java.util.Date;

public class FormItems {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getReadingDate() {
        return readingDate;
    }

    public void setReadingDate(Date readingDate) {
        this.readingDate = readingDate;
    }

    public LocalTime getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(LocalTime readingTime) {
        this.readingTime = readingTime;
    }

    public double getSystolicReading() {
        return SystolicReading;
    }

    public void setSystolicReading(double systolicReading) {
        SystolicReading = systolicReading;
    }

    public double getDiastolicReading() {
        return DiastolicReading;
    }

    public void setDiastolicReading(double diastolicReading) {
        DiastolicReading = diastolicReading;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    String userId;
    Date readingDate;
    LocalTime readingTime;
    double SystolicReading;
    double DiastolicReading;
    String condition;

    public FormItems(){}

    public FormItems(String uid, Date rd, LocalTime rt, double sr, double dr, String cond){
        this.userId = uid;
        this.readingDate = rd;
        this.readingTime = rt;
        this.SystolicReading = sr;
        this.DiastolicReading = dr;
        this.condition = cond;
    }


}
