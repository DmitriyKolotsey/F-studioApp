package com.kolotseyd.f_studioapp.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kolotseyd.f_studioapp.R;

import java.util.List;

public class MaterialFormatTypeAndCountListAdapter extends RecyclerView.Adapter<MaterialFormatTypeAndCountListAdapter.ViewHolder> {

    public interface OnMaterialFormatAndTypeSelectListener{
        void onMaterialFormatAndTypeSelected(MaterialFormatTypeAndCountData materialFormatTypeAndCountData, int position, View v);
    }

    private LayoutInflater inflater;
    private List<MaterialFormatTypeAndCountData> materialFormatTypeAndCountDataList;
    private OnMaterialFormatAndTypeSelectListener onMaterialFormatAndTypeSelectListener;
    private View previousElement;
    private Animation buttonsAnimation;

    public MaterialFormatTypeAndCountListAdapter(Context context, List<MaterialFormatTypeAndCountData> materialFormatTypeAndCountDataList,
                                                 OnMaterialFormatAndTypeSelectListener onMaterialFormatAndTypeSelectListener) {
        this.inflater = LayoutInflater.from(context);
        this.materialFormatTypeAndCountDataList = materialFormatTypeAndCountDataList;
        this.onMaterialFormatAndTypeSelectListener = onMaterialFormatAndTypeSelectListener;
    }

    @NonNull
    @Override
    public MaterialFormatTypeAndCountListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.material_format_name_and_count_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialFormatTypeAndCountListAdapter.ViewHolder holder, int position) {
        MaterialFormatTypeAndCountData materialFormatTypeAndCountData = materialFormatTypeAndCountDataList.get(position);
        buttonsAnimation = AnimationUtils.loadAnimation(inflater.getContext(), R.anim.buttons_animation);
        holder.tvRecyclerViewMaterialFormatAndType.setText(materialFormatTypeAndCountData.getMaterialFormatAndType());
        if (holder.tvRecyclerViewMaterialFormatAndType.getText().equals("Без расхода материалов")){
            holder.tvRecyclerViewMaterialCount.setWidth(0);
            holder.tvRecyclerViewMaterialCount.setHeight(0);
            holder.tvRecyclerViewMaterialCount.setVisibility(View.INVISIBLE);
            holder.divider.setVisibility(View.INVISIBLE);

            holder.tvRecyclerViewMaterialFormatAndType.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        holder.tvRecyclerViewMaterialCount.setText(materialFormatTypeAndCountData.getMaterialCount());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                if (previousElement != null){
                    previousElement.setBackgroundResource(R.drawable.default_rounded_menu_buttons);
                }
                view.setBackgroundResource(R.drawable.active_rounded_menu_buttons);
                previousElement = view;
                onMaterialFormatAndTypeSelectListener.onMaterialFormatAndTypeSelected(materialFormatTypeAndCountData,
                        holder.getAdapterPosition(), holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialFormatTypeAndCountDataList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        final TextView tvRecyclerViewMaterialFormatAndType, tvRecyclerViewMaterialCount;
        final View divider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecyclerViewMaterialFormatAndType = itemView.findViewById(R.id.tvRecyclerViewMaterialFormatAndType);
            tvRecyclerViewMaterialCount = itemView.findViewById(R.id.tvRecyclerViewMaterialCount);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
