package com.kolotseyd.f_studioapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kolotseyd.f_studioapp.R;
import com.kolotseyd.f_studioapp.UpdateMaterialsDataActivity;

public class EnterPasswordDialogFragment extends DialogFragment {

    private static final String password = "123";
    int stupidCounter;
    EditText etPassword;
    TextView tvNegativeButton, tvPositiveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Password");
        View v = inflater.inflate(R.layout.dialog_fragment_enter_password, null);
        etPassword = v.findViewById(R.id.etPassword);
        tvNegativeButton = v.findViewById(R.id.tvNegativeButton);
        tvPositiveButton = v.findViewById(R.id.tvPositiveButton);

        stupidCounter = 0;

        tvNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPassword.getText().toString().equals(password)){
                    Intent intent = new Intent(getContext(), UpdateMaterialsDataActivity.class);
                    startActivity(intent);
                } else {
                    if (stupidCounter != 2){
                        stupidCounter++;
                        etPassword.setBackgroundResource(R.drawable.negative_rounded_menu_buttons);
                        Toast.makeText(getContext(), "Ромка не лезь!", Toast.LENGTH_SHORT).show();
                    } else if(stupidCounter == 2){
                        stupidCounter = 0;
                        etPassword.setBackgroundResource(R.drawable.negative_rounded_menu_buttons);
                        Toast.makeText(getContext(), "Рома, блять, ты тупой? Сказали же не лезь", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        return v;
    }
}
