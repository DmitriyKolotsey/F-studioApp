package com.kolotseyd.f_studioapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.kolotseyd.f_studioapp.Data.DataBaseHelper;
import com.kolotseyd.f_studioapp.Data.ExcelUtils;
import com.kolotseyd.f_studioapp.databinding.ActivityReportsBinding;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    ActivityReportsBinding activityReportsBinding;

    DataBaseHelper dataBaseHelper;
    ExcelUtils excelUtils;

    Animation buttonsAnimation;

    String reportDate, reportType, reportPeriod;
    String reportName, firstReportName, secondReportName, thirdReportName, fourthReportName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityReportsBinding = DataBindingUtil.setContentView(this, R.layout.activity_reports);

        dataBaseHelper = new DataBaseHelper(getApplicationContext());
        excelUtils = new ExcelUtils();

        buttonsAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttons_animation);

        reportDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        activityReportsBinding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String reportDay;
                if (day < 10){
                    reportDay = "0"+day;
                } else reportDay = String.valueOf(day);
                int month = calendar.get(Calendar.MONTH) + 1;
                String reportMonth;
                if (month <10){
                    reportMonth = "0"+month;
                } else reportMonth = String.valueOf(month);
                int reportYear = calendar.get(Calendar.YEAR);

                reportDate = reportDay + "-" + reportMonth + "-" + reportYear;
            }
        });

        View.OnClickListener onClickExit = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                Intent intent = new Intent(ReportsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };

        activityReportsBinding.tvCloseReportsActivity.setOnClickListener(onClickExit);

        View.OnClickListener onClickSelectReportType = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                setActiveMenuButtonBackground(view);
                reportType = ((TextView) view).getText().toString();
            }
        };

        activityReportsBinding.tvServiceReport.setOnClickListener(onClickSelectReportType);
        activityReportsBinding.tvMaterialsReport.setOnClickListener(onClickSelectReportType);
        activityReportsBinding.tvMoneyReport.setOnClickListener(onClickSelectReportType);
        activityReportsBinding.tvAllReports.setOnClickListener(onClickSelectReportType);

        View.OnClickListener onClickSelectReportPeriod = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                setActiveButtonBackground(view);
                reportPeriod = ((TextView) view).getText().toString();
                if (view.getId() == R.id.tvYearReport){
                    activityReportsBinding.tvYearValue.setVisibility(View.VISIBLE);
                } else if (view.getId() == R.id.tvDayReport || view.getId() == R.id.tvMonthReport){
                    activityReportsBinding.tvYearValue.setVisibility(View.INVISIBLE);
                }
            }
        };

        activityReportsBinding.tvDayReport.setOnClickListener(onClickSelectReportPeriod);
        activityReportsBinding.tvMonthReport.setOnClickListener(onClickSelectReportPeriod);
        activityReportsBinding.tvYearReport.setOnClickListener(onClickSelectReportPeriod);

        View.OnClickListener onClickGenerateReport = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                generateReport();
                setDefaultButtonBackground();
                setDefaultMenuButtonBackground();
            }
        };

        activityReportsBinding.tvGenerateReport.setOnClickListener(onClickGenerateReport);

        View.OnClickListener onClickGenerateAndSendReport = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                generateReport();
                if (reportName != null){
                    Log.d("TAG", reportName);
                    getAndSendFile(reportName);
                    reportName = null;
                } else if (firstReportName != null){
                    getAndSendFiles(firstReportName, secondReportName, thirdReportName, fourthReportName);
                    firstReportName = null;
                    secondReportName = null;
                    thirdReportName = null;
                    fourthReportName = null;
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка. Файл не найден", Toast.LENGTH_SHORT).show();
                }
            }
        };
        activityReportsBinding.tvGenerateAndSendReport.setOnClickListener(onClickGenerateAndSendReport);
    }

    private void generateReport(){
        goToDefault();
        boolean isSuccess = false;
        Log.d("TAG", reportDate + " " + reportType + " " + reportPeriod);

        if (reportType == null || reportPeriod == null){
            Toast.makeText(getApplicationContext(), "Параметры отчёта не выбраны", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("TAG", "in else");
            switch (reportType) {
                case "Услуги":
                    switch (reportPeriod) {
                        case "За день":
                            Log.d("TAG", "in for day switch");
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate + ". Услуги. Отчёт за день.xlsx",
                                    excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate = '" + reportDate + "'")));
                            reportName = reportDate + ". Услуги. Отчёт за день.xlsx";
                            break;
                        case "За месяц":
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(3) + ". Услуги. Отчёт за месяц.xlsx",
                                    excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate like '%" + reportDate.substring(3) + "'")));
                            reportName = reportDate.substring(3) + ". Услуги. Отчёт за месяц.xlsx";
                            break;
                        case "За год":
                            if (activityReportsBinding.tvYearValue.getText().toString().length() != 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), activityReportsBinding.tvYearValue.getText().toString() + ". Услуги. Отчёт за год.xlsx",
                                        excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate like '%" + activityReportsBinding.tvYearValue.getText().toString() + "'")));
                            } else if (activityReportsBinding.tvYearValue.getText().toString().length() == 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(6) + ". Услуги. Отчёт за год.xlsx",
                                        excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate like '%" + reportDate.substring(6) + "'")));
                            }
                            reportName = reportDate.substring(6) + ". Услуги. Отчёт за год.xlsx";
                            break;
                    }
                    break;
                case "Материалы":
                    switch (reportPeriod) {
                        case "За день":
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate + ". Материалы. Отчёт за день.xlsx",
                                    excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate = '" + reportDate + "'")));
                            reportName = reportDate + ". Материалы. Отчёт за день.xlsx";
                            break;
                        case "За месяц":
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(3) + ". Материалы. Отчёт за месяц.xlsx",
                                    excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate like '%" + reportDate.substring(3) + "'")));
                            reportName = reportDate.substring(3) + ". Материалы. Отчёт за месяц.xlsx";
                            break;
                        case "За год":
                            if (activityReportsBinding.tvYearValue.getText().toString().length() != 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), activityReportsBinding.tvYearValue.getText().toString() + ". Материалы. Отчёт за год.xlsx",
                                        excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate like '%" + activityReportsBinding.tvYearValue.getText().toString() + "'")));
                            } else if (activityReportsBinding.tvYearValue.getText().toString().length() == 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(6) + ". Материалы. Отчёт за год.xlsx",
                                        excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate like '%" + reportDate.substring(6) + "'")));
                            }
                            reportName = reportDate.substring(6) + ". Материалы. Отчёт за год.xlsx";
                            break;
                    }
                    break;
                case "Выручка":
                    switch (reportPeriod) {
                        case "За день":
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate + ". Выручка. Отчёт за день.xlsx",
                                    excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date = '" + reportDate + "'")));
                            reportName = reportDate + ". Выручка. Отчёт за день.xlsx";
                            break;
                        case "За месяц":
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(3) + ". Выручка. Отчёт за месяц.xlsx",
                                    excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date like '%" + reportDate.substring(3) + "'")));
                            reportName = reportDate.substring(3) + ". Выручка. Отчёт за месяц.xlsx";
                            break;
                        case "За год":
                            if (activityReportsBinding.tvYearValue.getText().toString().length() != 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), activityReportsBinding.tvYearValue.getText().toString() + ". Выручка. Отчёт за год.xlsx",
                                        excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date like '%" + activityReportsBinding.tvYearValue.getText().toString() + "'")));
                            } else if (activityReportsBinding.tvYearValue.getText().toString().length() == 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(6) + ". Выручка. Отчёт за год.xlsx",
                                        excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date like '%" + reportDate.substring(6) + "'")));
                            }
                            reportName = reportDate.substring(6) + ". Выручка. Отчёт за год.xlsx";
                            break;
                    }
                    break;
                case "Все отчёты":
                    switch (reportPeriod) {
                        case "За день":
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate + ". Услуги. Отчёт за день.xlsx",
                                    excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate = '" + reportDate + "'")));
                            firstReportName = reportDate + ". Услуги. Отчёт за день.xlsx";
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate +  ". Материалы. Отчёт за день.xlsx",
                                    excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate = '" + reportDate + "'")));
                            secondReportName = reportDate + ". Материалы. Отчёт за день.xlsx";
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate + ". Выручка. Отчёт за день.xlsx",
                                    excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date = '" + reportDate + "'")));
                            fourthReportName = reportDate +  ". Выручка. Отчёт за день.xlsx";
                            break;
                        case "За месяц":
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(3) + ". Услуги. Отчёт за месяц.xlsx",
                                    excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate like '%" + reportDate.substring(3) + "'")));
                            firstReportName = reportDate.substring(3) +  ". Услуги. Отчёт за месяц.xlsx";
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(3) + ". Материалы. Отчёт за месяц.xlsx",
                                    excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate like '%" + reportDate.substring(3) + "'")));
                            secondReportName = reportDate.substring(3) + ". Материалы. Отчёт за месяц.xlsx";
                            isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(3) + ". Выручка. Отчёт за месяц.xlsx",
                                    excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date like '%" + reportDate.substring(3) + "'")));
                            fourthReportName = reportDate.substring(3) +  ". Выручка. Отчёт за месяц.xlsx";
                            break;
                        case "За год":
                            if (activityReportsBinding.tvYearValue.getText().toString().length() != 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), activityReportsBinding.tvYearValue.getText().toString() + ". Услуги. Отчёт за год.xlsx",
                                        excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate like '%" + activityReportsBinding.tvYearValue.getText().toString() + "'")));
                            } else if (activityReportsBinding.tvYearValue.getText().toString().length() == 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(6) + ". Услуги. Отчёт за год.xlsx",
                                        excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate like '%" + reportDate.substring(6) + "'")));
                            }
                            firstReportName = reportDate.substring(6) +  ". Услуги. Отчёт за год.xlsx";
                            if (activityReportsBinding.tvYearValue.getText().toString().length() != 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), activityReportsBinding.tvYearValue.getText().toString() + ". Материалы. Отчёт за год.xlsx",
                                        excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate like '%" + activityReportsBinding.tvYearValue.getText().toString() + "'")));
                            } else if (activityReportsBinding.tvYearValue.getText().toString().length() == 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(6) + ". Материалы. Отчёт за год.xlsx",
                                        excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate like '%" + reportDate.substring(6) + "'")));
                            }
                            secondReportName = reportDate.substring(6) + ". Материалы. Отчёт за год.xlsx";
                            if (activityReportsBinding.tvYearValue.getText().toString().length() != 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), activityReportsBinding.tvYearValue.getText().toString() + ". Выручка. Отчёт за год.xlsx",
                                        excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date like '%" + activityReportsBinding.tvYearValue.getText().toString() + "'")));
                            } else if (activityReportsBinding.tvYearValue.getText().toString().length() == 0){
                                isSuccess = excelUtils.storeExcelInStorage(getApplicationContext(), reportDate.substring(6) + ". Выручка. Отчёт за год .xlsx",
                                        excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date like '%" + reportDate.substring(6) + "'")));
                            }
                            fourthReportName = reportDate.substring(6) + ". Выручка. Отчёт за год.xlsx";
                    }
                    break;
            }
        }
        reportType = null;
        reportPeriod = null;
        if (isSuccess){
            activityReportsBinding.tvYearValue.setText("");
            Toast.makeText(getApplicationContext(), "Отчёт сгенерирован", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
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

    private void getAndSendFiles(String firstFileName, String secondFileName, String thirdFileName, String fourthFileName){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND_MULTIPLE);
        String[] subFirstFileName = firstFileName.split("\\.");
        String[] subSecondFileName = secondFileName.split("\\.");
        String[] subFourthFileName = fourthFileName.split("\\.");
        File file = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)));
        File firstFile = new File(file.getAbsolutePath() + "/" + subFirstFileName[1], firstFileName);
        File secondFile = new File(file.getAbsolutePath() + "/" + subSecondFileName[1], secondFileName);
        File fourthFile = new File(file.getAbsolutePath() + "/" + subFourthFileName[1], fourthFileName);
        if (firstFile.exists() && secondFile.exists() && fourthFile.exists()){
            intentShareFile.setType(URLConnection.guessContentTypeFromName(firstFileName));
            ArrayList<Uri> uriArrayList = new ArrayList<>();
            uriArrayList.add(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", firstFile));
            uriArrayList.add(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", secondFile));
            uriArrayList.add(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", fourthFile));
            intentShareFile.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
            startActivity(Intent.createChooser(intentShareFile, "Share Files"));
            uriArrayList.clear();
        }
    }

    private void setActiveMenuButtonBackground(View view){
        setDefaultMenuButtonBackground();
        view.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
    }

    private void setDefaultMenuButtonBackground(){
        activityReportsBinding.tvServiceReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvMaterialsReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvMoneyReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvAllReports.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
    }

    private void setActiveButtonBackground(View view){
        setDefaultButtonBackground();
        view.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
    }

    private void setDefaultButtonBackground(){
        activityReportsBinding.tvDayReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvMonthReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvYearReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
    }

    private void goToDefault(){
        activityReportsBinding.tvDayReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvMonthReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvYearReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvServiceReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvMaterialsReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvMoneyReport.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        activityReportsBinding.tvAllReports.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
    }
}