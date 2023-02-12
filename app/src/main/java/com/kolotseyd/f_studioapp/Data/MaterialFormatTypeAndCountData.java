package com.kolotseyd.f_studioapp.Data;

public class MaterialFormatTypeAndCountData {
    private String materialFormatAndType;
    private String materialCount;

    public MaterialFormatTypeAndCountData(String materialFormatAndType, String materialCount) {
        this.materialFormatAndType = materialFormatAndType;
        this.materialCount = materialCount;
    }

    public String getMaterialFormatAndType() {
        return materialFormatAndType;
    }

    public void setMaterialFormatAndType(String materialFormatAndType) {
        this.materialFormatAndType = materialFormatAndType;
    }

    public String getMaterialCount() {
        return materialCount;
    }

    public void setMaterialCount(String materialCount) {
        this.materialCount = materialCount;
    }
}
