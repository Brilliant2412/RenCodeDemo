package model;

public class ColumnProperty {
    private String colName;
    private String colType;
    private String colDescription;
    private int lengthFormat;
    private String FKTable;
    private String joinTableName;
    private String joinConstraint;
    private String joinField;
    private String selectField;
    private boolean validate;
    private String validateFormat;
    private String inputType;
    private String comboboxBuildPath;
    private String comboboxName;
    private String comboboxValue;
    private boolean show;
    private String validateMessage;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public String getColDescription() {
        return colDescription;
    }

    public void setColDescription(String colDescription) {
        this.colDescription = colDescription;
    }

    public int getLengthFormat() {
        return lengthFormat;
    }

    public void setLengthFormat(int lengthFormat) {
        this.lengthFormat = lengthFormat;
    }

    public String getFKTable() {
        return FKTable;
    }

    public void setFKTable(String FKTable) {
        this.FKTable = FKTable;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public void setJoinTableName(String joinTableName) {
        this.joinTableName = joinTableName;
    }

    public String getJoinConstraint() {
        return joinConstraint;
    }

    public void setJoinConstraint(String joinConstraint) {
        this.joinConstraint = joinConstraint;
    }

    public String getJoinField() {
        return joinField;
    }

    public void setJoinField(String joinField) {
        this.joinField = joinField;
    }

    public String getSelectField() {
        return selectField;
    }

    public void setSelectField(String selectField) {
        this.selectField = selectField;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getValidateFormat() {
        return validateFormat;
    }

    public void setValidateFormat(String validateFormat) {
        this.validateFormat = validateFormat;
    }

    public String getComboboxBuildPath() {
        return comboboxBuildPath;
    }

    public void setComboboxBuildPath(String comboboxBuildPath) {
        this.comboboxBuildPath = comboboxBuildPath;
    }

    public String getComboboxName() {
        return comboboxName;
    }

    public void setComboboxName(String comboboxName) {
        this.comboboxName = comboboxName;
    }

    public String getComboboxValue() {
        return comboboxValue;
    }

    public void setComboboxValue(String comboboxValue) {
        this.comboboxValue = comboboxValue;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getValidateMessage() {
        return validateMessage;
    }

    public void setValidateMessage(String validateMessage) {
        this.validateMessage = validateMessage;
    }
}

