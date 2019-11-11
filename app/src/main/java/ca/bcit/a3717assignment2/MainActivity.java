package ca.bcit.a3717assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Color;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.bcit.a3717assignment2.FormItems.Cond;


public class MainActivity extends AppCompatActivity {

    EditText editUID;
    TextView tvDateReading;
    TextView tvTimeReading;
    EditText editSystolicReading;
    EditText editDiastolicReading;
    TextView tvCondition;
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
        tvDateReading = findViewById(R.id.date);
        tvTimeReading = findViewById(R.id.time);
        editSystolicReading = findViewById(R.id.editSysReading);
        editDiastolicReading = findViewById(R.id.editDiaReading);
        tvCondition = findViewById(R.id.condition);
        addRecord = findViewById(R.id.buttonAddRecord);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
        String currentTime = sdf.format(new Date());
        tvTimeReading.setText(currentTime);

        String date = new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault()).format(new Date());
        tvDateReading.setText(date);

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
            showUpdateDialog(
                    item.getId(),
                    item.getUserId(),
                    item.getDateReading(),
                    item.getSystolicReading(),
                    item.getDiastolicReading(),
                    item.getSystolicReading(),
                    item.getCond());

            return true;
        });

        //test
        lvItems.setOnItemClickListener((parent, view, position, id) -> {
            FormItems item = itemList.get(position);

            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), AverageReading.class);

            String currentMonth = getMonth(item.getDateReading());

            intent.putExtra("date", currentMonth);
            intent.putExtra("userID", item.getUserId());

            intent.putExtra("totalSystolicReading", getTotalSystolicReading(item.getUserId(), currentMonth));
            intent.putExtra("totalDiastolicReading", getTotalDiastolicReading(item.getUserId(), currentMonth));
            intent.putExtra("totalUserReadings", getTotalUserReadings(item.getUserId(), currentMonth));

            startActivity(intent);

        });
    }

    private String getMonth(String date) {


        try {
            String[] dateParts = date.split("-");
            String month = dateParts[1];

            return month;

        } catch (Exception e) {

        }

        return null;
    }

    private int getTotalUserReadings(String userID, String month) {
        String currentDate = new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault()).format(new Date());
        String currentMonth = getMonth(currentDate);
        int total = 0;

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getUserId().equals(userID)) {

                if(month.equals(getMonth(itemList.get(i).getDateReading()))) {
                    total += 1;
                }
            }
        }

        return total;
    }

    private double getTotalSystolicReading(String userID, String month) {
        double total = 0.0;

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getUserId().equals(userID)) {

                if(month.equals(getMonth(itemList.get(i).getDateReading()))) {
                    total += Double.parseDouble(itemList.get(i).getSystolicReading());
                }
            }
        }
        return total;
    }

    private double getTotalDiastolicReading(String userID, String month) {
        double total = 0.0;


        for (int i = 0; i < itemList.size(); i++) {

            if (itemList.get(i).getUserId().equals(userID)) {
                if(month.equals(getMonth(itemList.get(i).getDateReading()))) {
                    total += Double.parseDouble(itemList.get(i).getDiastolicReading());
                }
            }
        }
        return total;
    }


    private void addItem() {
        SimpleDateFormat dFormat = new SimpleDateFormat("dd/MMM/yyyy");

        String userId = editUID.getText().toString().trim();
        String date = tvDateReading.getText().toString().trim();
        String time = tvTimeReading.getText().toString().trim();
        String sysRead = editSystolicReading.getText().toString().trim();
        String diaRead = editDiastolicReading.getText().toString().trim();

        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "You must enter a user ID.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(sysRead)) {
            Toast.makeText(this, "You must enter a systolic reading.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "You must enter a diastolic reading.", Toast.LENGTH_LONG).show();
            return;
        }

        if(Double.parseDouble(sysRead) >= 180 || Double.parseDouble(diaRead) >= 120) {
            Toast.makeText(this, "WARNING YOU ARE FAT", Toast.LENGTH_LONG).show();
        }


        String id = databaseBloodPressureTracker.push().getKey();
        FormItems item = new FormItems(id, userId, date, time, sysRead, diaRead);
        Task setValueTask = databaseBloodPressureTracker.child(id).setValue(item);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                if(Double.parseDouble(sysRead) >= 180 || Double.parseDouble(diaRead) >= 120) {
//                    Toast.makeText(MainActivity.this, "WARNING YOU ARE FAT", Toast.LENGTH_LONG).show();
                    Toast toast = new Toast (getApplicationContext());
                    toast.setGravity(Gravity.CENTER,0,0);

                    TextView ttv = new TextView(MainActivity.this);
                    ttv.setBackgroundColor(Color.BLACK);
                    ttv.setTextColor(Color.RED);
                    ttv.setTextSize(25);

                    Typeface t = Typeface.create("monospace", Typeface.BOLD);
                    ttv.setTypeface(t);
                    ttv.setPadding(10,10,10,10);
                    ttv.setText("Consult Your Doctor Immediately");
                    toast.setView(ttv);
                    toast.show();

                } else {
                    Toast.makeText(MainActivity.this, "Form added", Toast.LENGTH_LONG).show();

                }

                editUID.setText("");
                tvDateReading.setText("");
                tvTimeReading.setText("");
                editDiastolicReading.setText("");
                editSystolicReading.setText("");

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
                String currentTime = sdf.format(new Date());
                tvTimeReading.setText(currentTime);

                String date = new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault()).format(new Date());
                tvDateReading.setText(date);
            }

        });
        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
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

                lvItems.setAdapter(new ItemListAdapter(MainActivity.this, itemList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View row = super.getView(position, convertView, parent);
                        String condition = getItem(position).getCondition();

                        if (condition.equals(Cond.CRISIS.label)) {
                            row.setBackgroundColor(Color.parseColor("#BD3B1B"));

                        } else if (condition.equals(Cond.STAGE2.label)) {
                            row.setBackgroundColor(Color.parseColor("#DBA800"));

                        } else if (condition.equals(Cond.STAGE1.label)) {
                            row.setBackgroundColor(Color.parseColor("#B9D870"));

                        } else if (condition.equals(Cond.ELEVATED.label)) {
                            row.setBackgroundColor(Color.parseColor("#B6C61A"));

                        } else if (condition.equals(Cond.NORMAL.label)) {
                            row.setBackgroundColor(Color.parseColor("#006344"));

                        } else {
                            row.setBackgroundColor(Color.WHITE);
                        }

                        return row;
                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void updateForm(String id, String uid, String d, String t, String sys, String dia) {
        DatabaseReference dbRef = databaseBloodPressureTracker.child(id);

        FormItems item = new FormItems(id, uid, d, t, sys, dia);

        Task setValueTask = dbRef.setValue(item);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Form Updated.", Toast.LENGTH_LONG).show();
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

    private void showUpdateDialog(final String id, String uid, String dateR, String timeR, String sr, String dr, String cond) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText tvUserID = dialogView.findViewById(R.id.editUID);
        tvUserID.setText(uid);

        final TextView tvReadingDate = dialogView.findViewById(R.id.date);
        String date = new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault()).format(new Date());
        tvReadingDate.setText(date);

        final TextView tvReadingTime = dialogView.findViewById(R.id.time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
        String currentTime = sdf.format(new Date());
        tvReadingTime.setText(currentTime);


        final EditText editsReading = dialogView.findViewById(R.id.editSysReading);
        editsReading.setText(sr);

        final EditText editdReading = dialogView.findViewById(R.id.editDiaReading);
        editdReading.setText(dr);

        final TextView editCondition = dialogView.findViewById(R.id.condition);
        editCondition.setText(cond);

        final Button btnUpdate = dialogView.findViewById(R.id.buttonUpdate);

        dialogBuilder.setTitle("Updated Form For User: " + uid);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = tvUserID.getText().toString().trim();
                String readingDate = tvReadingDate.getText().toString().trim();
                String readingTime = tvReadingTime.getText().toString().trim();
                String sReading = editsReading.getText().toString().trim();
                String dReading = editdReading.getText().toString().trim();
                /* String condition = editCondition.getText().toString();*/

                if (TextUtils.isEmpty(sReading)) {
                    editsReading.setError("Must enter a Systolic Reading!");
                    return;
                } else if (TextUtils.isEmpty(dReading)) {
                    editdReading.setError("Must enter a Diastolic Reading!");
                    return;
                } else if (TextUtils.isEmpty(userID)) {
                    editdReading.setError("Must enter a user ID!");
                    return;
                }

                updateForm(id, userID, readingDate, readingTime, sReading, dReading);

                alertDialog.dismiss();
            }
        });

        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(id);

                alertDialog.dismiss();
            }
        });
    }

    private void deleteItem(String id) {
        DatabaseReference dbRef = databaseBloodPressureTracker.child(id);

        Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this, "Form Deleted!", Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong!" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}