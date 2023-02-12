package com.kolotseyd.f_studioapp.Data;

public class MaterialData {

    private String materialName;
    private String materialFormatAndType;
    private String materialCurrentValue;
    private String materialCurrentCost;

    public MaterialData(String materialName, String materialFormatAndType, String materialCurrentValue, String materialCurrentCost) {
        this.materialName = materialName;
        this.materialFormatAndType = materialFormatAndType;
        this.materialCurrentValue = materialCurrentValue;
        this.materialCurrentCost = materialCurrentCost;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialFormatAndType() {
        return materialFormatAndType;
    }

    public void setMaterialFormatAndType(String materialFormatAndType) {
        this.materialFormatAndType = materialFormatAndType;
    }

    public String getMaterialCurrentValue() {
        return materialCurrentValue;
    }

    public void setMaterialCurrentValue(String materialCurrentValue) {
        this.materialCurrentValue = materialCurrentValue;
    }

    public String getMaterialCurrentCost() {
        return materialCurrentCost;
    }

    public void setMaterialCurrentCost(String materialCurrentCost) {
        this.materialCurrentCost = materialCurrentCost;
    }
}
