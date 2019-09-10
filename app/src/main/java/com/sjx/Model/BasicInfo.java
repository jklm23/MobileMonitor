package com.sjx.Model;

public class BasicInfo {
    private String brand;//品牌
    private String model;//型号
    private String IMEI;//IMEI
    private String running_APP;//正在运行的应用
    private String memory;//总内存
    private String a_memory;//可用内存
    private String currentLevel;//当前电量
    private String location;//地址
    public BasicInfo(){
        brand="未知";
        model="未知";
        IMEI="未知或无权限查看";
        running_APP="未知";
        memory="未知";
        a_memory="未知";
        currentLevel="0";
        location="未知或无权限查看";
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getRunning_APP() {
        return running_APP;
    }

    public void setRunning_APP(String running_APP) {
        this.running_APP = running_APP;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getA_memory() {
        return a_memory;
    }

    public void setA_memory(String a_memory) {
        this.a_memory = a_memory;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
