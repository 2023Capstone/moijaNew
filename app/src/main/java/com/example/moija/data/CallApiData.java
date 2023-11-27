package com.example.moija.data;

public class CallApiData {
    double startPointX;
    double startPointY;
    double endPointX;
    double endPointY;
    String startName;
    String endName;

    int totalTime;

    public double getStartPointX() {
        return startPointX;
    }

    public void setStartPointX(double startPointX) {
        this.startPointX = startPointX;
    }

    public double getStartPointY() {
        return startPointY;
    }

    public void setStartPointY(double startPointY) {
        this.startPointY = startPointY;
    }

    public double getEndPointX() {
        return endPointX;
    }

    public void setEndPointX(double endPointX) {
        this.endPointX = endPointX;
    }

    public double getEndPointY() {
        return endPointY;
    }

    public void setEndPointY(double endPointY) {
        this.endPointY = endPointY;
    }

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

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Start Name: ").append(startName).append("\n");
        sb.append("End Name: ").append(endName).append("\n");
        sb.append("Start Point X: ").append(startPointX).append("\n");
        sb.append("Start Point Y: ").append(startPointY).append("\n");
        sb.append("End Point X: ").append(endPointX).append("\n");
        sb.append("End Point Y: ").append(endPointY).append("\n");
        sb.append("Total Time: ").append(totalTime).append(" minutes\n");
        return sb.toString();
    }
}
