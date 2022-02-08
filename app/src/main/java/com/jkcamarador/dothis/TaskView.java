package com.jkcamarador.dothis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class TaskView extends AppCompatActivity {

    DBHelper DB;
    private static final int editTaskEntry = 0;
    TextView taskName, taskDiff, taskDeadline, taskNote, taskWeight;
    Button back, copyNote, editTask;
    AlertDialog.Builder alertDialogBuilder;
    float value;
    String formatValue, taskDiff1 = "", taskDL;
    double taskWeight1;
    final Random rnd = new Random();
    int taskID, taskIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        DB = new DBHelper(this);
        init();
        result();
        back();
        setImage();
        copyNote();
        editTask();
    }

    public void init(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        taskName = findViewById(R.id.tvTaskName);
        taskDiff = findViewById(R.id.tvTaskDifficulty);
        taskDeadline = findViewById(R.id.tvTaskDeadline);
        taskNote = findViewById(R.id.tvTaskNote);
        taskWeight = findViewById(R.id.tvTaskWeight);
        back = findViewById(R.id.btnBack2);
        copyNote = findViewById(R.id.btnCopy);
        editTask = findViewById(R.id.btnEdit);
        value = Float.valueOf(getIntent().getStringExtra("task_weight"));
        formatValue = String.format("%.02f", value);
    }

    private void result() {
        taskIcon = Integer.parseInt(getIntent().getStringExtra("task_icon"));
        taskID = Integer.parseInt(getIntent().getStringExtra("task_id"));
        taskDL = getIntent().getStringExtra("task_dl");
        taskDiff1 = getIntent().getStringExtra("task_diff");
        taskWeight1 = Double.parseDouble(formatValue);

        //print results
        taskName.setText(getIntent().getStringExtra("task_name"));
        taskDiff.setText("Difficulty: " + taskDiff1);
        taskDeadline.setText(taskDL);
        taskNote.setText(getIntent().getStringExtra("task_note"));
        taskWeight.setText("Weight: " + formatValue);
        taskNote.setMovementMethod(new ScrollingMovementMethod());
    }

    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void copyNote(){
        copyNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("task_note", taskNote.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Notes copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setImage(){
        final ImageView img = (ImageView) findViewById(R.id.imageVector);
        final String str = "vector_" + rnd.nextInt(6);
        img.setImageDrawable(getResources().getDrawable(getResourceID(str, "drawable", getApplicationContext())));
    }

    protected final static int getResourceID(final String resName, final String resType, final Context ctx) {
        final int ResourceID = ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {
            throw new IllegalArgumentException("No resource string found with name " + resName);
        }
        else {
            return ResourceID;
        }
    }

    public void editTask(){
        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),TaskDetails.class);
                intent.putExtra("methodName","editMode");

                intent.putExtra("task_id",String.valueOf(taskID));
                intent.putExtra("task_name",taskName.getText().toString());
                intent.putExtra("task_dl",String.valueOf(taskDL));
                intent.putExtra("task_diff",String.valueOf(taskDiff1));
                intent.putExtra("task_note",taskNote.getText().toString());
                intent.putExtra("task_weight",String.valueOf(taskWeight1));
                intent.putExtra("task_icon",String.valueOf(taskIcon));

                startActivityForResult(intent, editTaskEntry);
            }
        });
    }

    public void showError(String textError, View view){
        Snackbar snackbar = Snackbar.make(view, textError, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.errorRed));
        snackbar.setAction("OK",new TaskView.closeSnackBar());
        snackbar.show();
    }

    public class closeSnackBar implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // no action
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == editTaskEntry) {
            if(resultCode == RESULT_OK) {
                taskName.setText(data.getStringExtra("task_name"));
                taskDiff.setText("Difficulty: " + data.getStringExtra("task_diff"));
                taskDeadline.setText(data.getStringExtra("task_dl"));
                taskNote.setText(data.getStringExtra("task_note"));

                value = Float.valueOf(data.getStringExtra("task_weight"));
                formatValue = String.format("%.02f", value);
                taskWeight.setText("Weight: " + formatValue);
            }
        }
    }


}