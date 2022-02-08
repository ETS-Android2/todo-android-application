package com.jkcamarador.dothis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class TaskDetails extends AppCompatActivity {

    DBHelper DB;
    EditText taskName, note;
    Button help, deadline, back;
    TextView diffProgress;
    SeekBar difficulty;
    AlertDialog.Builder alertDialogBuilder;
    FloatingActionButton doneTask;
    static TextView deadlineText;
    static String deadlineDate ="";
    static String presentDate ="";
    String task_note = "";
    Boolean editModeCheck = false;
    int editTaskID, editTaskIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        DB = new DBHelper(this);
        init();
        getPresentDate();
        getDeadline();
        transferResult();
        seekBarProgress();
        helpBtn();
        back();

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                //Extra bundle is null
            } else{
                String method = extras.getString("methodName");
                if (method.equals("editMode")) {
                    editModeOn();
                }
            }
        }

    }

    public void init(){
        taskName = findViewById(R.id.etTaskName);
        note = findViewById(R.id.mlNotes);
        deadline = findViewById(R.id.btnDeadline);
        back = findViewById(R.id.btnBack);
        doneTask = findViewById(R.id.fabDone);
        diffProgress = findViewById(R.id.sbProgress);
        difficulty = findViewById(R.id.sbDifficulty);
        deadlineText = findViewById(R.id.tvDeadline);
        help = findViewById(R.id.btnHelp);
        alertDialogBuilder = new AlertDialog.Builder(this );
    }

    public void editModeOn(){
        editModeCheck = true;
        editTaskID = Integer.parseInt(getIntent().getStringExtra("task_id"));
        editTaskIcon = Integer.parseInt(getIntent().getStringExtra("task_icon"));

        taskName.setText(getIntent().getStringExtra("task_name"));
        deadlineDate = getIntent().getStringExtra("task_dl");
        deadlineText.setText("Deadline: " + getIntent().getStringExtra("task_dl"));

        if (getIntent().getStringExtra("task_note").equals("The user is rather lazy and has yet to leave a note.")){
            note.setText("");
        } else {
            note.setText(getIntent().getStringExtra("task_note"));
        }

        difficulty.setMax(5);
        difficulty.setProgress(Integer.parseInt(getIntent().getStringExtra("task_diff")));
    }

    public void transferResult(){
        final int random = new Random().nextInt(40 - 1) + 0;
        doneTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskName.getText().toString().equals("")){
                    showError("Enter Task Name!",view);
                }else if (taskName.getText().toString().trim().isEmpty()){
                    showError("Empty spaces are not allowed!",view);
                }else if (deadlineDate.equals("")){
                    showError("Pick a Deadline Date!", view);
                }else if (getDateDiff(deadlineDate) <= 0){
                    showError("Invalid Deadline Date!",view);
                }else if (diffProgress.getText().toString().equals("0")){
                    showError("Choose Difficulty!",view);
                }else{

                    String task_name = taskName.getText().toString().toUpperCase();
                    String task_dl = deadlineDate;
                    int task_diff = Integer.parseInt(diffProgress.getText().toString());

                    if (note.getText().toString().isEmpty()){
                        task_note = "The user is rather lazy and has yet to leave a note.";
                    } else {
                        task_note = note.getText().toString();
                    }

                    double task_weight = (Double)getDateDiff(deadlineDate)  / Double.parseDouble(diffProgress.getText().toString());
                    int task_icon = random;

                    if(!editModeCheck){
                        Boolean checkinsertdata = DB.insertTaskData(task_name, task_dl, task_diff, task_note, task_weight, task_icon);
                        if(checkinsertdata==true) {
                            Toast.makeText(TaskDetails.this, "Task Successfully Added.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(TaskDetails.this, "Task Failed to Add.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        DB.updateTaskData(editTaskID, task_name, task_dl, task_diff, task_note, task_weight, editTaskIcon);
                        Toast.makeText(TaskDetails.this, "Task Successfully Updated.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();

                        intent.putExtra("task_name", task_name);
                        intent.putExtra("task_dl", task_dl);
                        intent.putExtra("task_diff", String.valueOf(task_diff));
                        intent.putExtra("task_note", task_note);
                        intent.putExtra("task_weight", String.valueOf(task_weight));

                        setResult(RESULT_OK, intent);
                    }

                    finish();
                }

            }
        });
    }

    public void showError(String textError, View view){
        Snackbar snackbar = Snackbar.make(view, textError, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.errorRed));
        snackbar.setAction("OK",new closeSnackBar());
        snackbar.show();
    }

    public class closeSnackBar implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // no action
        }
    }

    public void getDeadline(){
        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    public String getPresentDate(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        presentDate = year + "-" + (month + 1) + "-" + day;

        return presentDate;
    }

    public double getDateDiff(String deadlineDate){
        double days = 0;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            long diff = sdf.parse(deadlineDate).getTime() - sdf.parse(getPresentDate()).getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            days = ((int) (long) hours / 24);
        }catch (Exception e){
            e.printStackTrace();
        }
        return days;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            deadlineDate = year + "-" + (month + 1) + "-" + day;
            deadlineText.setText("Deadline: " + deadlineDate);
        }
    }

    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void seekBarProgress(){
        difficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                diffProgress.setText(String.valueOf(i));
                if (i>=4){
                    diffProgress.setTextColor(Color.parseColor("#ef5350"));
                } else if (i==3){
                    diffProgress.setTextColor(Color.parseColor("#ffb74d"));
                } else if (i>0){
                    diffProgress.setTextColor(Color.parseColor("#80e27e"));
                } else {
                    diffProgress.setTextColor(Color.parseColor("#382c36"));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void helpBtn(){
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilder.setTitle("Difficulty?")
                        .setMessage("Set the Difficulty Level of the task: \n1 is the lowest difficulty.\n5 is the highest difficulty.")
                        .setCancelable(true);
                alertDialogBuilder.show();
            }
        });
    }
}