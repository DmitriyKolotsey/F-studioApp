package com.kolotseyd.f_studioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kolotseyd.f_studioapp.Data.DataBaseHelper;
import com.kolotseyd.f_studioapp.Data.ExcelUtils;
import com.kolotseyd.f_studioapp.Data.MaterialData;
import com.kolotseyd.f_studioapp.Data.MaterialDataAdapter;
import com.kolotseyd.f_studioapp.databinding.ActivityUpdateMaterialsDataBinding;


import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UpdateMaterialsDataActivity extends AppCompatActivity {

    ActivityUpdateMaterialsDataBinding activityUpdateMaterialsDataBinding;

    DataBaseHelper dataBaseHelper;
    SQLiteDatabase sqLiteDatabase;
    ArrayList<MaterialData> materialDataArrayList = new ArrayList<>();
    Cursor cursor;
    MaterialDataAdapter materialDataAdapter;
    RecyclerView recyclerView;
    Spinner materialDataSpinner;

    Animation buttonsAnimation;

    TextView inputTextView, tvToastText;
    View toastLayout;

    String operation;
    double materialCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityUpdateMaterialsDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_materials_data);

        dataBaseHelper = new DataBaseHelper(getApplicationContext());
        dataBaseHelper.create_db();
        sqLiteDatabase = dataBaseHelper.open();

        buttonsAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttons_animation);

        ArrayList<String> materialNames;
        materialNames = dataBaseHelper.getMaterialNames();

        materialDataSpinner = findViewById(R.id.spinnerMaterialsName);
        ArrayAdapter<?> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item,materialNames);
        //ArrayAdapter.createFromResource(this, R.array.materialNames, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialDataSpinner.setAdapter(adapter);

        recyclerView = findViewById(R.id.rvMaterialsData);

        MaterialDataAdapter.OnItemClickListener itemClickListener = new MaterialDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(MaterialData materialData, int position, View v) {
                v.startAnimation(buttonsAnimation);
                activityUpdateMaterialsDataBinding.selectedMaterial.setTag(position);
                activityUpdateMaterialsDataBinding.selectedMaterial.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
                activityUpdateMaterialsDataBinding.tvSelectedMaterialName.setText(materialDataSpinner.getSelectedItem().toString());
                activityUpdateMaterialsDataBinding.tvSelectedMaterialFormatAndType.setText(materialData.getMaterialFormatAndType());
                materialCost = Double.parseDouble(materialData.getMaterialCurrentCost());
                inputTextView = activityUpdateMaterialsDataBinding.tvMaterialsCountValue;
                activityUpdateMaterialsDataBinding.tvMaterialsCount.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
            }
        };

        View.OnClickListener onClickExit = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                Intent intent = new Intent(UpdateMaterialsDataActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };

        activityUpdateMaterialsDataBinding.tvCloseUpdateMaterialsActivity.setOnClickListener(onClickExit);

        materialDataAdapter = new MaterialDataAdapter(this, materialDataArrayList, itemClickListener);
        recyclerView.setAdapter(materialDataAdapter);

        materialDataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                materialDataArrayList.clear();
                setDataFromDatabaseInRecyclerView();
                materialDataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onClickKeyBoardButtonClear(View view) {
        view.startAnimation(buttonsAnimation);
        operation = "";
        inputTextView.setText("");
        inputTextView.setHint("");
    }

    public void onClickKeyBoardButtonDelete(View view) {
        view.startAnimation(buttonsAnimation);
        if (inputTextView.getText().length() != 0){
            inputTextView.setText(inputTextView.getText().toString().substring(0, inputTextView.getText().length()-1));
        } else if (inputTextView.getText().length() == 0 && inputTextView.getHint().length() != 0){
            inputTextView.setText(inputTextView.getHint().toString().substring(0, inputTextView.getHint().length()-1));
            inputTextView.setHint("");
        }
    }

    public void onClickKeyBoardOperation(View view) {
        view.startAnimation(buttonsAnimation);
        if (inputTextView == null){
            showCustomToast("Поле ввода не выбрано");
        } else {
            inputTextView.setText(((TextView) view).getText().toString());
        }
    }


    public void onClickKeyBoardButtonEnter(View view) {
        view.startAnimation(buttonsAnimation);
        if (activityUpdateMaterialsDataBinding.tvMaterialsCountValue.getText().equals("")){
            activityUpdateMaterialsDataBinding.tvMaterialCostValue.setText("0");
        } else {
            updateDataInDatabase();
        }
        inputTextView = activityUpdateMaterialsDataBinding.tvMaterialsCountValue;
        activityUpdateMaterialsDataBinding.tvMaterialsCount.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
        activityUpdateMaterialsDataBinding.tvMaterialCost.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityUpdateMaterialsDataBinding.tvMaterialsCountValue.setText("");
        activityUpdateMaterialsDataBinding.tvMaterialCostValue.setText("");
    }

    public  void onClickKeyBoardButton(View view) {
        view.startAnimation(buttonsAnimation);
        if (inputTextView == null){
            showCustomToast("Поле ввода не выбрано");
        } else {
            inputTextView.setText(inputTextView.getText().toString() + ((TextView) view).getText().toString());
        }
    }

    public void onClickClearSelectedMaterial(View view) {
        view.startAnimation(buttonsAnimation);
        ContentValues cv = new ContentValues();
        cv.put("MaterialTotalValue", 0);
        cv.put("MaterialCost", 0);
        int result = 0;

        if (activityUpdateMaterialsDataBinding.tvSelectedMaterialFormatAndType.getText().length() != 0){
            Log.d("TAG", "full material update");
            result = dataBaseHelper.updateFullMaterialValue(cv, activityUpdateMaterialsDataBinding.tvSelectedMaterialName.getText().toString(),
                    activityUpdateMaterialsDataBinding.tvSelectedMaterialFormatAndType.getText().toString());
        }
        setDataFromDatabaseInRecyclerView();
        if (result == 1){
            Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка. Данные не обновлены", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDataInDatabase() {
        int updateIndex = Integer.parseInt(activityUpdateMaterialsDataBinding.selectedMaterial.getTag().toString());
        int materialsCount = Integer.parseInt(materialDataArrayList.get(updateIndex).getMaterialCurrentValue());
        int additionalMaterialsCount = Integer.parseInt(activityUpdateMaterialsDataBinding.tvMaterialsCountValue.getText().toString());
        int newMaterialsCount = materialsCount + additionalMaterialsCount;

        if (!activityUpdateMaterialsDataBinding.tvMaterialCostValue.getText().toString().equals("")) {
            if (activityUpdateMaterialsDataBinding.tvMaterialCostValue.getText().toString().charAt(0) == '.'){
                String stringMaterialCost = "0" + activityUpdateMaterialsDataBinding.tvMaterialCostValue.getText().toString();
                materialCost = Double.parseDouble(stringMaterialCost);
            } else {
                materialCost = Double.parseDouble(activityUpdateMaterialsDataBinding.tvMaterialCostValue.getText().toString());
            }
        }

        int result = 0;

        ContentValues cv = new ContentValues();
        cv.put("MaterialTotalValue", newMaterialsCount);
        cv.put("MaterialCost", materialCost);
        if (activityUpdateMaterialsDataBinding.tvSelectedMaterialFormatAndType.getText().length() != 0){
            Log.d("TAG", "full material update");
            result = dataBaseHelper.updateFullMaterialValue(cv, activityUpdateMaterialsDataBinding.tvSelectedMaterialName.getText().toString(),
                    activityUpdateMaterialsDataBinding.tvSelectedMaterialFormatAndType.getText().toString());
        }
        setDataFromDatabaseInRecyclerView();
        if (result == 1){
            Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка. Данные не обновлены", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomToast(String textForToast){
        LayoutInflater inflater = getLayoutInflater();
        toastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toastLayout));

        tvToastText = (TextView) toastLayout.findViewById(R.id.tvToastText);
        tvToastText.setText(textForToast);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }

    private void setDataFromDatabaseInRecyclerView(){
        cursor = sqLiteDatabase
                .rawQuery("select MaterialName, MaterialFormatAndType, MaterialTotalValue, MaterialCost from materials where MaterialName = '"
                        + materialDataSpinner.getSelectedItem() + "' and IsMaterialDeleted <> 1 and IsMaterialFormatDeleted <> 1", null);
        cursor.moveToFirst();
        materialDataArrayList.clear();
        if (cursor.moveToFirst()){
            do {
                materialDataArrayList.add(new MaterialData(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        materialDataAdapter.notifyDataSetChanged();
    }

    public void onClickSelectInputTextView(View view) {
        view.startAnimation(buttonsAnimation);
        if (view.getId() == R.id.tvMaterialsCount || view.getId() == R.id.tvMaterialsCountValue){
            activityUpdateMaterialsDataBinding.tvMaterialsCount.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
            activityUpdateMaterialsDataBinding.tvMaterialCost.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
            inputTextView = activityUpdateMaterialsDataBinding.tvMaterialsCountValue;
        } else if (view.getId() == R.id.tvMaterialCost || view.getId() == R.id.tvMaterialCostValue){
            activityUpdateMaterialsDataBinding.tvMaterialCost.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
            activityUpdateMaterialsDataBinding.tvMaterialsCount.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
            inputTextView = activityUpdateMaterialsDataBinding.tvMaterialCostValue;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UpdateMaterialsDataActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //дописать обновление списков
    public void onClickDeleteMaterial(View view) {
        dataBaseHelper.deleteMaterial(materialDataSpinner.getSelectedItem().toString());
        Toast.makeText(this, "Материал удалён", Toast.LENGTH_SHORT).show();
        materialDataArrayList.clear();
        setDataFromDatabaseInRecyclerView();
        materialDataAdapter.notifyDataSetChanged();
    }

    public void onClickDeleteMaterialFormat(View view) {
        dataBaseHelper.deleteMaterialFormat(materialDataSpinner.getSelectedItem().toString(),
                activityUpdateMaterialsDataBinding.tvSelectedMaterialFormatAndType.getText().toString());
        Toast.makeText(this, "Формат удалён", Toast.LENGTH_SHORT).show();
        materialDataArrayList.clear();
        setDataFromDatabaseInRecyclerView();
        materialDataAdapter.notifyDataSetChanged();
    }

    public void onClickDownloadReport(View view) {
        ExcelUtils excelUtils = new ExcelUtils();
        boolean isSuccess = false;
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), currentDate + ". Материалы. Текущее количество.xlsx",
                excelUtils.writeActualMaterialCount(dataBaseHelper.getMaterialsCurrentCount()));

        if (isSuccess){
            Toast.makeText(this, "Отчёт сгенерирован", Toast.LENGTH_SHORT).show();
            getAndSendFile(currentDate + ". Материалы. Текущее количество.xlsx");
        } else Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
    }

    private void getAndSendFile(String fileName){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        String[] subFileName = fileName.split("\\.");
        File firstReport = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + subFileName[1], fileName);
        if (firstReport.exists()){
            intentShareFile.setType(URLConnection.guessContentTypeFromName(fileName));
            intentShareFile.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", firstReport));
            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }
}