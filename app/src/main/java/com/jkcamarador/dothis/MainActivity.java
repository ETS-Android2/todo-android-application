package com.jkcamarador.dothis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DBHelper DB;
    private static final int newTaskEntry = 0;
    ArrayList<String> listOfTaskNames;
    ArrayList<String> listOfTaskID;
    ArrayList<String> listOfTaskIDExpired;
    ArrayList<Double> weights;
    ListView listTask;
    Button help;
    TextView totalTask;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog.Builder alertDialogBuilder1;
    FloatingActionButton addTask;
    ListAdapter listAdapter;
    Intent intent;
    String presentDate ="";
    double weight = 0;
    int[] taskIcons = {R.drawable.task_icon1, R.drawable.task_icon2, R.drawable.task_icon3, R.drawable.task_icon4, R.drawable.task_icon5, R.drawable.task_icon6, R.drawable.task_icon7, R.drawable.task_icon8, R.drawable.task_icon9, R.drawable.task_icon10,
            R.drawable.task_icon11, R.drawable.task_icon12, R.drawable.task_icon13, R.drawable.task_icon14, R.drawable.task_icon15, R.drawable.task_icon16, R.drawable.task_icon17, R.drawable.task_icon18, R.drawable.task_icon19, R.drawable.task_icon20,
            R.drawable.task_icon21, R.drawable.task_icon22, R.drawable.task_icon23, R.drawable.task_icon24, R.drawable.task_icon25, R.drawable.task_icon26, R.drawable.task_icon27, R.drawable.task_icon28, R.drawable.task_icon29, R.drawable.task_icon30,
            R.drawable.task_icon31, R.drawable.task_icon32, R.drawable.task_icon33, R.drawable.task_icon34, R.drawable.task_icon35, R.drawable.task_icon36, R.drawable.task_icon37, R.drawable.task_icon38, R.drawable.task_icon39, R.drawable.task_icon40,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB = new DBHelper(this);

        init();
        addDataToArrayList();
        addingTask();
        taskView();
        updateWeight();
        checkExpired();

    }

    public void init(){
        listTask = findViewById(R.id.listTask);
        addTask = findViewById(R.id.fabDone);
        help = findViewById(R.id.btnHelp3);
        totalTask = findViewById(R.id.tvTotalTask);
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder1 = new AlertDialog.Builder(this);
        listOfTaskNames = new ArrayList<String>();
        listOfTaskID = new ArrayList<String>();
        listOfTaskIDExpired = new ArrayList<String>();
        weights = new ArrayList<Double>();
    }

    public void addingTask(){
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(),TaskDetails.class);
                startActivity(intent);
            }
        });
    }

    public void addDataToArrayList(){
        listOfTaskNames.clear();
        listOfTaskID.clear();
        listOfTaskIDExpired.clear();
        ArrayList<TasksList> userArrayList = new ArrayList<>();

        Cursor res = DB.taskWeightSortingAlgorithm();

        if(res.getCount()==0){
            totalTask.setText("0");

            listAdapter = new ListAdapter(MainActivity.this,userArrayList);
            listTask.setAdapter(listAdapter);
            listTask.setClickable(true);
            totalTask.setText(String.valueOf(userArrayList.size()));
            listAdapter.notifyDataSetChanged();
            return;
        }
        while(res.moveToNext()){
            listOfTaskID.add(res.getString(0));
            listOfTaskNames.add(res.getString(1));

            TasksList tasksList = new TasksList(String.valueOf(res.getString(1)),"Deadline: " + String.valueOf(res.getString(2)),"Difficulty: " + String.valueOf(res.getString(3)), taskIcons[Integer.valueOf(res.getString(6))]);
            userArrayList.add(tasksList);
        }
        listAdapter = new ListAdapter(MainActivity.this,userArrayList);
        listTask.setAdapter(listAdapter);
        listTask.setClickable(true);
        totalTask.setText(String.valueOf(userArrayList.size()));
        listAdapter.notifyDataSetChanged();
    }

    public void taskView(){
        listTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(getApplicationContext(),TaskView.class);

                Cursor res = DB.searchTaskData(Integer.parseInt(listOfTaskID.get(i)));

                while(res.moveToNext()){
                    intent.putExtra("task_id",String.valueOf(res.getString(0)));
                    intent.putExtra("task_name",String.valueOf(res.getString(1)));
                    intent.putExtra("task_dl",String.valueOf(res.getString(2)));
                    intent.putExtra("task_diff",String.valueOf(res.getString(3)));
                    intent.putExtra("task_note",String.valueOf(res.getString(4)));
                    intent.putExtra("task_weight",String.valueOf(res.getString(5)));
                    intent.putExtra("task_icon",String.valueOf(res.getString(6)));

                }
                startActivityForResult(intent, newTaskEntry);
            }
        });

        listTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String taskNameRetrieved = String.valueOf(listOfTaskNames.get(position));
                alertDialogBuilder.setTitle("Are you sure?")
                        .setMessage("Do you really want to delete " + taskNameRetrieved +" Task? " +
                                "This process cannot be undone.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DB.deleteTaskData(Integer.parseInt(listOfTaskID.get(position)));
                                listAdapter.notifyDataSetChanged();
                                deleteToast(taskNameRetrieved + " Task Deleted",view);
                                addDataToArrayList();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setCancelable(false);
                alertDialogBuilder.show();
                return true;
            }
        });
    }

    public void updateWeight(){
        Cursor res = DB.getTaskData();
        if(res.getCount()==0){
            return;
        }
        while(res.moveToNext()){
            weight = getDateDiff(String.valueOf(res.getString(2))) / Double.parseDouble(res.getString(3));
            if (weight<=0){
                weight=0;
            }

            DB.updateWeightTaskData(Integer.parseInt(res.getString(0)), String.valueOf(res.getString(1)),String.valueOf(res.getString(2)),Integer.parseInt(String.valueOf(res.getString(3))),String.valueOf(res.getString(4)),weight,Integer.parseInt(String.valueOf(res.getString(6))));

            if (weight <= 0){
                listOfTaskIDExpired.add(res.getString(0));
            }
        }
    }

    public void checkExpired(){
        int noOfTask = listOfTaskIDExpired.size();
        if (noOfTask > 0){
            alertDialogBuilder.setTitle("Warning!")
                    .setMessage("Some task deadlines have been met!. Do you want to delete them? This process cannot be undone.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int ctr = 0; ctr < noOfTask; ctr++){
                                Cursor res = DB.getTaskData();

                                DB.checkExpiredTask(Integer.parseInt(listOfTaskIDExpired.get(ctr)));
                            }
                            listAdapter.notifyDataSetChanged();
                            addDataToArrayList();
                            Toast.makeText(getApplicationContext(),noOfTask + " Task/s Deleted",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setCancelable(false);
            alertDialogBuilder.show();
        }
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

    public void deleteToast(String text, View view){
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.green));
        snackbar.setAction("OK",new closeSnackBar());
        snackbar.show();
    }

    public class closeSnackBar implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // no action
        }
    }

    public void deleteAllTask(){
        alertDialogBuilder.setTitle("Are you sure?")
                .setMessage("Do you really want to delete all of the Task? " +
                        "This process cannot be undone.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DB.deleteAllTask();

                        listAdapter.notifyDataSetChanged();
                        addDataToArrayList();
                        Toast.makeText(getApplicationContext(),"All Task Deleted!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(false);
        alertDialogBuilder.show();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.task_menu);
        popup.show();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainHelp:
                alertDialogBuilder1.setTitle("How to Delete Task?")
                        .setMessage("Just press and hold the selected Task.\nWarning, deleting Task cannot be undone.")
                        .setCancelable(true);
                alertDialogBuilder1.show();
                return true;
            case R.id.mainDeleteAll:
                deleteAllTask();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }
}