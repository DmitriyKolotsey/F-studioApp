package com.kolotseyd.f_studioapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.kolotseyd.f_studioapp.BroadcastReceiver.BatteryStatusReceiver;
import com.kolotseyd.f_studioapp.Data.DataBaseHelper;
import com.kolotseyd.f_studioapp.Data.MaterialNameListAdapter;
import com.kolotseyd.f_studioapp.Data.OrderData;
import com.kolotseyd.f_studioapp.Data.OrderDataAdapter;
import com.kolotseyd.f_studioapp.Fragments.AddNewMaterialDialogFragment;
import com.kolotseyd.f_studioapp.Fragments.CloseWorkDialogFragment;
import com.kolotseyd.f_studioapp.Fragments.CreateDataBaseBackupDialogFragment;
import com.kolotseyd.f_studioapp.Fragments.EnterPasswordDialogFragment;
import com.kolotseyd.f_studioapp.Fragments.MaterialFormatTypeAndCountFragment;
import com.kolotseyd.f_studioapp.Fragments.OrderWithoutCostDialogFragment;
import com.kolotseyd.f_studioapp.Fragments.OrderWithoutMaterialsDialogFragment;
import com.kolotseyd.f_studioapp.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        DialogInterface.OnDismissListener {

    boolean isOperationClicked = false;

    private static final String APP_PREFERENCES = "settings";
    private static final String APP_PREFERENCES_BACKUP_DATE = "backup_date";
    private static final String DB_NAME = "f_studio_app_database_modern.db";

    SharedPreferences sharedPreferences;

    DataBaseHelper dataBaseHelper;
    BatteryStatusReceiver batteryStatusReceiver;

    ActivityMainBinding binding;
    FragmentManager fragmentManager;

    RecyclerView recyclerViewOrderData, recyclerViewMaterialNames;

    OrderDataAdapter orderDataAdapter;
    MaterialNameListAdapter materialNameListAdapter;

    TextView inputTextView, tvToastText;
    View toastLayout;

    Animation buttonsAnimation;

    double firstValue, secondValue, resultValue;
    String operation;

    ArrayList<OrderData> orderDataArrayList = new ArrayList<>();
    ArrayList<String> materialNamesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getString(APP_PREFERENCES_BACKUP_DATE, "").equals(currentDate)
               || sharedPreferences.getString(APP_PREFERENCES_BACKUP_DATE, "").equals("")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(APP_PREFERENCES_BACKUP_DATE, currentDate);
            editor.apply();
            DialogFragment createDatabaseBackupDialogFragment = new CreateDataBaseBackupDialogFragment();
            createDatabaseBackupDialogFragment.show(getSupportFragmentManager(), "enterPasswordDialog");
        }

        deleteReportsDirectories();

        buttonsAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttons_animation);

        dataBaseHelper = new DataBaseHelper(getApplicationContext());

        batteryStatusReceiver = new BatteryStatusReceiver();

        fragmentManager = getSupportFragmentManager();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        recyclerViewOrderData = findViewById(R.id.rvOrderData);
        recyclerViewMaterialNames = findViewById(R.id.rvMaterialNames);

        orderDataAdapter = new OrderDataAdapter(this, orderDataArrayList);
        recyclerViewOrderData.setAdapter(orderDataAdapter);

        MaterialNameListAdapter.OnMaterialNameSelectListener onMaterialNameSelectListener =
                new MaterialNameListAdapter.OnMaterialNameSelectListener() {
            @Override
            public void onMaterialNameSelected(String materialName, int position, View v) {
                binding.llOrderInfo.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
                inputTextView = binding.tvOrderInfoValue;
                binding.tvOrderInfoName.setText(materialName);
                MaterialFormatTypeAndCountFragment materialFormatTypeAndCountFragment = new MaterialFormatTypeAndCountFragment();
                Bundle bundle = new Bundle();
                bundle.putString("materialName", materialName);

                materialFormatTypeAndCountFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (fragmentManager.getFragments().isEmpty()){
                    fragmentTransaction.add(R.id.flFragmentContainer, materialFormatTypeAndCountFragment);
                } else {
                    fragmentTransaction.replace(R.id.flFragmentContainer, materialFormatTypeAndCountFragment);
                }
                fragmentTransaction.commit();
            }
        };

        materialNamesList = dataBaseHelper.getMaterialNames();
        materialNameListAdapter = new MaterialNameListAdapter(this, materialNamesList, onMaterialNameSelectListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewMaterialNames.setLayoutManager(linearLayoutManager);
        recyclerViewMaterialNames.setAdapter(materialNameListAdapter);

        binding.ivButtonGoToUpdateMaterialDataActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                DialogFragment enterPasswordDialogFragment = new EnterPasswordDialogFragment();
                enterPasswordDialogFragment.show(getSupportFragmentManager(), "enterPasswordDialog");
            }
        });

        binding.ivButtonGoToReportActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
                startActivity(intent);
            }
        });

        binding.ivCloseWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                DialogFragment closeWorkDialogFragment = new CloseWorkDialogFragment();
                Bundle args = new Bundle();
                args.putDouble("totalDaysCost", dataBaseHelper.getTotalCostOfDayOrders(currentDate));
                closeWorkDialogFragment.setArguments(args);
                closeWorkDialogFragment.show(getSupportFragmentManager(), "closeWorkDialog");
            }
        });

        binding.ivAddNewMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                DialogFragment addNewMaterialDialogFragment = new AddNewMaterialDialogFragment();
                addNewMaterialDialogFragment.show(getSupportFragmentManager(), "addNewMaterialDialog");
            }
        });

        binding.tvMemoryValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonsAnimation);
                if (binding.tvMemoryValue.getText().length() == 0 && inputTextView.getText().length() != 0){
                    binding.tvMemoryValue.setText(inputTextView.getText().toString());
                } else if (binding.tvMemoryValue.getText().length() != 0 && inputTextView.getText().length() == 0){
                    inputTextView.setText(binding.tvMemoryValue.getText().toString());
                } else if (inputTextView.getText().length() != 0){
                    showCustomToast("В поле ввода уже есть значение");
                } else {
                    showCustomToast("Нет значения для сохранения или восстановления");
                }
            }
        });

        binding.tvMemoryValue.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (binding.tvMemoryValue.getText().length() != 0){
                    binding.tvMemoryValue.setText("");
                } else {
                    showCustomToast("Нет значения для удаления");
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryStatusReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryStatusReceiver);
    }

    public void updateOrderInfoFormatAndType(String orderFormatAndType){
        binding.tvOrderInfoFormatAndType.setText(orderFormatAndType);
    }

    protected void updateOrderInfoName(String s){
        binding.tvOrderInfoName.setText(s);
    }

    public void clearOrderInfo(){
        clearOrderTextView();
        binding.tvOrderPaymentValue.setText("");
        orderDataArrayList.clear();
    }

    private void clearOrderTextView(){
        String materialName = binding.tvOrderInfoName.getText().toString();
        MaterialFormatTypeAndCountFragment materialFormatTypeAndCountFragment = new MaterialFormatTypeAndCountFragment();
        Bundle bundle = new Bundle();
        bundle.putString("materialName", materialName);

        materialFormatTypeAndCountFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.getFragments().isEmpty()){
            fragmentTransaction.add(R.id.flFragmentContainer, materialFormatTypeAndCountFragment);
        } else {
            fragmentTransaction.replace(R.id.flFragmentContainer, materialFormatTypeAndCountFragment);
        }
        fragmentTransaction.commit();
        binding.tvOrderInfoFormatAndType.setText("");
        binding.tvOrderInfoValue.setText("");
    }

    private void setActiveButtonBackground(View view){
        setDefaultButtonBackground();
        view.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
        binding.tvKeyboardButtonEnter.setBackground(getDrawable(R.drawable.active_close_order_button));
    }

    private void setDefaultButtonBackground(){
        binding.tvCloseOrderButton.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        binding.tvKeyboardButtonEnter.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        binding.tvPaymentTitle.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        binding.llOrderInfo.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
    }

    public void onClickSelectInputTextView(View view) {
        view.startAnimation(buttonsAnimation);
        isOperationClicked = false;
        if (view.getId() == R.id.llOrderInfo || view.getId() == R.id.rvOrderData){
            binding.tvPaymentTitle.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
            binding.llOrderInfo.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
            binding.tvKeyboardButtonEnter.setBackground(getDrawable(R.drawable.active_close_order_button));
            binding.tvCloseOrderButton.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
            inputTextView = binding.tvOrderInfoValue;
        } else if (view.getId()== R.id.tvPaymentTitle || view.getId() == R.id.tvOrderPaymentValue){
            binding.llOrderInfo.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
            binding.tvPaymentTitle.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
            binding.tvCloseOrderButton.setBackground(getDrawable(R.drawable.active_close_order_button));
            binding.tvKeyboardButtonEnter.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
            inputTextView = binding.tvOrderPaymentValue;
        }
    }

    public void onClickKeyBoardButton(View view) {
        view.startAnimation(buttonsAnimation);
        if (inputTextView == null){
            showCustomToast("Поле ввода не выбрано");
        } else {
            if (!isOperationClicked){
                if (((TextView) view).getText().toString().equals(".") && inputTextView.getText().toString().contains(".")){
                    showCustomToast("Невозможно ввести две точки в число");
                } else {
                    inputTextView.setText(inputTextView.getText().toString() + ((TextView) view).getText().toString());
                }
            } else {
                showCustomToast("Ввод цифр недопустим. Только математические операции");
            }
        }
    }

    public void onClickKeyBoardButtonEnter(View view) {
        view.startAnimation(buttonsAnimation);
        if (!checkOrderIsCorrect()){
            showCustomToast("Не все данные о заказе выбраны");
        } else{
            orderDataArrayList.add(new OrderData(binding.tvOrderInfoName.getText().toString(), binding.tvOrderInfoFormatAndType.getText().toString()
                    , binding.tvOrderInfoValue.getText().toString()));
            orderDataAdapter.notifyDataSetChanged();
            clearOrderTextView();
            goToDefault();
            binding.llOrderInfo.setBackground(getDrawable(R.drawable.active_rounded_menu_buttons));
            isOperationClicked = false;
        }
    }

    public void onClickKeyBoardButtonClear(View view) {
        view.startAnimation(buttonsAnimation);
        firstValue = 0;
        secondValue = 0;
        operation = "";
        inputTextView.setText("");
        inputTextView.setHint("");
        isOperationClicked = false;
        binding.tvKeyboardButtonEqual.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
    }

    public void onClickKeyBoardButtonDelete(View view) {
        view.startAnimation(buttonsAnimation);
        if (inputTextView != null){
            if (inputTextView.getText().length() != 0){
                inputTextView.setText(inputTextView.getText().toString().substring(0, inputTextView.getText().length()-1));
            } else if (inputTextView.getText() == null && inputTextView.getHint().length() != 0){
                inputTextView.setText(inputTextView.getHint().toString().substring(0, inputTextView.getHint().length()-1));
                inputTextView.setHint("");
                binding.tvKeyboardButtonEqual.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
                isOperationClicked = false;
            } else if (inputTextView.getText() == null){
                Toast.makeText(this, "Нечего удалять", Toast.LENGTH_SHORT).show();
            }
        } else {
            showCustomToast("Поле ввода не выбрано");
        }

    }

    public void onClickKeyBoardOperation(View view) {
        view.startAnimation(buttonsAnimation);
        binding.tvKeyboardButtonEqual.setBackground(getDrawable(R.drawable.active_close_order_button));
        isOperationClicked = false;
        switch (((TextView) view).getText().toString()){
            case "+":
                if (inputTextView.getText().length() == 0){
                    Toast.makeText(this, "Первое значение не введено", Toast.LENGTH_SHORT).show();
                } else {
                    firstValue = round(Double.parseDouble(inputTextView.getText().toString()), 2);
                    operation = ((TextView) view).getText().toString();
                    inputTextView.setText("");
                    inputTextView.setHint(firstValue + "+");
                    inputTextView.setHintTextColor(getColor(R.color.grey));
                }
                break;
            case "-":
                if (inputTextView.getText().length() == 0){
                    inputTextView.setText("-");
                } else {
                    firstValue = round(Double.parseDouble(inputTextView.getText().toString()), 2);
                    operation = ((TextView) view).getText().toString();
                    inputTextView.setText("");
                    inputTextView.setHint(firstValue + "-");
                    inputTextView.setHintTextColor(getColor(R.color.grey));
                }
                break;
            case "*":
                if (inputTextView.getText().length() == 0){
                    Toast.makeText(this, "Первое значение не введено", Toast.LENGTH_SHORT).show();
                } else {
                    firstValue = round(Double.parseDouble(inputTextView.getText().toString()), 2);
                    operation = ((TextView) view).getText().toString();
                    inputTextView.setText("");
                    inputTextView.setHint(firstValue + "*");
                    inputTextView.setHintTextColor(getColor(R.color.grey));
                }
                break;
        }
    }

    public void onClickKeyBoardOperationEqual(View view) {
        view.startAnimation(buttonsAnimation);
        secondValue = round(Double.parseDouble(inputTextView.getText().toString()), 2);
        isOperationClicked = true;
        binding.tvKeyboardButtonEqual.setBackground(getDrawable(R.drawable.default_rounded_menu_buttons));
        if (operation != null) {
            switch (operation) {
                case "+":
                    resultValue = round(firstValue + secondValue, 2);
                    inputTextView.setText(String.valueOf(resultValue));
                    inputTextView.setHint("");
                    break;
                case "-":
                    resultValue = round(firstValue - secondValue, 2);
                    inputTextView.setText(String.valueOf(resultValue));
                    ;
                    inputTextView.setHint("");
                    break;
                case "*":
                    resultValue = round(firstValue * secondValue, 2);
                    inputTextView.setText(String.valueOf(resultValue));
                    ;
                    inputTextView.setHint("");
                    break;
            }
        } else {
            Toast.makeText(this, "Не выбрана операция", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToDefault(){
        setDefaultButtonBackground();
    }

    public void onClickCloseOrder(View view) {
        view.startAnimation(buttonsAnimation);
        String stringPaymentValue;
        double doublePaymentValue;

        if (orderDataArrayList.size() == 0 && binding.tvOrderPaymentValue.getText().length() == 0){
            showCustomToast("Не введены ни материалы, ни оплата!");
        } else if (orderDataArrayList.size() == 0){
            if (binding.tvOrderPaymentValue.getText().toString().charAt(0) == '.'){
                stringPaymentValue = "0" + binding.tvOrderPaymentValue.getText().toString();
                doublePaymentValue = Double.parseDouble(stringPaymentValue);
            } else {
                doublePaymentValue = Double.parseDouble(binding.tvOrderPaymentValue.getText().toString());
            }
            DialogFragment orderWithoutMaterialsDialogFragment = new OrderWithoutMaterialsDialogFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("orderDataArrayList", orderDataArrayList);
            orderWithoutMaterialsDialogFragment.setArguments(args);
            args.putDouble("orderCost", doublePaymentValue);
            orderWithoutMaterialsDialogFragment.show(getSupportFragmentManager(), "enterPasswordDialog");
        } else if (binding.tvOrderPaymentValue.getText().length() == 0) {
            DialogFragment orderWithoutCostDialogFragment = new OrderWithoutCostDialogFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("orderDataArrayList", orderDataArrayList);
            orderWithoutCostDialogFragment.setArguments(args);
            orderWithoutCostDialogFragment.show(getSupportFragmentManager(), "enterPasswordDialog");
        } else {
            if (binding.tvOrderPaymentValue.getText().toString().charAt(0) == '.'){
                stringPaymentValue = "0" + binding.tvOrderPaymentValue.getText().toString();
                doublePaymentValue = Double.parseDouble(stringPaymentValue);
            } else {
                doublePaymentValue = Double.parseDouble(binding.tvOrderPaymentValue.getText().toString());
            }

            long insert = dataBaseHelper.insertFullOrderInDataBase(orderDataArrayList, doublePaymentValue);

            orderDataArrayList.clear();
            orderDataAdapter.notifyDataSetChanged();
            binding.tvOrderPaymentValue.setText("");
            clearOrderInfo();
            goToDefault();

            Toast.makeText(this, "Заказ закрыт и сохранен в БД. Заказов в БД: " + insert, Toast.LENGTH_SHORT).show();

        }
    }

    public void showCustomToast(String textForToast){
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

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Log.d("TAG", "dialog dismiss");
        goToDefault();
        binding.tvOrderPaymentValue.setText("");
        clearOrderInfo();
        orderDataArrayList.clear();
        orderDataAdapter.notifyDataSetChanged();
        materialNamesList = dataBaseHelper.getMaterialNames();
        materialNameListAdapter.notifyDataSetChanged();
    }

    private boolean checkOrderIsCorrect(){
        if (binding.tvOrderInfoName.getText().length() != 0 && binding.tvOrderInfoFormatAndType.getText().length() != 0
        && binding.tvOrderInfoValue.getText().length() !=0 && !binding.tvOrderInfoFormatAndType.getText().toString().equals("Без расхода материалов")){
            return true;
        } else if (binding.tvOrderInfoName.getText().length() != 0 && binding.tvOrderInfoFormatAndType.getText().length() != 0
                 && binding.tvOrderInfoFormatAndType.getText().toString().equals("Без расхода материалов")){
            binding.tvOrderInfoFormatAndType.setText(0);
            return true;
        } else return false;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent intent = result.getData();
            Uri uri = intent.getData();
            String [] pathsections = intent.getData().getPath().split(":");
            String path = Environment.getExternalStorageDirectory().getPath() + "/" + pathsections[pathsections.length - 1];
            Log.i("TAG", path);

            //File fileFromUri = new File("/storage/emulated/0/Backup/f_studio_app_database_modern.db");
            File fileFromUri = new File(findFullPath(uri.getPath()));
            File defaultFile = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), "База данных");

            try {
                copyDirectoryOneLocationToAnotherLocation(defaultFile, fileFromUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public static String findFullPath(String path) {
        String actualResult="";
        path=path.substring(5);
        int index=0;
        StringBuilder result = new StringBuilder("/storage");
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) != ':') {
                result.append(path.charAt(i));
            } else {
                index = ++i;
                result.append('/');
                break;
            }
        }
        for (int i = index; i < path.length(); i++) {
            result.append(path.charAt(i));
        }
        if (result.substring(9, 16).equalsIgnoreCase("primary")) {
            actualResult = result.substring(0, 8) + "/emulated/0/" + result.substring(17);
        } else {
            actualResult = result.toString();
        }
        return actualResult;
    }

    public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdirs();
            }

            Log.d("TAG", "source location " + sourceLocation.getAbsolutePath());
            Log.d("TAG", "target location " + targetLocation.getAbsolutePath());

            String[] children = sourceLocation.list();
            for (File f : sourceLocation.listFiles()) {
                if (f.isFile()){
                    File targetFile = new File(targetLocation, f.getName());
                    Log.d("TAG", f.getAbsolutePath());
                    Log.d("TAG", targetFile.getAbsolutePath());
                    targetFile.createNewFile();
                    copyDirectoryOneLocationToAnotherLocation(f, targetFile);
                }
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        }
    }

    private void deleteReportsDirectories(){
        File file = new File(String.valueOf(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)));
        String firstFileName = " Материалы";
        String secondFileName = " Услуги";
        String thirdFileName = " Выручка";

        File firstFile = new File(file.getAbsolutePath(), firstFileName);
        File secondFile = new File(file.getAbsolutePath(), secondFileName);
        File thirdFile = new File(file.getAbsolutePath(), thirdFileName);

        if (firstFile.exists()){
            Log.d("TAG", "first file exist");
            if (firstFile.isDirectory()) {
                String[] children = firstFile.list();
                for (String child : children) {
                    new File(firstFile, child).delete();
                }
            }
            firstFile.delete();
        }

        if (secondFile.exists()){
            Log.d("TAG", "second file exist");
            if (secondFile.isDirectory()) {
                String[] children = secondFile.list();
                for (String child : children) {
                    new File(secondFile, child).delete();
                }
            }
            secondFile.delete();
        }

        if (thirdFile.exists()){
            Log.d("TAG", "third file exist");
            if (thirdFile.isDirectory()) {
                String[] children = thirdFile.list();
                for (String child : children) {
                    new File(thirdFile, child).delete();
                }
            }
            thirdFile.delete();
        }
    }
}