package com.kolotseyd.f_studioapp.Fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kolotseyd.f_studioapp.Data.DataBaseHelper;
import com.kolotseyd.f_studioapp.Data.MaterialFormatTypeAndCountData;
import com.kolotseyd.f_studioapp.Data.MaterialFormatTypeAndCountListAdapter;
import com.kolotseyd.f_studioapp.MainActivity;
import com.kolotseyd.f_studioapp.R;
import com.kolotseyd.f_studioapp.databinding.FragmentMaterialFormatTypeAndCountBinding;

import java.util.ArrayList;

public class MaterialFormatTypeAndCountFragment extends Fragment {

    private ArrayList<MaterialFormatTypeAndCountData> materialFormatTypeAndCountDataArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.kolotseyd.f_studioapp.databinding.FragmentMaterialFormatTypeAndCountBinding fragmentMaterialFormatTypeAndCountBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_material_format_type_and_count, container, false);
        View view = fragmentMaterialFormatTypeAndCountBinding.getRoot();

        String materialName = this.getArguments().getString("materialName");

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());

        MaterialFormatTypeAndCountListAdapter.OnMaterialFormatAndTypeSelectListener onMaterialFormatAndTypeSelectListener =
                new MaterialFormatTypeAndCountListAdapter.OnMaterialFormatAndTypeSelectListener() {
                    @Override
                    public void onMaterialFormatAndTypeSelected(MaterialFormatTypeAndCountData materialFormatTypeAndCountData, int position, View v) {
                        ((MainActivity) getActivity()).updateOrderInfoFormatAndType(materialFormatTypeAndCountData.getMaterialFormatAndType());
                    }
                };


        materialFormatTypeAndCountDataArrayList = dataBaseHelper.getMaterialFormatTypeAndCount(materialName);
        MaterialFormatTypeAndCountListAdapter materialFormatTypeAndCountListAdapter = new MaterialFormatTypeAndCountListAdapter(getContext(), materialFormatTypeAndCountDataArrayList, onMaterialFormatAndTypeSelectListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = fragmentMaterialFormatTypeAndCountBinding.rvMaterialsFormatTypeAndCount;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(materialFormatTypeAndCountListAdapter);


        return view;
    }


}
