package com.example.peter.gentlereminder;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.peter.gentlereminder.adapter.MyAdapter;
import com.example.peter.gentlereminder.database.DBHelper;
import com.example.peter.gentlereminder.dialogs.EditReminder;
import com.example.peter.gentlereminder.notifications.AlarmHelper;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    // instance vars
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Reminder> reminderList;
    DBHelper db = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reminderList = db.getAllReminders();

        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(reminderList);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration
                (this, LinearLayoutManager.VERTICAL));

        // Hides floating action button when scrolling
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                FloatingActionButton myFab = findViewById(R.id.myFab);

                if(dy == 0 && myFab.getVisibility() != View.VISIBLE)
                {
                    myFab.show();
                }
                else if(dy > 0 && myFab.getVisibility() == View.VISIBLE)
                {
                    myFab.hide();
                }
                else if(dy < 0 && myFab.getVisibility() != View.VISIBLE)
                {
                    myFab.show();
                }
            }
        });
        addReminder();
    }

    /**
     * Adds a Prompt for the user to enter information for new reminder object
     * then adds the reference to the object to the database and recyclerview
     */
    private void addReminder()
    {
        FloatingActionButton myFab = findViewById(R.id.myFab);
        myFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final Reminder newReminder = new Reminder();
                EditReminder editReminder = new EditReminder(MainActivity.this, newReminder);

                editReminder.show();
                editReminder.setCancelable(false);
                editReminder.setCanceledOnTouchOutside(false);

                editReminder.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        if(!newReminder.isDeleted())
                        {
                            reminderList.add(newReminder);
                            db.addReminder(newReminder);
                            mRecyclerView.getAdapter().
                                    notifyItemInserted(reminderList.indexOf(newReminder));
                            addNotification(newReminder);
                        }
                        else
                        {
                            Toast.makeText(v.getContext(), "Reminder canceled.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * Adds a notification based on the content of the reminder
     *
     * @param reminder  reference to the reminder
     */
    private void addNotification(Reminder reminder)
    {
        AlarmHelper alarmHelper = new AlarmHelper(this);
        alarmHelper.setmReminder(reminder);
        alarmHelper.scheduleNotification(alarmHelper.getNotification());
    }

    private void testData()
    {
        Reminder reminder;
        List<Integer> mList = new ArrayList<>();
        mList.add(1);
        mList.add(2);
        mList.add(3);
        mList.add(4);
        mList.add(5);
        mList.add(6);
        mList.add(7);
        for(int i = 0; i < 50; i++) {
            reminder = new Reminder();
            reminder.setTitle("Title: " + i);
            reminder.setNote("Note: " + i);
            reminder.setHour(5);
            reminder.setMinute(0);
            reminder.setDaysOfWeek(mList);
//            reminder.setId(i);
            reminderList.add(reminder);
            db.addReminder(reminder);
        }
        db.close();
    }
}
