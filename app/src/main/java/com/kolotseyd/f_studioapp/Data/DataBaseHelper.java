package com.kolotseyd.f_studioapp.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "f_studio_app_database_modern.db";
    private static String DB_PATH;
    public static final int DB_VERSION = 1;

    private Context myContext;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext=context;

        File sdCard = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(sdCard.getAbsolutePath() + "/База данных/");
        dir.mkdirs();
        File file = new File(dir, DB_NAME);
        DB_PATH = file.getAbsolutePath();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String getDbName(){
        return DB_NAME;
    }

    public String getDbPath(){
        Log.d("TAG", DB_PATH);
        return DB_PATH;
    }

    public void create_db(){
        File file = new File(DB_PATH);
        if (!file.exists()) {
            try(InputStream myInput = myContext.getAssets().open(DB_NAME);
                OutputStream myOutput = new FileOutputStream(DB_PATH)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            }
            catch(IOException ex){
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }

    public SQLiteDatabase open()throws SQLException {
        return SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
    }

    public long insertFullOrderInDataBase(ArrayList<OrderData> orderDataArrayList, double orderCost){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Log.d("TAG", currentDate);
        long insert = 0;
        int groupId = getOrderGroupId();
        for (int i = 0; i <= orderDataArrayList.size() - 1; i++){
            int materialId = getMaterialId(orderDataArrayList.get(i).getName(), orderDataArrayList.get(i).getFormatAndType());
            int orderId = getOrderId();
            String insertSQL = "insert into [order] values ("+ orderId + ", " + groupId + ", '" + currentDate + "', " + materialId + ", "
                    + Integer.parseInt(orderDataArrayList.get(i).getValue()) + ")";
            sqLiteDatabase.execSQL(insertSQL);
            if (materialId == 86){
                materialId = 28;
            }
            if (materialId == 87){
                materialId = 27;
            }
            updateMaterialsCount(sqLiteDatabase, materialId, Integer.parseInt(orderDataArrayList.get(i).getValue()));
        }
        insertOrderCost(sqLiteDatabase, groupId, orderCost);

        Cursor cursor = sqLiteDatabase.rawQuery("select count(Id) from [order]", null);
        cursor.moveToFirst();
        insert = cursor.getInt(0);
        return insert;

    }

    public long insertNoMaterialsOrderInDataBase(double orderCost){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        long insert = 0;
            int groupId = getOrderGroupId();
            int materialId = 0;
            int materialCount = 0;
            int orderId = getOrderId();
            String insertSQL = "insert into [order] values ("+ orderId + ", " + groupId + ", '" + currentDate + "', " + materialId + ", "
                    + materialCount + ")";
            sqLiteDatabase.execSQL(insertSQL);
            insertOrderCost(sqLiteDatabase, groupId, orderCost);


        Cursor cursor = sqLiteDatabase.rawQuery("select count(Id) from [order]", null);
        cursor.moveToFirst();
        insert = cursor.getInt(0);
        return insert;
    }

    public long insertEditMaterialInDataBase(double orderCost){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        long insert = 0;
        int groupId = getOrderGroupId();
        int materialId = 85;
        int materialCount = 0;
        int orderId = getOrderId();
        String insertSQL = "insert into [order] values ("+ orderId + ", " + groupId + ", '" + currentDate + "', " + materialId + ", "
                + materialCount + ")";
        sqLiteDatabase.execSQL(insertSQL);
        insertOrderCost(sqLiteDatabase, groupId, orderCost);

        Cursor cursor = sqLiteDatabase.rawQuery("select count(Id) from [order]", null);
        cursor.moveToFirst();
        insert = cursor.getInt(0);
        return insert;
    }

    public long insertNoCostOrderInDataBase(ArrayList<OrderData> orderDataArrayList){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        double orderCost = 0;
        long insert = 0;
        int groupId = getOrderGroupId();
        for (int i = 0; i <= orderDataArrayList.size() - 1; i++){
            int materialId = getMaterialId(orderDataArrayList.get(i).getName(), orderDataArrayList.get(i).getFormatAndType());
            int orderId = getOrderId();
            String insertSQL = "insert into [order] values ("+ orderId + ", " + groupId + ", '" + currentDate + "', " + materialId + ", "
                    + Integer.parseInt(orderDataArrayList.get(i).getValue()) + ")";
            sqLiteDatabase.execSQL(insertSQL);
            if (materialId == 86){
                materialId = 28;
            }
            if (materialId == 87){
                materialId = 27;
            }
            updateMaterialsCount(sqLiteDatabase, materialId, Integer.parseInt(orderDataArrayList.get(i).getValue()));
        }
        insertOrderCost(sqLiteDatabase, groupId, orderCost);

        Cursor cursor = sqLiteDatabase.rawQuery("select count(Id) from [order]", null);
        cursor.moveToFirst();
        insert = cursor.getInt(0);
        return insert;
    }

    private int getOrderGroupId(){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT MAX(OrderGroupId) FROM order_cost", null);
        cursor.moveToFirst();
        if (cursor.isNull(0)){
            return 1;
        } else {
            return cursor.getInt(0) + 1;
        }
    }

    private int getOrderId(){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT MAX(Id) FROM [order]", null);
        cursor.moveToFirst();
        if (cursor.isNull(0)){
            return 1;
        } else {
            return cursor.getInt(0) + 1;
        }
    }

    private int getMaterialId(String materialName, String materialFormatAndType){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();
        Log.d("TAG", materialName);
        Log.d("TAG", materialFormatAndType);


        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Id FROM materials WHERE MaterialName = '" + materialName + "' and MaterialFormatAndType = '"
                + materialFormatAndType + "'", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void insertOrderCost(SQLiteDatabase sqLiteDatabase, int groupId, double value){
        String insertSQL = "insert into [order_cost] values (" + groupId + ", " + value + ")";
        sqLiteDatabase.execSQL(insertSQL);
    }

    private void updateMaterialsCount(SQLiteDatabase sqLiteDatabase, int id, int value){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT MaterialTotalValue FROM materials WHERE id = " + id, null);
        cursor.moveToFirst();
        int currentValue = cursor.getInt(0);
        int newValue = currentValue - value;
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("MaterialTotalValue", newValue);
        sqLiteDatabase.update("materials", cv, "id = " + id, null);
    }

    public void deleteMaterial(String materialName){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("IsMaterialDeleted", 1);
        sqLiteDatabase.update("materials", cv, "MaterialName = '" + materialName + "'", null);
    }

    public void deleteMaterialFormat(String materialName, String materialFormatAndType){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("IsMaterialFormatDeleted", 1);
        sqLiteDatabase.update("materials", cv,
                "MaterialName = '" + materialName + "' and MaterialFormatAndType = '" + materialFormatAndType + "'", null);
    }

    public double getTotalCostOfDayOrders(String currentDate){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();
        Cursor cursor = sqLiteDatabase.rawQuery("select sum(OrderCost) from order_cost " +
                "where OrderGroupId in (select OrderGroupId from [order] where OrderDate = '" + currentDate + "')", null);
        cursor.moveToFirst();
        return cursor.getDouble(0);
    }

    public ArrayList<ServicesReportData> selectAllServiceCount(String whereCondition){
        ArrayList<ServicesReportData> servicesReportDataArrayList = new ArrayList<>();
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();
        Cursor cursor = sqLiteDatabase
                .rawQuery("select MaterialName, count(materialId) from [order] inner join materials on materials.id = MaterialId " +
                        "where " + whereCondition + " group by MaterialName order by MaterialId", null);
        if (cursor.moveToFirst()){
            do {
                servicesReportDataArrayList.add(new ServicesReportData(cursor.getString(0), cursor.getInt(1)));
            } while (cursor.moveToNext());
        }
        return servicesReportDataArrayList;
    }

    public ArrayList<FullMoneyGainReportData> selectFullMoneyGain(String whereCondition){
        ArrayList<FullMoneyGainReportData> fullMoneyGainReportData = new ArrayList<>();
        create_db();
        SQLiteDatabase sqLiteDatabase = open();

        Cursor cursor = sqLiteDatabase
                .rawQuery("select * from [money_gain] where " + whereCondition, null);
        if (cursor.moveToFirst()){
            do {
                fullMoneyGainReportData.add(new FullMoneyGainReportData(cursor.getString(0), cursor.getDouble(1), cursor.getDouble(2),
                        cursor.getDouble(3), cursor.getDouble(4)));
            } while (cursor.moveToNext());
        }
        return fullMoneyGainReportData;
    }

    public ArrayList<MaterialData> selectAllMaterialsData(String whereCondition){
        ArrayList<MaterialData> materialDataArrayList = new ArrayList<>();
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();
        Cursor cursor = sqLiteDatabase
                .rawQuery("select MaterialName, MaterialFormatAndType, sum(MaterialCount), MaterialCost from materials " +
                        "inner join [order] on materials.Id = [order].MaterialId where " + whereCondition + " group by " +
                        "MaterialName, MaterialFormatAndType, MaterialCost", null);
        if (cursor.moveToFirst()){
            do {
                materialDataArrayList.add(new MaterialData(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        return materialDataArrayList;
    }

    public int updateFullMaterialValue(ContentValues cv, String materialName, String materialFormatAndType){
        create_db();
        SQLiteDatabase sqLiteDatabase  = open();

        return sqLiteDatabase.update("materials", cv, "MaterialName = '" + materialName
                + "' and MaterialFormatAndType = '" + materialFormatAndType + "'", null);

    }

    public long insertFullMoneyData(String date, double totalMoney, double cashMoney, double cardMoney, double editMoney){
        SQLiteDatabase sqLiteDatabase = open();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from [money_gain] where date = '" + date + "'", null);
        long result = 0;
        if (cursor.getCount() != 0){
            Log.d("TAG", "money_gain update");
            ContentValues cv = new ContentValues();
            cv.clear();
            cv.put("totalMoney", totalMoney);
            cv.put("cashMoney", cashMoney);
            cv.put("cardMoney", cardMoney);
            cv.put("editMoney", editMoney);
            result = sqLiteDatabase.update("money_gain", cv, "date = '" + date + "'", null);
            String updateSQL = "update [money_gain] set totalMoney = " + totalMoney + ", cashMoney = " + cashMoney + ", cardMoney = " + cardMoney +
                    ", editMoney = " + editMoney + " where date = '" + date + "'";
            Log.d("TAG", updateSQL);
            sqLiteDatabase.execSQL(updateSQL);
        } else {
            ContentValues cv = new ContentValues();
            cv.clear();
            cv.put("date", date);
            cv.put("totalMoney", totalMoney);
            cv.put("cashMoney", cashMoney);
            cv.put("cardMoney", cardMoney);
            cv.put("editMoney", editMoney);
            result = sqLiteDatabase.insert("money_gain", null, cv);
        }
        return result;
    }

    public ArrayList<String> getMaterialNames(){
        ArrayList<String> materialNames = new ArrayList<>();
        create_db();
        SQLiteDatabase sqLiteDatabase = open();

        Cursor cursor = sqLiteDatabase.rawQuery("select distinct MaterialName from [materials] where MaterialName <> 'Корректировка' and  IsMaterialDeleted = 0 " +
                "order by Id", null);

        if (cursor.moveToFirst()){
            do {
                materialNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return materialNames;
    }

    public ArrayList<MaterialFormatTypeAndCountData> getMaterialFormatTypeAndCount(String materialName){
        ArrayList<MaterialFormatTypeAndCountData> materialFormatTypeAndCountDataArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = open();
        Cursor cursor;
        if (materialName.equals("Срочное фото")){
            cursor = sqLiteDatabase.rawQuery("select MaterialFormatAndType, MaterialTotalValue" +
                    " from materials where MaterialName = 'Печать фото' and (MaterialFormatAndType = '10x15 Глянец' or MaterialFormatAndType = '10x15 Матовая') " +
                    "and IsMaterialFormatDeleted = 0", null);
        } else{
             cursor = sqLiteDatabase.rawQuery("select MaterialFormatAndType, MaterialTotalValue" +
                    " from materials where MaterialName = '" + materialName + "' and IsMaterialFormatDeleted = 0", null);
        }

        if (cursor.moveToFirst()){
            do {
                materialFormatTypeAndCountDataArrayList.add(new MaterialFormatTypeAndCountData(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        if (materialName.equals("Срочное фото")){
            materialFormatTypeAndCountDataArrayList
                    .add(new MaterialFormatTypeAndCountData("Без расхода материалов", "0"));
        }
        return materialFormatTypeAndCountDataArrayList;
    }

    public long insertNewMaterialInDatabase(ContentValues values){
        SQLiteDatabase sqLiteDatabase = open();
        return sqLiteDatabase.insert("materials", null, values);
    }

    public ArrayList<MaterialData> getMaterialsCurrentCount(){
        ArrayList<MaterialData> materialDataArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = open();

        Cursor cursor = sqLiteDatabase.rawQuery("select MaterialName, MaterialFormatAndType, MaterialTotalValue, MaterialCost" +
                " from materials where IsMaterialDeleted = 0 and IsMaterialFormatDeleted = 0 and MaterialTotalValue is not null", null);

        if (cursor.moveToFirst()){
            do {
                materialDataArrayList.add(new MaterialData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        return materialDataArrayList;
    }
}



