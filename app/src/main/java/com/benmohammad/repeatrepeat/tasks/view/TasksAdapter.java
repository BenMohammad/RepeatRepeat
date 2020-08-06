package com.benmohammad.repeatrepeat.tasks.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.benmohammad.repeatrepeat.R;
import com.benmohammad.repeatrepeat.tasks.view.TasksListViewData.TaskViewData;
import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;

public class TasksAdapter extends BaseAdapter {

    private ImmutableList<TaskViewData> tasks;
    private TaskItemListener taskItemListener;

    public void setTaskItemListener(TaskItemListener listener) {
        this.taskItemListener= listener;
    }

    public void replaceData(ImmutableList<TaskViewData> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return tasks == null ? 0 : tasks.size();
    }

    @Override
    public TaskViewData getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView =inflater.inflate(R.layout.task_item, parent, false);
        }
         final TaskViewData task = getItem(position);
        TextView titleTv = rowView.findViewById(R.id.title);
        titleTv.setText(task.title());

        CheckBox completeCB = rowView.findViewById(R.id.complete);
        completeCB.setChecked(task.completed());

        completeCB.setOnClickListener(
                __ -> {
                    if(taskItemListener == null) return;

                    if(!task.completed()) {
                        taskItemListener.onCompleteTaskClick(task.id());
                    } else {
                        taskItemListener.onActivateTaskClick(task.id());
                    }
                });

        rowView.setOnClickListener(__ -> {
            if(taskItemListener != null) taskItemListener.onTaskClick(task.id());

        });

        return rowView;
    }


    public interface TaskItemListener {
        void onTaskClick(String id);
        void onCompleteTaskClick(String id);
        void onActivateTaskClick(String id);
    }
}
