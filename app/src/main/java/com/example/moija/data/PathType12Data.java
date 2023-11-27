package com.example.moija.data;

import java.util.ArrayList;
import java.util.List;

public class PathType12Data {

    CallApiData callApiData = new CallApiData();
    //처음 부터 patyType2인 경우
    List<List<String>> busNos1 = new ArrayList<>();
    List<String> startNames1 = new ArrayList<>();
    List<String> endNames1 = new ArrayList<>();
    //totalTime
    private int totalTime1;
    private int totalTime2 = callApiData.getTotalTime();
    private int totalTIme3;

    List<List<String>> busNos2 = new ArrayList<>();
    List<String> startNames2 = new ArrayList<>();
    List<String> endNames2 = new ArrayList<>();




    public void addSubPath1( List<String> busNos1, String startName1, String endName1) {
        this.busNos1.add(busNos1);
        this.startNames1.add(startName1);
        this.endNames1.add(endName1);
    }

    public void addSubPath2(List<String> busNos2, String startName2, String endName2) {
        this.busNos2.add(busNos2);
        this.startNames2.add(startName2);
        this.endNames2.add(endName2);
    }

    public CallApiData getCallApiData() {
        return callApiData;
    }

    public void setCallApiData(CallApiData callApiData) {
        this.callApiData = callApiData;
    }

    public List<List<String>> getBusNos1() {
        return busNos1;
    }

    public void setBusNos1(List<List<String>> busNos1) {
        this.busNos1 = busNos1;
    }

    public List<String> getStartNames1() {
        return startNames1;
    }

    public void setStartNames1(List<String> startNames1) {
        this.startNames1 = startNames1;
    }

    public List<String> getEndNames1() {
        return endNames1;
    }

    public void setEndNames1(List<String> endNames1) {
        this.endNames1 = endNames1;
    }

    public int getTotalTime1() {
        return totalTime1;
    }

    public void setTotalTime1(int totalTime1) {
        this.totalTime1 = totalTime1;
    }

    public int getTotalTime2() {
        return totalTime2;
    }

    public void setTotalTime2(int totalTime2) {
        this.totalTime2 = totalTime2;
    }

    public int getTotalTIme3() {
        return totalTIme3;
    }

    public void setTotalTIme3(int totalTIme3) {
        this.totalTIme3 = totalTIme3;
    }

    public List<List<String>> getBusNos2() {
        return busNos2;
    }

    public void setBusNos2(List<List<String>> busNos2) {
        this.busNos2 = busNos2;
    }

    public List<String> getStartNames2() {
        return startNames2;
    }

    public void setStartNames2(List<String> startNames2) {
        this.startNames2 = startNames2;
    }

    public List<String> getEndNames2() {
        return endNames2;
    }

    public void setEndNames2(List<String> endNames2) {
        this.endNames2 = endNames2;
    }

    public String toString1() {
        StringBuilder sb = new StringBuilder();
        // totalTime 리스트의 각 요소를 문자열로 변환하여 추가
        sb.append("TotalTime: ").append(totalTime1).append("\n");
        for (int i = 0; i < busNos1.size(); i++) {
            sb.append("Bus Nos: ").append(String.join(", ", busNos1.get(i))).append("\n");
            sb.append("Start: ").append(startNames1.get(i)).append("\n");
            sb.append("End: ").append(endNames1.get(i)).append("\n");
        }
        return sb.toString();
    }

    public String toString2() {
        StringBuilder sb = new StringBuilder();
        // totalTime 리스트의 각 요소를 문자열로 변환하여 추가
        sb.append("TotalTime: ").append(totalTIme3).append("\n");
        for (int i = 0; i < busNos2.size(); i++) {
            sb.append("Bus Nos: ").append(String.join(", ", busNos2.get(i))).append("\n");
            sb.append("Start: ").append(startNames2.get(i)).append("\n");
            sb.append("End: ").append(endNames2.get(i)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // totalTime 리스트의 각 요소를 문자열로 변환하여 추가
        sb.append("TotalTime: ").append(totalTime1 + totalTime2 + totalTIme3).append("\n");
        for (int i = 0; i < busNos1.size(); i++) {
            sb.append("Bus Nos: ").append(String.join(", ", busNos1.get(i))).append("\n");
            sb.append("Start: ").append(startNames1.get(i)).append("\n");
            sb.append("End: ").append(endNames1.get(i)).append("\n");
            sb.append(callApiData.getStartName()).append(">>").append(callApiData.getEndName());
        }

        for (int i = 0; i < busNos2.size(); i++) {
            sb.append("Bus Nos: ").append(String.join(", ", busNos2.get(i))).append("\n");
            sb.append("Start: ").append(startNames2.get(i)).append("\n");
            sb.append("End: ").append(endNames2.get(i)).append("\n");
        }
        return sb.toString();
    }
}

