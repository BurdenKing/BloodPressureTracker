package ca.bcit.a3717assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            showUpdateDialog(item.getId(),
                    item.getUserId(),
                    item.getDateReading(),
                    item.getSystolicReading(),
                    item.getDiastolicReading(),
                    item.getSystolicReading(),
                    item.getCond());

            return false;

        });
    }

    private void addItem() {
        SimpleDateFormat dFormat = new SimpleDateFormat("dd/MMM/yyyy");

        String userId = editUID.getText().toString().trim();
        String date = tvDateReading.getText().toString().trim();
        String time = tvTimeReading.getText().toString().trim();
        String sysRead = editSystolicReading.getText().toString().trim();
        String diaRead = editDiastolicReading.getText().toString().trim();
        String condition = tvCondition.getText().toString().trim();


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

        String id = databaseBloodPressureTracker.push().getKey();
        FormItems item = new FormItems(id,userId, date, time, sysRead, diaRead);
        String conditionGen = item.getCond();
        Task setValueTask = databaseBloodPressureTracker.child(id).setValue(item);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this, "item added", Toast.LENGTH_LONG).show();

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

                ItemListAdapter adapter = new ItemListAdapter(MainActivity.this, itemList);
                lvItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void updateForm(String id,String uid, String d, String t, String sys, String dia) {
        DatabaseReference dbRef = databaseBloodPressureTracker.child(id);

        FormItems item = new FormItems(id, uid, d, t, sys,dia );

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
                Toast.makeText(MainActivity.this, "Form Deleted!",Toast.LENGTH_LONG).show();
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