package com.example.moija;

class BusInfo {
    String busNo;
    String startName;
    double startX;
    double startY;
    String endName;
    double endX;
    double endY;

    public BusInfo(String busNo, String startName, double startX, double startY, String endName, double endX, double endY) {
        this.busNo = busNo;
        this.startName = startName;
        this.startX = startX;
        this.startY = startY;
        this.endName = endName;
        this.endX = endX;
        this.endY = endY;
    }

    // setter
    public void setBusNo(String busNo){
        this.busNo = busNo;
    }
    public void setStartName(String startName) {
        this.startName = startName;
    }
    public void setEndName(String endName){
        this.endName = endName;
    }

    //getter
    public String getBusNo(){
        return busNo;
    }
    public String getStartName(){
        return startName;
    }
    public String getEndName(){
        return endName;
    }


}