package com.example.moija.data;

import java.util.ArrayList;
import java.util.List;

public class PathInfo2 {
    //처음 부터 patyType2인 경우
    List<List<String>> busNos = new ArrayList<>();
    List<String> startNames = new ArrayList<>();
    List<String> endNames = new ArrayList<>();
    //totalTime
    private int totalTime;


    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public List<List<String>> getBusNos() {
        return busNos;
    }

    public void setBusNos(List<List<String>> busNos) {
        this.busNos = busNos;
    }

    public List<String> getStartNames() {
        return startNames;
    }

    public void setStartNames(List<String> startNames) {
        this.startNames = startNames;
    }

    public List<String> getEndNames() {
        return endNames;
    }

    public void setEndNames(List<String> endNames) {
        this.endNames = endNames;
    }


    public void addSubPath( List<String> busNos, String startName, String endName) {
        this.busNos.add(busNos);
        this.startNames.add(startName);
        this.endNames.add(endName);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // totalTime 리스트의 각 요소를 문자열로 변환하여 추가
        sb.append("TotalTime: ").append(totalTime + "분").append("\n");
        for (int i = 0; i < busNos.size(); i++) {
            sb.append("Bus Nos: ").append(String.join(", ", busNos.get(i))).append("\n");
            sb.append("Start: ").append(startNames.get(i)).append("\n");
            sb.append("End: ").append(endNames.get(i)).append("\n");
        }
        return sb.toString();
    }
}

