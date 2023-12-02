package com.example.moija.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PathInfo {
    private List<Integer> trafficType=new ArrayList<>();
    List<List<String>> busNos = new ArrayList<>();
    List<List<String>> busLocalBlIDs = new ArrayList<>();
    List<List<Integer>> busCityCodes=new ArrayList<>();

    List<List<Integer>> busIDs=new ArrayList<>();
    List<String> startNames = new ArrayList<>();
    List<String> endNames = new ArrayList<>();
    private int totalTime;

    private List<Double> startx=new ArrayList<>();
    private List<Double> starty=new ArrayList<>();
    private List<Double> endx=new ArrayList<>();
    private List<Double> endy=new ArrayList<>();
    List<String> path12StartNames = new ArrayList<>();
    List<String> path12EndNames = new ArrayList<>();



    public List<List<String>> getBusLocalBlIDs(){return busLocalBlIDs;}

    public List<List<Integer>> getBusCityCodes(){return busCityCodes;}
    public List<List<Integer>> getBusIDs(){return busIDs;}

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
    public void setSubPath(List<String> busNos,List<Integer> busIDs,List<String> busLocalBlIDs,List<Integer> busCityCodes,String startName, String endName,double startX,double startY,double endX,double endY,int trafficType) {
        this.busNos.add(busNos);
        this.busIDs.add(busIDs);
        this.busLocalBlIDs.add(busLocalBlIDs);
        this.busCityCodes.add(busCityCodes);
        this.startNames.add(startName);
        this.endNames.add(endName);
        this.startx.add(startX);
        this.starty.add(startY);
        this.endx.add(endX);
        this.endy.add(endY);
        this.trafficType.add(trafficType);
        Log.d("localblid",this.busLocalBlIDs.toString());
        Log.d("localbuscitycodes",this.busCityCodes.toString());
    }
    public void WalkSetSubPath(List<String> busNos,String startName, String endName,double startX,double startY,double endX,double endY,int trafficType) {
        this.busNos.add(busNos);
        this.startNames.add(startName);
        this.endNames.add(endName);
        this.startx.add(startX);
        this.starty.add(startY);
        this.endx.add(endX);
        this.endy.add(endY);
        this.trafficType.add(trafficType);
    }
    public void addPathinfo(PathInfo path,int startorend){

        if(startorend==0) {
            for (int i = 0; i < path.getBusLocalBlIDs().size(); i++) {
                this.busLocalBlIDs.add(0, path.getBusLocalBlIDs().get(i));
                this.busCityCodes.add(0, path.getBusCityCodes().get(i));
            }
            for (int i = 0; i < path.getBusNos().size(); i++) {
                this.busNos.add(0, path.getBusNos().get(i));
                this.busIDs.add(0, path.getBusIDs().get(0));

                this.startNames.add(0, path.getStartNames().get(i));
                this.endNames.add(0, path.getEndNames().get(i));
                this.startx.add(0, path.getstartx().get(i));
                this.starty.add(0, path.getstarty().get(i));
                this.endx.add(0, path.getendx().get(i));
                this.endy.add(0, path.getendy().get(i));
                this.trafficType.add(0, path.getTrafficType().get(i));
            }

        }
        else if(startorend==1){
            for (int i = 0; i < path.getBusLocalBlIDs().size(); i++) {
                this.busLocalBlIDs.add(path.getBusLocalBlIDs().get(i));
                this.busCityCodes.add(path.getBusCityCodes().get(i));
            }
            for (int i = 0; i < path.getBusNos().size(); i++) {
                this.busNos.add(path.getBusNos().get(i));
                this.busIDs.add( path.getBusIDs().get(0));

                this.startNames.add( path.getStartNames().get(i));
                this.endNames.add( path.getEndNames().get(i));
                this.startx.add(path.getstartx().get(i));
                this.starty.add( path.getstarty().get(i));
                this.endx.add(path.getendx().get(i));
                this.endy.add( path.getendy().get(i));
                this.trafficType.add( path.getTrafficType().get(i));
            }

        }
        Log.d("localblid",this.busLocalBlIDs.toString());
        Log.d("localbuscitycodes",this.busCityCodes.toString());

    }
    public void addPath12Names(String startName, String endName) {
        this.path12StartNames.add(startName);
        this.path12EndNames.add(endName);
    }
}
