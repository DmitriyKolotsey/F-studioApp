package com.kolotseyd.f_studioapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.kolotseyd.f_studioapp.Data.DataBaseHelper;
import com.kolotseyd.f_studioapp.MainActivity;
import com.kolotseyd.f_studioapp.R;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;

public class CreateDataBaseBackupDialogFragment extends DialogFragment{

    com.kolotseyd.f_studioapp.databinding.DialogFragmentCreateDatabaseBackupBinding dialogFragmentCreateDatabaseBackupBinding;
    DataBaseHelper dataBaseHelper;

    private static final String DB_NAME = "f_studio_app_database_modern.db";
    private static String DB_PATH = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("CloseWork");
        dialogFragmentCreateDatabaseBackupBinding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_fragment_create_database_backup, container, false);
        View view = dialogFragmentCreateDatabaseBackupBinding.getRoot();

        dataBaseHelper = new DataBaseHelper(getContext());

        dialogFragmentCreateDatabaseBackupBinding.tvSendDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentShareFile = new Intent(Intent.ACTION_SEND_MULTIPLE);
                File database = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/База данных");
                if (database.isDirectory()) {
                    String[] children = database.list();
                    Log.d("TAG", database.getAbsolutePath());
                    Log.d("TAG", String.valueOf(children.length));
                    ArrayList<Uri> dbFilesUriArrayList = new ArrayList<>();
                    for (String child : children) {
                        File file = new File(database, child);
                        dbFilesUriArrayList.add(FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file));
                        intentShareFile.putParcelableArrayListExtra(Intent.EXTRA_STREAM, dbFilesUriArrayList);
                    }
                }
                intentShareFile.setType("html/txt");
                startActivity(Intent.createChooser(intentShareFile, "Share File"));

                dismiss();
            }
        });

        dialogFragmentCreateDatabaseBackupBinding.tvSaveDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFolder();
                dismiss();
            }
        });

        return view;
    }

    private void openFolder(){
        Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        ((MainActivity) getActivity()).activityResultLauncher.launch(chooseFile);
    }

}
