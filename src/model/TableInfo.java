/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class TableInfo {
    public String tableName;
    public String title;
    public String description;
    public ArrayList<ColumnProperty> columns;

    public TableInfo(String exccelFile) throws IOException {
        int n_mst = 0;
        int n_department = 0;
        FileInputStream inputStream = new FileInputStream(exccelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        inputStream.close();
        XSSFSheet sheet = workbook.getSheetAt(0);
        tableName = sheet.getSheetName();
        title = sheet.getRow(1).getCell(2).toString();
        description = sheet.getRow(2).getCell(2).toString();
        Iterator<Row> rowIterator = sheet.iterator();
        Row row = rowIterator.next();
        columns = new ArrayList<>();
        while (rowIterator.hasNext()){
            if(row.getRowNum() >= 5 && !row.getCell(1).toString().equals("")){
                ColumnProperty colProp = new ColumnProperty();
                colProp.setColName(row.getCell(1).toString().trim());
                colProp.setColType(row.getCell(3).toString().trim());
                colProp.setColDescription(row.getCell(2).toString().trim());
                if(row.getCell(4).toString().equals("")){
                    colProp.setLengthFormat(500000);
                }
                else{
                    try{
                        colProp.setLengthFormat((int)Double.parseDouble(row.getCell(4).toString()));
                    }
                    catch (NumberFormatException ex){
                        System.out.println(ex.getMessage() + " row " + row.getRowNum());
                        colProp.setLengthFormat(500000);
                    }
                }
                colProp.setFKTable(row.getCell(5).toString().trim());
                if(!colProp.getFKTable().equals("")){
                    if(row.getCell(5).toString().startsWith("mst")){
                        colProp.setJoinTableName("mst" + ++n_mst);
                    }
                    else if(row.getCell(5).toString().startsWith("department")){
                        colProp.setJoinTableName("d" + ++n_department);
                    }
                    colProp.setJoinConstraint(row.getCell(6).toString());;
                    colProp.setJoinField(row.getCell(7).toString());
                    colProp.setSelectField(row.getCell(8).toString());
                }
                if(row.getCell(11).toString().equals("")){
                    colProp.setValidate(false);
                }
                else{
                    colProp.setValidate(true);
                }
                colProp.setValidateFormat(row.getCell(12).toString());
                colProp.setInputType(row.getCell(14).toString());
                if(!row.getCell(15).equals("")){
                    colProp.setComboboxBuildPath(row.getCell(15).toString());
                    colProp.setComboboxName(row.getCell(16).toString());
                    colProp.setComboboxValue(row.getCell(17).toString());
                }
                if(row.getCell(10).equals("")){
                    colProp.setShow(false);
                }
                else{
                    colProp.setShow(true);
                }
                colProp.setValidateMessage(row.getCell(13).toString());
                columns.add(colProp);
            }
            row = rowIterator.next();
        }
    }
}


