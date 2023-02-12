package com.kolotseyd.f_studioapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.kolotseyd.f_studioapp.Data.DataBaseHelper;
import com.kolotseyd.f_studioapp.Data.ExcelUtils;
import com.kolotseyd.f_studioapp.R;
import com.kolotseyd.f_studioapp.databinding.DialogFragmentCloseWorkBinding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CloseWorkDialogFragment extends DialogFragment {

    DialogFragmentCloseWorkBinding dialogFragmentCloseWorkBinding;
    ExcelUtils excelUtils;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase sqLiteDatabase;
    boolean isServicesReportSuccess, isMaterialsReportSuccess, isMoneyGainReportSuccess, isFullMoneyGainReportSuccess;
    String currentDate;

    Animation buttonsAnimation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("CloseWork");
        dialogFragmentCloseWorkBinding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_fragment_close_work, container, false);
        View view = dialogFragmentCloseWorkBinding.getRoot();

        buttonsAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.buttons_animation);

        dataBaseHelper = new DataBaseHelper(getContext());
        excelUtils = new ExcelUtils();

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        dataBaseHelper.create_db();
        sqLiteDatabase = dataBaseHelper.open();
        double dayTotalPaymentFromDB = round(dataBaseHelper.getTotalCostOfDayOrders(currentDate), 2);
        dialogFragmentCloseWorkBinding.tvDayTotalValue.setText(String.valueOf(dayTotalPaymentFromDB));

        View.OnClickListener onClickCloseWork = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                Toast.makeText(getContext(), "Смена закрывается", Toast.LENGTH_SHORT).show();
                dialogFragmentCloseWorkBinding.tvCloseWork.setBackgroundResource((R.drawable.active_rounded_menu_buttons));
                double editPayment = 0.00;

                if (dialogFragmentCloseWorkBinding.etEditPaymentValue.getText().length() != 0){
                    editPayment = Double.parseDouble(dialogFragmentCloseWorkBinding.etEditPaymentValue.getText().toString());
                }

                if (dialogFragmentCloseWorkBinding.etPaymentCashValue.getText().toString().length() == 0
                        || dialogFragmentCloseWorkBinding.etPaymentCardValue.getText().toString().length() == 0){
                    Toast.makeText(getContext(), "Не все данные введены", Toast.LENGTH_SHORT).show();
                } else {
                    double cashPayment = Double.parseDouble(dialogFragmentCloseWorkBinding.etPaymentCashValue.getText().toString());
                    double cardPayment = Double.parseDouble(dialogFragmentCloseWorkBinding.etPaymentCardValue.getText().toString());
                    if (round((dayTotalPaymentFromDB + editPayment),2) != round((cashPayment + cardPayment), 2)) {
                        dialogFragmentCloseWorkBinding.tvDayTotalTitle.setBackgroundResource(R.drawable.negative_rounded_menu_buttons);
                        dialogFragmentCloseWorkBinding.tvCloseWork.setBackgroundResource(R.drawable.negative_rounded_menu_buttons);
                        dialogFragmentCloseWorkBinding.etEditPaymentValue.setText(String.valueOf(round((cashPayment + cardPayment) - dayTotalPaymentFromDB, 2)));
                        Toast.makeText(getContext(), "Чё за хуйня?", Toast.LENGTH_SHORT).show();
                    } else {
                        long insert = 0;
                        dataBaseHelper.insertEditMaterialInDataBase(editPayment);
                        insert = dataBaseHelper.insertFullMoneyData(currentDate, dayTotalPaymentFromDB, cashPayment, cardPayment, editPayment);

                        if (insert != 0) {
                            dialogFragmentCloseWorkBinding.tvCloseWork.setBackgroundResource(R.drawable.active_close_order_button);
                            generateReports(getContext());

                            Intent sendDataIntent = new Intent(Intent.ACTION_SEND);
                            sendDataIntent.setType("text/plain");
                            sendDataIntent.putExtra(Intent.EXTRA_TEXT, "Дневной отчет за " + currentDate + ":\n" +
                                    "Всего за день: " + dayTotalPaymentFromDB + " руб.\n" + "Наличными: " + cashPayment + " руб.\n" +
                                    "Картой: " + cardPayment + " руб. \n" +
                                    "Корректировка: " + editPayment);
                            Intent shareIntent = Intent.createChooser(sendDataIntent, null);
                            startActivity(shareIntent);
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Данные не вставлены", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
            }
        };

        dialogFragmentCloseWorkBinding.tvCloseWork.setOnClickListener(onClickCloseWork);

        return view;
    }

    private void generateReports(Context context){
        isServicesReportSuccess = excelUtils.storeExcelInStorage(context, currentDate + ". Услуги. Отчёт за день.xlsx",
                excelUtils.writeServicesReportInExcel(dataBaseHelper.selectAllServiceCount("OrderDate = '" + currentDate + "'")));
        isMaterialsReportSuccess = excelUtils.storeExcelInStorage(context, currentDate + ". Материалы. Отчёт за день.xlsx",
                excelUtils.writeMaterialsReportInExcel(dataBaseHelper.selectAllMaterialsData("OrderDate = '" + currentDate + "'")));
        isFullMoneyGainReportSuccess = excelUtils.storeExcelInStorage(context, currentDate + ". Выручка. Отчёт за день.xlsx",
                excelUtils.writeFullMoneyGainReportInExcel(dataBaseHelper.selectFullMoneyGain("date = '" + currentDate + "'")));
        if (isServicesReportSuccess && isMaterialsReportSuccess && isFullMoneyGainReportSuccess){
            Toast.makeText(getContext(), "Отчёты сгенерированы", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDismiss(@NonNull final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener){
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
