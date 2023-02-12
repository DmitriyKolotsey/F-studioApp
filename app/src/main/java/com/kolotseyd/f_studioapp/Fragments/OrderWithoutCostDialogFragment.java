package com.kolotseyd.f_studioapp.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kolotseyd.f_studioapp.Data.DataBaseHelper;
import com.kolotseyd.f_studioapp.Data.OrderData;
import com.kolotseyd.f_studioapp.MainActivity;
import com.kolotseyd.f_studioapp.R;

import java.util.ArrayList;

public class OrderWithoutCostDialogFragment extends DialogFragment {

    TextView tvPositiveButton, tvNegativeButton;
    DataBaseHelper dataBaseHelper;
    ArrayList<OrderData> orderDataArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("No materials");
        View v = inflater.inflate(R.layout.dialog_fragment_order_without_cost, null);
        tvPositiveButton = v.findViewById(R.id.tvPositiveButton);
        tvNegativeButton = v.findViewById(R.id.tvNegativeButton);

        dataBaseHelper = new DataBaseHelper(getContext());

        orderDataArrayList = getArguments().getParcelableArrayList("orderDataArrayList");

        tvNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long insert = dataBaseHelper.insertNoCostOrderInDataBase(orderDataArrayList);
                ((MainActivity) getActivity()).goToDefault();
                ((MainActivity) getActivity()).clearOrderInfo();
                Toast.makeText(getContext(), "Заказ закрыт и сохранен в БД. Заказов в БД: " + insert, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        return v;
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
