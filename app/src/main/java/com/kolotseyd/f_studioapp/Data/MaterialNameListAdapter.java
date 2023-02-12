package com.kolotseyd.f_studioapp.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kolotseyd.f_studioapp.R;

import java.util.List;

public class MaterialNameListAdapter extends RecyclerView.Adapter<MaterialNameListAdapter.ViewHolder> {

    public interface OnMaterialNameSelectListener{
        void onMaterialNameSelected(String materialName, int position, View v);
    }

    private final LayoutInflater inflater;
    private final List<String> materialNamesList;
    private final OnMaterialNameSelectListener onMaterialNameSelectListener;
    private View previousElement;
    private Animation buttonsAnimation;

    public MaterialNameListAdapter(Context context, List<String> materialNamesList, OnMaterialNameSelectListener onMaterialNameSelectListener) {
        this.inflater = LayoutInflater.from(context);
        this.materialNamesList = materialNamesList;
        this.onMaterialNameSelectListener = onMaterialNameSelectListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.material_name_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String materialName = materialNamesList.get(position);
        holder.tvMaterialName.setText(materialName);
        buttonsAnimation = AnimationUtils.loadAnimation(inflater.getContext(), R.anim.buttons_animation);
        holder.tvMaterialName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                if (previousElement != null){
                    previousElement.setBackgroundResource(R.drawable.default_rounded_menu_buttons);
                }
                view.setBackgroundResource(R.drawable.active_rounded_menu_buttons);
                previousElement = view;
                onMaterialNameSelectListener.onMaterialNameSelected(materialName, holder.getAdapterPosition(), holder.itemView);
                //onItemClickListener.onItemClicked(materialData, holder.getAdapterPosition(), holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialNamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMaterialName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaterialName = itemView.findViewById(R.id.tvMaterialName);
        }
    }
}
