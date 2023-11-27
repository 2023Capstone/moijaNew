package com.example.moija.data;

public class PointerData {
    //api를 호출할떄 사용하는 데이터
    String startName;
    String endName;
    double startPointerX;
    double startPointerY;
    double endPointerX;
    double endPointerY;

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public double getStartPointerX() {
        return startPointerX;
    }

    public void setStartPointerX(double startPointerX) {
        this.startPointerX = startPointerX;
    }

    public double getStartPointerY() {
        return startPointerY;
    }

    public void setStartPointerY(double startPointerY) {
        this.startPointerY = startPointerY;
    }

    public double getEndPointerX() {
        return endPointerX;
    }

    public void setEndPointerX(double endPointerX) {
        this.endPointerX = endPointerX;
    }

    public double getEndPointerY() {
        return endPointerY;
    }

    public void setEndPointerY(double endPointerY) {
        this.endPointerY = endPointerY;
    }
}
