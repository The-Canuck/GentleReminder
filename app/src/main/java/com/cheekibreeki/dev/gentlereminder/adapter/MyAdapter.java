package com.cheekibreeki.dev.gentlereminder.adapter;

import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cheekibreeki.dev.gentlereminder.database.DBHelper;
import com.cheekibreeki.dev.gentlereminder.dialogs.EditReminder;
import com.cheekibreeki.dev.gentlereminder.R;
import com.cheekibreeki.dev.gentlereminder.Reminder;
import com.cheekibreeki.dev.gentlereminder.notifications.AlarmHelper;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Reminder> reminderList;
    private long mLastClickTime = 0;

    /**
     * Constructs the adapter
     *
     * @param reminderList the arraylist of reminder objects
     */
    public MyAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
    }

    // sets the onClickListener for the recyclerview item
    // and sets the buttons for the RV item and shows/hides the buttons
    private final View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View button1;
            View button2;

            button1 = v.findViewById(R.id.button1);
            button2 = v.findViewById(R.id.button2);

            updateButtonVisibility(button1, button2);
        }
    };

    /**
     * Provides the views for each value of the reminder object
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView note;
        public ImageButton button1;
        public ImageButton button2;

        /**
         * Constructs the view holder
         *
         * @param view the activity view
         */
        private ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            note = view.findViewById(R.id.note);
            button1 = view.findViewById(R.id.button1);
            button2 = view.findViewById(R.id.button2);
        }
    }

    /**
     * Hides/shows the buttons of a recyclerview item when clicked
     *
     * @param view1 view of the edit button for the RV item that was clicked
     * @param view2 view of the deleted button for the RV item that was clicked
     */
    private void updateButtonVisibility(View view1, View view2) {

        if (view1.getVisibility() == View.VISIBLE && view2.getVisibility() == View.VISIBLE) {
            view1.setAnimation(AnimationUtils.loadAnimation(view1.getContext(), R.anim.slide_right));
            view1.setVisibility(View.GONE);

            view2.setAnimation(AnimationUtils.loadAnimation(view2.getContext(), R.anim.slide_right));
            view2.setVisibility(View.GONE);
        } else {
            view1.setAnimation(AnimationUtils.loadAnimation(view1.getContext(), R.anim.slide_left));
            view1.setVisibility(View.VISIBLE);

            view2.setAnimation(AnimationUtils.loadAnimation(view2.getContext(), R.anim.slide_left));
            view2.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Fades the button for 0.2 seconds on click
     *
     * @param view view of the button that was clicked
     */
    private void fadeButtonClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_on_click));
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_list_row, parent, false);
        view.findViewById(R.id.button1).setVisibility(View.GONE);
        view.findViewById(R.id.button2).setVisibility(View.GONE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final View.OnClickListener editListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeButtonClick(v);
                editPrompt(holder, v);
            }
        };

        final View.OnClickListener deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                AlarmHelper alarmHelper = new AlarmHelper(v.getContext());
                DBHelper db = new DBHelper(v.getContext());
                fadeButtonClick(v);

                int pos = holder.getAdapterPosition();
                Reminder reminderToDelete = reminderList.get(pos);

                alarmHelper.setmReminder(reminderList.get(pos));
                alarmHelper.deleteNotification(alarmHelper.getNotification());

                Toast.makeText(v.getContext(), "Delete item at pos: " +
                        pos + " with id: " + reminderList.get(pos).getId(), Toast.LENGTH_SHORT).show();

                reminderList.remove(reminderToDelete);
                db.deleteReminder(reminderToDelete);
                notifyItemRemoved(holder.getAdapterPosition());

            }
        };

        final Reminder reminder = reminderList.get(position);
        holder.title.setText(reminder.getTitle());
        holder.note.setText(reminder.getNote());
        holder.itemView.setOnClickListener(listener);
        holder.button1.setOnClickListener(editListener);
        holder.button2.setOnClickListener(deleteListener);
    }

    /**
     * Calls the edit layout to appear when the edit button is clicked for the specific entry
     *
     * @param holder the viewholder for the recyclerview
     * @param v      the view of the item being edited
     */
    private void editPrompt(ViewHolder holder, final View v) {
        final int pos = holder.getAdapterPosition();
        EditReminder edit = new EditReminder(v.getContext(), reminderList.get(pos));
        edit.show();
        edit.setCanceledOnTouchOutside(false);
        edit.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                DBHelper db = new DBHelper(v.getContext());
                notifyItemChanged(pos);
                db.updateReminder(reminderList.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
