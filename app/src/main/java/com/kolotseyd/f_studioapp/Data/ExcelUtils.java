package com.kolotseyd.f_studioapp.Data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ExcelUtils {

    public Workbook writeServicesReportInExcel(ArrayList<ServicesReportData> servicesReportDataArrayList){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet;
        Cell cell;
        String EXCEL_SHEET_NAME = "Sheet1";
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Log.d("TAG", "Write service in excel");

        for (int i = 0; i < servicesReportDataArrayList.size(); i++){
            Row titlesRow = sheet.createRow(0);
            cell = titlesRow.createCell(0);
            cell.setCellValue("Наименование услуги");
            cell = titlesRow.createCell(1);
            cell.setCellValue("Количество");

            Row rowData = sheet.createRow(i+1);

            cell = rowData.createCell(0);
            cell.setCellValue(servicesReportDataArrayList.get(i).getName());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(1);
            cell.setCellValue(servicesReportDataArrayList.get(i).getCount());
            cell.setCellStyle(cellStyle);
        }

        return workbook;
    }

    public Workbook writeMaterialsReportInExcel(ArrayList<MaterialData> materialDataArrayList){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet;
        Cell cell;
        double sumTotalMaterialCost = 0;
        String EXCEL_SHEET_NAME = "Sheet1";
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < materialDataArrayList.size(); i++){
            Row titlesRow = sheet.createRow(0);
            cell = titlesRow.createCell(0);
            cell.setCellValue("Наименование материала");
            cell = titlesRow.createCell(1);
            cell.setCellValue("Тип и формат материала");
            cell = titlesRow.createCell(2);
            cell.setCellValue("Израсходовано");
            cell = titlesRow.createCell(3);
            cell.setCellValue("Стоимость");

            Row rowData = sheet.createRow(i+1);

            cell = rowData.createCell(0);
            cell.setCellValue(materialDataArrayList.get(i).getMaterialName());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(1);
            cell.setCellValue(materialDataArrayList.get(i).getMaterialFormatAndType());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(2);
            cell.setCellValue(materialDataArrayList.get(i).getMaterialCurrentValue());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(3);
            cell.setCellValue(Double.parseDouble(materialDataArrayList.get(i).getMaterialCurrentCost())
                    * Double.parseDouble(materialDataArrayList.get(i).getMaterialCurrentValue()));
            cell.setCellStyle(cellStyle);
        }

        for (int i = 0; i < materialDataArrayList.size(); i++){
            sumTotalMaterialCost = sumTotalMaterialCost + Double.parseDouble(materialDataArrayList.get(i).getMaterialCurrentCost());
        }

        Row sumRow = sheet.createRow(sheet.getLastRowNum() + 1);
        cell = sumRow.createCell(0);
        cell.setCellValue("Итого");
        cell.setCellStyle(cellStyle);

        cell = sumRow.createCell(3);
        cell.setCellValue(sumTotalMaterialCost);
        cell.setCellStyle(cellStyle);

        return workbook;
    }

    public Workbook writeFullMoneyGainReportInExcel(ArrayList<FullMoneyGainReportData> fullMoneyGainReportData){
        double sumTotalMoney = 0;
        double sumCashMoney = 0;
        double sumCardMoney = 0;
        double sumEditMoney = 0;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet;
        Cell cell;
        String EXCEL_SHEET_NAME = "Sheet1";
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < fullMoneyGainReportData.size(); i++){
            Row titlesRow = sheet.createRow(0);
            cell = titlesRow.createCell(0);
            cell.setCellValue("Дата");
            cell = titlesRow.createCell(1);
            cell.setCellValue("Выручка всего");
            cell = titlesRow.createCell(2);
            cell.setCellValue("Наличными");
            cell = titlesRow.createCell(3);
            cell.setCellValue("Картой");
            cell = titlesRow.createCell(4);
            cell.setCellValue("Корректировка");

            Row rowData = sheet.createRow(i+1);

            cell = rowData.createCell(0);
            cell.setCellValue(fullMoneyGainReportData.get(i).getDate());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(1);
            cell.setCellValue(fullMoneyGainReportData.get(i).getTotalMoney());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(2);
            cell.setCellValue(fullMoneyGainReportData.get(i).getCashMoney());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(3);
            cell.setCellValue(fullMoneyGainReportData.get(i).getCardMoney());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(4);
            cell.setCellValue(fullMoneyGainReportData.get(i).getEditMoney());
            cell.setCellStyle(cellStyle);
        }

        for (int i = 0; i < fullMoneyGainReportData.size(); i++){
            sumTotalMoney = sumTotalMoney + fullMoneyGainReportData.get(i).getTotalMoney();
            sumCashMoney = sumCashMoney + fullMoneyGainReportData.get(i).getCashMoney();
            sumCardMoney = sumCardMoney + fullMoneyGainReportData.get(i).getCardMoney();
            sumEditMoney = sumEditMoney + fullMoneyGainReportData.get(i).getEditMoney();
        }

        Row sumRow = sheet.createRow(sheet.getLastRowNum() + 1);
        cell = sumRow.createCell(0);
        cell.setCellValue("Итого");
        cell.setCellStyle(cellStyle);

        cell = sumRow.createCell(1);
        cell.setCellValue(sumTotalMoney);
        cell.setCellStyle(cellStyle);

        cell = sumRow.createCell(2);
        cell.setCellValue(sumCashMoney);
        cell.setCellStyle(cellStyle);

        cell = sumRow.createCell(3);
        cell.setCellValue(sumCardMoney);
        cell.setCellStyle(cellStyle);

        cell = sumRow.createCell(4);
        cell.setCellValue(sumEditMoney);
        cell.setCellStyle(cellStyle);

        return workbook;
    }

    public Workbook writeActualMaterialCount(ArrayList<MaterialData> materialDataArrayList){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet;
        Cell cell;
        double sumTotalMaterialCost = 0;
        String EXCEL_SHEET_NAME = "Sheet1";
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < materialDataArrayList.size(); i++){
            Row titlesRow = sheet.createRow(0);
            cell = titlesRow.createCell(0);
            cell.setCellValue("Наименование материала");
            cell = titlesRow.createCell(1);
            cell.setCellValue("Тип и формат материала");
            cell = titlesRow.createCell(2);
            cell.setCellValue("Актуальный остаток");
            cell = titlesRow.createCell(3);
            cell.setCellValue("Стоимость");

            Row rowData = sheet.createRow(i+1);

            cell = rowData.createCell(0);
            cell.setCellValue(materialDataArrayList.get(i).getMaterialName());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(1);
            cell.setCellValue(materialDataArrayList.get(i).getMaterialFormatAndType());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(2);
            cell.setCellValue(materialDataArrayList.get(i).getMaterialCurrentValue());
            cell.setCellStyle(cellStyle);

            cell = rowData.createCell(3);
            cell.setCellValue(Double.parseDouble(materialDataArrayList.get(i).getMaterialCurrentCost())
                    * Double.parseDouble(materialDataArrayList.get(i).getMaterialCurrentValue()));
            cell.setCellStyle(cellStyle);
        }

        for (int i = 0; i < materialDataArrayList.size(); i++){
            sumTotalMaterialCost = sumTotalMaterialCost + Double.parseDouble(materialDataArrayList.get(i).getMaterialCurrentCost());
        }

        Row sumRow = sheet.createRow(sheet.getLastRowNum() + 1);
        cell = sumRow.createCell(0);
        cell.setCellValue("Итого");
        cell.setCellStyle(cellStyle);

        cell = sumRow.createCell(3);
        cell.setCellValue(sumTotalMaterialCost);
        cell.setCellStyle(cellStyle);

        return workbook;
    }

    public boolean storeExcelInStorage(Context context, String fileName, Workbook workbook) {
        boolean isSuccess;
        Log.d("TAG", "Store service-excel in storage");
        String[] fn = fileName.split("\\.");
        String directoryName = fn[1];
        File sdCard = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(sdCard.getAbsolutePath() + "/" + directoryName);
        dir.mkdirs();
        File file = new File(dir, fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e("TAG", "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e("TAG", "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e("TAG", "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return isSuccess;
    }
}
