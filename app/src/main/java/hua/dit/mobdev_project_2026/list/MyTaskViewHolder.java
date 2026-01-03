package hua.dit.mobdev_project_2026.list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import hua.dit.mobdev_project_2026.R;

public class MyTaskViewHolder extends RecyclerView.ViewHolder {
    TextView item_id;
    TextView item_short_name, item_status;
    ImageView item_icon;

    public MyTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        item_id = itemView.findViewById(R.id.item_id);
        item_short_name = itemView.findViewById(R.id.item_short_name);
        item_status = itemView.findViewById(R.id.item_status);
        item_icon = itemView.findViewById(R.id.item_icon);
    }
}


