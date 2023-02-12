package com.kolotseyd.f_studioapp.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kolotseyd.f_studioapp.R;
import com.kolotseyd.f_studioapp.UpdateMaterialsDataActivity;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MaterialDataAdapter extends RecyclerView.Adapter<MaterialDataAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClicked(MaterialData materialData, int position, View v);
    }

    private final LayoutInflater inflater;
    private final List<MaterialData> materialDataList;
    private final  OnItemClickListener onItemClickListener;
    private View previousElement;


    public MaterialDataAdapter(Context context, List<MaterialData> materialDataList, OnItemClickListener onItemClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.materialDataList = materialDataList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.material_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialData materialData = materialDataList.get(position);
        holder.tvMaterialFormat.setText(materialData.getMaterialFormatAndType());
        holder.tvMaterialCurrentValue.setText(materialData.getMaterialCurrentValue());
        holder.tvMaterialCurrentCost.setText(materialData.getMaterialCurrentCost());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousElement != null){
                    previousElement.setBackgroundResource(R.drawable.default_rounded_menu_buttons);
                }
                view.setBackgroundResource(R.drawable.active_rounded_menu_buttons);
                previousElement = view;
                onItemClickListener.onItemClicked(materialData, holder.getAdapterPosition(), holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView tvMaterialFormat, tvMaterialCurrentValue, tvMaterialCurrentCost;
        final LinearLayout llMaterialItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llMaterialItem = itemView.findViewById(R.id.llMaterialItem);
            tvMaterialFormat = itemView.findViewById(R.id.tvMaterialFormatAndType);
            tvMaterialCurrentValue = itemView.findViewById(R.id.tvMaterialCurrentValue);
            tvMaterialCurrentCost = itemView.findViewById(R.id.tvMaterialCurrentCost);
        }
    }
}
