package com.example.moija.data;

import java.util.ArrayList;
import java.util.List;

public class PathInfo {
    private List<Integer> trafficType=new ArrayList<>();
    List<List<String>> busNos = new ArrayList<>();
    List<String> startNames = new ArrayList<>();
    List<String> endNames = new ArrayList<>();
    private int totalTime;

    private List<Double> startx=new ArrayList<>();
    private List<Double> starty=new ArrayList<>();
    private List<Double> endx=new ArrayList<>();
    private List<Double> endy=new ArrayList<>();
    List<String> path12StartNames = new ArrayList<>();
    List<String> path12EndNames = new ArrayList<>();



    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void addTotalTime(int totalTime){this.totalTime+=totalTime;}

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


    public List<Double> getstartx(){return startx;}
    public List<Double> getstarty(){return starty;}
    public List<Double> getendx(){return endx;}
    public List<Double> getendy(){return endy;}

    public List<Integer> getTrafficType(){return trafficType;}
    public void setSubPath(List<String> busNos, String startName, String endName,double startX,double startY,double endX,double endY,int trafficType) {
        this.busNos.add(busNos);
        this.startNames.add(startName);
        this.endNames.add(endName);
        this.startx.add(startX);
        this.starty.add(startY);
        this.endx.add(endX);
        this.endy.add(endY);
        this.trafficType.add(trafficType);
    }
    public void addPathinfo(PathInfo path,int index){
        for(int i=0; i<path.getBusNos().size(); i++){
            this.busNos.add(index,path.getBusNos().get(i));
            this.startNames.add(index,path.getStartNames().get(i));
            this.endNames.add(index,path.getEndNames().get(i));
            this.startx.add(index,path.getstartx().get(i));
            this.starty.add(index,path.getstarty().get(i));
            this.endx.add(index,path.getendx().get(i));
            this.endy.add(index,path.getendy().get(i));
            this.trafficType.add(index,path.getTrafficType().get(i));
        }

    }
    public void addPath12Names(String startName, String endName) {
        this.path12StartNames.add(startName);
        this.path12EndNames.add(endName);
    }

    /*@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // totalTime 리스트의 각 요소를 문자열로 변환하여 추가
        sb.append("TotalTime: ").append(totalTime + "분").append("\n");
        for (int i = 0; i < busNos.size(); i++) {
            sb.append("Bus Nos: ").append(String.join(", ", busNos.get(i))).append("\n");
            sb.append("Start: ").append(startNames.get(i)).append("\n");
            sb.append("End: ").append(endNames.get(i)).append("\n");
        }
        for (int i = 0; i < path12StartNames.size(); i++) {
            sb.append("Path12 Start: ").append(path12StartNames.get(i)).append(">");
            sb.append("Path12 End: ").append(path12EndNames.get(i)).append("\n");
        }
        return sb.toString();
    }*/
}
