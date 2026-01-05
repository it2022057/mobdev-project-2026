package hua.dit.mobdev_project_2026.list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hua.dit.mobdev_project_2026.R;
import hua.dit.mobdev_project_2026.db.TaskWithStatus;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskViewHolder> {

    private static final String TAG = "MyTaskAdapter";

    private List<TaskWithStatus> taskList;

    private final OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(int taskId);
    }

    public MyTaskAdapter(List<TaskWithStatus> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View cardView = inflater.inflate(R.layout.activity_view_tasks_item, parent, false);
        return new MyTaskViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTaskViewHolder holder, int position) {
        TaskWithStatus task = taskList.get(position);
        holder.item_id.setText(String.valueOf(task.getId()));
        holder.item_short_name.setText(task.getShortName());
        holder.item_status.setText(task.getStatus());

        switch (holder.item_status.getText().toString().trim()) {
            case "RECORDED":
                holder.item_status.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.recorded_icon)
                );
                holder.item_icon.setImageResource(R.drawable.ic_recorded);
                break;
            case "IN_PROGRESS":
                holder.item_status.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.in_progress_icon)
                );
                holder.item_icon.setImageResource(R.drawable.ic_in_progress);
                break;
            case "COMPLETED":
                holder.item_status.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.completed_icon)
                );
                holder.item_icon.setImageResource(R.drawable.ic_completed);
                break;
            case "EXPIRED":
                holder.item_status.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.expired_icon)
                );
                holder.item_icon.setImageResource(R.drawable.ic_expired);
        }

        holder.itemView.setOnClickListener((v) -> {
            // Adds a subtle click animation to improve user feedback
            animateClick(v);
            Log.i(TAG, "List - Element Selected: " + holder.getAbsoluteAdapterPosition());
            listener.onTaskClick(task.getId());
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public List<TaskWithStatus> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskWithStatus> taskList) {
        this.taskList = taskList;
    }

    private void animateClick(View view) {
        view.animate()
                .scaleX(0.97f)
                .scaleY(0.97f)
                .setDuration(50)
                .withEndAction(() ->
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(50)
                )
                .start();
    }
}

