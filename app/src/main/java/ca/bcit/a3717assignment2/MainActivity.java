package ca.bcit.a3717assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editUID;
    DatePicker editReadingDate;
    TimePicker editReadingTime;
    EditText editSystolicReading;
    EditText editDiastolicReading;
    //    EditText editCondition;
    Button addRecord;

    ListView lvItems;
    List<FormItems> itemList;

    DatabaseReference databaseBloodPressureTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseBloodPressureTracker = FirebaseDatabase.getInstance().getReference("Records");

        editUID = findViewById(R.id.editUID);
        editReadingDate = findViewById(R.id.datePicker1);
        editReadingTime = findViewById(R.id.timePicker1);
        editReadingTime.setIs24HourView(true);
        editSystolicReading = findViewById(R.id.editSysReading);
        editDiastolicReading = findViewById(R.id.editDiaReading);
        addRecord = findViewById(R.id.buttonAddRecord);

        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        lvItems = findViewById(R.id.lvItems);
        itemList = new ArrayList<>();

        lvItems.setOnItemLongClickListener((parent, view, position, id) -> {
            FormItems item = itemList.get(position);
            showUpdateDialog(item.getUserId(),
                    item.getDiastolicReading(),
                    item.getSystolicReading());

            return false;

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseBloodPressureTracker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot formSnap : dataSnapshot.getChildren()) {
                    FormItems item = formSnap.getValue(FormItems.class);
                    itemList.add(item);
                }

                ItemListAdapter adapter = new ItemListAdapter(MainActivity.this, itemList);
                lvItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    private LocalTime getTimeFromTimePicker(TimePicker timePicker) {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        Calendar cal = Calendar.getInstance();
        return null;
    }


    private void addItem() {
        String userId = editUID.getText().toString().trim();
        Date date = getDateFromDatePicker(this.editReadingDate);
        LocalTime time = getTimeFromTimePicker(this.editReadingTime);
        double sysRead = Double.parseDouble(editSystolicReading.getText().toString());
        double diaRead = Double.parseDouble(editDiastolicReading.getText().toString());
        String condition = null;


        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "You must enter a user ID.", Toast.LENGTH_LONG).show();
            return;
        }
        if (time == null) {
            Toast.makeText(this, "You must select a time.", Toast.LENGTH_LONG).show();
            return;
        }
        if (date == null) {
            Toast.makeText(this, "You must select a date.", Toast.LENGTH_LONG).show();
            return;
        }

        String id = databaseBloodPressureTracker.push().getKey();
        FormItems item = new FormItems(userId, date, time, sysRead, diaRead, condition);
        Task setValueTask = databaseBloodPressureTracker.child(id).setValue(item);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this, "item added", Toast.LENGTH_LONG).show();

                editUID.setText("");
                editDiastolicReading.setText("");
                editSystolicReading.setText("");
                editReadingDate.updateDate(Calendar.getInstance().YEAR,
                        Calendar.getInstance().MONTH,
                        Calendar.getInstance().DAY_OF_MONTH);
                editReadingTime.setHour(Calendar.getInstance().HOUR);
                editReadingTime.setMinute(Calendar.getInstance().MINUTE);
            }
        });
        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });

    }
}

    private void updateForm(String uid, Date d, DateAndTime due, boolean done) {
        DatabaseReference dbRef = databaseBloodPressureTracker.child(id);

        FormItems item = new FormItems(editUID,);

        Task setValueTask = dbRef.setValue(student);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Student Updated.",Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }