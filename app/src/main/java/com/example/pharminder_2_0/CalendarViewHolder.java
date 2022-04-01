package com.example.pharminder_2_0;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder {

    private final TextView dayOfmonth;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        dayOfmonth = itemView.findViewById(R.id.cellDayText);
    }
}