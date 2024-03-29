package ca.bcit.a3717assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import java.text.DecimalFormat;

import ca.bcit.a3717assignment2.FormItems.Cond;


public class AverageReading extends AppCompatActivity {

    TextView tvDate;
    String date;
    Double systolicReading;
    Double diastolicReading;
    int totalReadings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_average_reading);

        date = (String) getIntent().getExtras().get("date");
        String user = (String) getIntent().getExtras().get("userID");

        systolicReading = (double) getIntent().getExtras().get("totalSystolicReading");
        diastolicReading = (double) getIntent().getExtras().get("totalDiastolicReading");
        totalReadings = (int) getIntent().getExtras().get("totalUserReadings");

        tvDate = findViewById(R.id.avgReading_date);
        tvDate.setText(String.valueOf(date));

        TextView userID = findViewById(R.id.avgReading_userID);
        userID.setText(String.valueOf(user));

        TextView tvSystolicAvg = findViewById(R.id.avgReading_averageSystolic);
        tvSystolicAvg.setText(String.valueOf(getAverageReading(systolicReading, totalReadings)));

        TextView tvDiastolicAvg = findViewById(R.id.avgReading_averageDiastolic);
        tvDiastolicAvg.setText(String.valueOf(getAverageReading(diastolicReading, totalReadings)));


        TextView tvAverageCondition = findViewById(R.id.avgReading_averageCondition);
        tvAverageCondition.setText(String.valueOf(generateCond()));

    }


    private double getAverageReading(double value, double total) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        double result = value / total;
        numberFormat.format(result);

        return Double.parseDouble(numberFormat.format(result));

    }

    public String generateCond() {
        String condition = "";

        double sr = getAverageReading(systolicReading, totalReadings);
        double dr = getAverageReading(diastolicReading, totalReadings);

        if (sr >= 180 || dr >= 120) {
            condition = Cond.CRISIS.label;
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#BD3B1B"));

        } else if ((sr >= 140 && sr < 180) || dr >= 90) {
            condition = Cond.STAGE2.label;
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#DBA800"));

        } else if ((sr >= 130 && sr <= 139) || (dr >= 80 && dr <= 89)) {
            condition = Cond.STAGE1.label;
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#B9D870"));

        } else if ((sr >= 120 && sr <= 129) && dr < 80) {
            condition = Cond.ELEVATED.label;
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#B6C61A"));

        } else if (sr < 120 && dr < 80) {
            condition = Cond.NORMAL.label;
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#006344"));

        }
        return condition;
    }
}
