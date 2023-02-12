package com.kolotseyd.f_studioapp.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kolotseyd.f_studioapp.MainActivity;
import com.kolotseyd.f_studioapp.R;

import java.util.List;

public class OrderDataAdapter extends RecyclerView.Adapter<OrderDataAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<OrderData> orderDataList;

    public OrderDataAdapter(Context context, List<OrderData> orderDataList){
        this.orderDataList = orderDataList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.order_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderData orderData = orderDataList.get(position);
        holder.tvOrderName.setText(orderData.getName());
        holder.tvOrderFormat.setText(orderData.getFormatAndType());
        holder.tvOrderValue.setText(orderData.getValue());

        holder.ivDeleteValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderDataList.size() != 0){
                    orderDataList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), orderDataList.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final ImageView ivDeleteValue;
        final TextView tvOrderName, tvOrderFormat, tvOrderValue;
        final LinearLayout llOrderItem;
        public ViewHolder(@NonNull View view) {
            super(view);
            tvOrderName = view.findViewById(R.id.tvOrderName);
            tvOrderFormat = view.findViewById(R.id.tvOrderFormatAndType);
            tvOrderValue = view.findViewById(R.id.tvOrderValue);
            ivDeleteValue = view.findViewById(R.id.ivDeleteValue);
            llOrderItem = view.findViewById(R.id.llOrderItem);
        }
    }
}
