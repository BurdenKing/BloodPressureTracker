package ca.bcit.a3717assignment2;

import java.util.Date;

public class FormItems {

    private String id;
    private String userId;
    private String dateReading;
    private String timeReading;
    private String systolicReading;
    private String diastolicReading;
    private String condition;

    public String getDiastolicReading() {

        return diastolicReading;
    }

    public String getSystolicReading() {

        return systolicReading;
    }

    public void setSystolicReading(String systolicReading) {
        this.systolicReading = systolicReading;
    }

    public void setDiastolicReading(String diastolicReading) {
        this.diastolicReading = diastolicReading;
    }

    public String getCondition() {

        return condition;
    }

    public void setCondition(String condition) {

        this.condition = condition;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getTimeReading() {

        return timeReading;
    }

    public void setTimeReading(String timeReading) {

        this.timeReading = timeReading;
    }

    public String getDateReading() {

        return dateReading;
    }

    public void setDateReading(String dateReading) {

        this.dateReading = dateReading;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }


    public String getCond() {

        return condition;
    }

    public void setCond(String condition) {

        this.condition = condition;
    }


    enum Cond {
        NORMAL("Normal"),
        ELEVATED("Elevated"),
        STAGE1("High Blood Pressure! (stage 1)"),
        STAGE2("High Blood Pressure! (stage 2)"),
        CRISIS("Hypertensive Crisis!");

        public final String label;

        Cond(String label) {
            this.label = label;
        }
    }

    public void generateCond() {
        double sr = Double.parseDouble(systolicReading);
        double dr = Double.parseDouble(diastolicReading);
        if (sr >= 180 || dr >= 120) {
            this.condition = Cond.CRISIS.label;
        } else if ((sr >= 140 && sr < 180) || dr >= 90) {
            this.condition = Cond.STAGE2.label;
        } else if ((sr >= 130 && sr <= 139) || (dr >= 80 && dr <= 89)) {
            this.condition = Cond.STAGE1.label;
        } else if ((sr >= 120 && sr <= 129) && dr < 80) {
            this.condition = Cond.ELEVATED.label;
        } else if (sr < 120 && dr < 80) {
            this.condition = Cond.NORMAL.label;
        }
    }


    public FormItems() {
    }

    public FormItems(String id, String uid, String date, String time, String sr, String dr, String cond) {
        this.id = id;
        this.userId = uid;
        this.dateReading = date;
        this.timeReading = time;
        this.systolicReading = sr;
        this.diastolicReading = dr;
        this.condition = cond;
    }

    public FormItems(String id, String uid, String date, String time, String sr, String dr) {
        this.id = id;
        this.userId = uid;
        this.dateReading = date;
        this.timeReading = time;
        this.systolicReading = sr;
        this.diastolicReading = dr;
        generateCond();
    }

}
