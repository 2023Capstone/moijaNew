package com.example.moija.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OdsayData {
    @SerializedName("result")
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "PublicTransitResponse{" +
                "result=" + result +
                '}';
    }

    public static class Result {
        @SerializedName("path")
        private List<Path> path;

        public void setPath(List<Path> path) {
            this.path = path;
        }
        public List<Path> getPath ()
        {
            return path;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "path=" + path +
                    '}';
        }
    }

    public static class Path {
        @SerializedName("pathType")
        private int pathType;
        @SerializedName("info")
        private Info info;
        @SerializedName("subPath")
        private List<SubPath> subPath;

        public int getPathType() {
            return pathType;
        }

        public void setPathType(int pathType) {
            this.pathType = pathType;
        }

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        public List<SubPath> getSubPath() {
            return subPath;
        }

        public void setSubPath(List<SubPath> subPath) {
            this.subPath = subPath;
        }

        @Override
        public String toString() {
            return "Path{" +
                    "pathType=" + pathType +
                    ", info=" + info +
                    ", subPath=" + subPath +
                    '}';
        }
    }

    public static class Info {
        @SerializedName("firstStartStation")
        private String firstStartStation;
        @SerializedName("lastEndStation")
        private String lastEndStation;

        @SerializedName("totalTime")
        private int totalTime;

        public String getFirstStartStation() {
            return firstStartStation;
        }

        public void setFirstStartStation(String firstStartStation) {
            this.firstStartStation = firstStartStation;
        }

        public String getLastEndStation() {
            return lastEndStation;
        }

        public void setLastEndStation(String lastEndStation) {
            this.lastEndStation = lastEndStation;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(int totalTime) {
            this.totalTime = totalTime;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "firstStartStation='" + firstStartStation + '\'' +
                    ", lastEndStation='" + lastEndStation + '\'' +
                    ", totalTime=" + totalTime +
                    '}';
        }
    }

    public static class SubPath {
        @SerializedName("trafficType")
        private int trafficType;
        @SerializedName("sectionTime")
        private int sectionTime;
        @SerializedName("lane")
        private List<Lane> lane;
        @SerializedName("startName")
        private String startName;
        @SerializedName("endName")
        private String endName;
        @SerializedName("startX")
        private double startX;
        @SerializedName("startY")
        private double startY;
        @SerializedName("endX")
        private double endX;
        @SerializedName("endY")
        private double endY;

        public int getTrafficType() {
            return trafficType;
        }

        public void setTrafficType(int trafficType) {
            this.trafficType = trafficType;
        }

        public int getSectionTime() {
            return sectionTime;
        }

        public void setSectionTime(int sectionTime) {
            this.sectionTime = sectionTime;
        }

        public List<Lane> getLane() {
            return lane;
        }

        public void setLane(List<Lane> lane) {
            this.lane = lane;
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

        public double getStartX() {
            return startX;
        }

        public void setStartX(double startX) {
            this.startX = startX;
        }

        public double getStartY() {
            return startY;
        }

        public void setStartY(double startY) {
            this.startY = startY;
        }

        public double getEndX() {
            return endX;
        }

        public void setEndX(double endX) {
            this.endX = endX;
        }

        public double getEndY() {
            return endY;
        }

        public void setEndY(double endY) {
            this.endY = endY;
        }

        @Override
        public String toString() {
            return "SubPath{" + "\n" +
                    "trafficType=" + trafficType + "\n" +
                    ", sectionTime=" + sectionTime + "\n" +
                    ", lane=" + lane + "\n" +
                    ", startName='" + startName + '\'' +
                    ", endName='" + endName + '\'' +
                    ", startX=" + startX + "\n" +
                    ", startY=" + startY + "\n" +
                    ", endX=" + endX + "\n" +
                    ", endY=" + endY + "\n" +
                    '}';
        }
    }

    public static class Lane {
        @SerializedName("busNo")
        private String busNo;
        @SerializedName("busID")
        private int busID;

        public String getBusNo() {
            return busNo;
        }

        public void setBusNo(String busNo) {
            this.busNo = busNo;
        }

        public int getBusID() {
            return busID;
        }

        public void setBusID(int busID) {
            this.busID = busID;
        }

        @Override
        public String toString() {
            return "Lane{" +
                    "busNo='" + busNo + '\'' +
                    ", busID=" + busID +
                    '}';
        }
    }

}
