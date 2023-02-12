package com.kolotseyd.f_studioapp.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.kolotseyd.f_studioapp.Data.DataBaseHelper;
import com.kolotseyd.f_studioapp.R;
import com.kolotseyd.f_studioapp.databinding.DialogFragmentAddNewMaterialBinding;

import java.util.ArrayList;

public class AddNewMaterialDialogFragment extends DialogFragment {

    private DialogFragmentAddNewMaterialBinding dialogFragmentAddNewMaterialBinding;
    private DataBaseHelper dataBaseHelper;
    SQLiteDatabase sqLiteDatabase;
    private Spinner materialDataSpinner;
    private String newMaterialName, newMaterialFormatAndType;
    private int newMaterialCount;
    private double newMaterialCost;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("CloseWork");
        dialogFragmentAddNewMaterialBinding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_fragment_add_new_material, container, false);
        View view = dialogFragmentAddNewMaterialBinding.getRoot();

        dataBaseHelper = new DataBaseHelper(getContext());
        dataBaseHelper.create_db();
        sqLiteDatabase = dataBaseHelper.open();

        ArrayList<String> materialNames;
        materialNames = dataBaseHelper.getMaterialNames();

        materialDataSpinner = view.findViewById(R.id.spinnerMaterialsName);
        ArrayAdapter<?> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, materialNames);
                //ArrayAdapter.createFromResource(getContext(), R.array.materialNames, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialDataSpinner.setAdapter(adapter);

        dataBaseHelper = new DataBaseHelper(getContext());

        dialogFragmentAddNewMaterialBinding.tvButtonAddNewMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getNewMaterialDataFromEditText()){
                    ContentValues cv = new ContentValues();
                    cv.put("MaterialName", newMaterialName);
                    cv.put("MaterialFormatAndType", newMaterialFormatAndType);
                    cv.put("MaterialTotalValue", newMaterialCount);
                    cv.put("MaterialCost", newMaterialCost);
                    cv.put("IsMaterialDeleted", 0);
                    cv.put("IsMaterialFormatDeleted", 0);

                    if (dataBaseHelper.insertNewMaterialInDatabase(cv) != -1){
                        Toast.makeText(getContext(), "Материал добавлен", Toast.LENGTH_SHORT).show();
                        dialogFragmentAddNewMaterialBinding.etAddedMaterialNameValue.setText("");
                        dialogFragmentAddNewMaterialBinding.etAddedMaterialFormatAndTypeValue.setText("");
                        dialogFragmentAddNewMaterialBinding.etAddedMaterialCountValue.setText("");
                        dialogFragmentAddNewMaterialBinding.etAddedMaterialCostValue.setText("");
                    } else Toast.makeText(getContext(), "Ошибка. Материал не добавлен", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogFragmentAddNewMaterialBinding.tvButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private boolean getNewMaterialDataFromEditText(){
        boolean isMaterialFormatAndTypeEntered, isMaterialCountEntered, isMaterialCostEntered;
        if (dialogFragmentAddNewMaterialBinding.etAddedMaterialNameValue.getText().length() != 0){
            newMaterialName = dialogFragmentAddNewMaterialBinding.etAddedMaterialNameValue.getText().toString();
        } else newMaterialName = dialogFragmentAddNewMaterialBinding.spinnerMaterialsName.getSelectedItem().toString();

        if (dialogFragmentAddNewMaterialBinding.etAddedMaterialFormatAndTypeValue.getText().length() != 0){
            newMaterialFormatAndType = dialogFragmentAddNewMaterialBinding.etAddedMaterialFormatAndTypeValue.getText().toString();
            isMaterialFormatAndTypeEntered = true;
        } else {
            Toast.makeText(getContext(), "Поле Формат и тип не заполнено", Toast.LENGTH_SHORT).show();
            isMaterialFormatAndTypeEntered = false;
        }

        if (dialogFragmentAddNewMaterialBinding.etAddedMaterialCountValue.getText().length() != 0){
            newMaterialCount = Integer.parseInt(dialogFragmentAddNewMaterialBinding.etAddedMaterialCountValue.getText().toString());
            isMaterialCountEntered = true;
        } else {
            Toast.makeText(getContext(), "Поле Количество не заполнено", Toast.LENGTH_SHORT).show();
            isMaterialCountEntered = false;
        }

        if (dialogFragmentAddNewMaterialBinding.etAddedMaterialCostValue.getText().length() != 0){
            newMaterialCost = Double.parseDouble(dialogFragmentAddNewMaterialBinding.etAddedMaterialCostValue.getText().toString());
            isMaterialCostEntered = true;
        } else {
            Toast.makeText(getContext(), "Поле Стоимость не заполнено", Toast.LENGTH_SHORT).show();
            isMaterialCostEntered = false;
        }

        return isMaterialFormatAndTypeEntered && isMaterialCountEntered && isMaterialCostEntered;
    }

    @Override
    public void onDismiss(@NonNull final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener){
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
