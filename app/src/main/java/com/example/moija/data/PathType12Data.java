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
    private int totalTime2;
    private int totalTime3;

    private String midStartName;
    private String midEndName;

    public String getMidStartName() {
        return midStartName;
    }

    public void setMidStartName(String midStartName) {
        this.midStartName = midStartName;
    }

    public String getMidEndName() {
        return midEndName;
    }

    public void setMidEndName(String midEndName) {
        this.midEndName = midEndName;
    }

    List<List<String>> busNos2 = new ArrayList<>();
    List<String> startNames2 = new ArrayList<>();
    List<String> endNames2 = new ArrayList<>();

    public class SubPathData {
        private List<String> busNos;
        private String startName;
        private String endName;

        public SubPathData(List<String> busNos, String startName, String endName) {
            this.busNos = busNos;
            this.startName = startName;
            this.endName = endName;
        }
    }

    public class PathData {
        private List<SubPathData> subPaths;

        private int totalTime;  // Path의 총 소요 시간을 저장할 필드

        public PathData() {
            this.subPaths = new ArrayList<>();
        }

        public void setTotalTime(int totalTime) {
            this.totalTime = totalTime;
        }

        public void addSubPath(SubPathData subPath) {
            subPaths.add(subPath);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("TotalTime: ").append(totalTime).append("\n");
            for (SubPathData subPath : subPaths) {
                sb.append("Bus Nos: ").append(String.join(", ", subPath.busNos)).append("\n");
                sb.append("Start: ").append(subPath.startName).append("\n");
                sb.append("End: ").append(subPath.endName).append("\n");
            }
            return sb.toString();
        }

    }

    public class TravelRoute {
        private List<PathData> paths;

        public TravelRoute() {
            this.paths = new ArrayList<>();
        }

        public void addPath(PathData path) {
            paths.add(path);
        }

        // Getters, setters 및 toString 메서드 생략
    }
    // 첫 번째 API 호출에 대한 데이터 저장을 위한 필드
    private List<PathData> api1Paths;

    // 두 번째 API 호출에 대한 데이터 저장을 위한 필드
    private List<PathData> api2Paths;

    public PathType12Data() {
        api1Paths = new ArrayList<>();
        api2Paths = new ArrayList<>();
    }

    public void addApi1Path(PathData path) {
        api1Paths.add(path);
    }

    public void addApi2Path(PathData path) {
        api2Paths.add(path);
    }
    private List<SubPathData> subPaths1 = new ArrayList<>();
    private List<SubPathData> subPaths2 = new ArrayList<>();

//    public void addSubPath1( List<String> busNos, String startName, String endName) {
//        SubPathData subPathData = new SubPathData(busNos, startName, endName);
//        this.subPaths1.add(subPathData);
//    }
//
//    public void addSubPath2( List<String> busNos, String startName, String endName) {
//        SubPathData subPathData = new SubPathData(busNos, startName, endName);
//        this.subPaths2.add(subPathData);
//    }

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

    public int getTotalTime3() {
        return totalTime3;
    }
    public void setTotalTime3(int totalTIme3) {
        this.totalTime3 = totalTIme3;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int minSize = Math.min(api1Paths.size(), api2Paths.size()); // 더 작은 사이즈를 기준으로 설정

        for (int i = 0; i < minSize; i++) {
            PathData api1Path = api1Paths.get(i);
            PathData api2Path = api2Paths.get(i);

            // API 1의 경로 데이터
            sb.append("Path ").append(i + 1).append("\n");
            sb.append("TotalTime: ").append(api1Path.totalTime + totalTime2).append("\n");
            for (SubPathData subPath : api1Path.subPaths) {
                sb.append("- SubPath: Bus Nos: ")
                        .append(String.join(", ", subPath.busNos))
                        .append(", Start: ").append(subPath.startName)
                        .append(", End: ").append(subPath.endName).append("\n");
            }

            // 중간 경로 데이터
            sb.append("Mid Route: ").append(midStartName).append(" ->> ").append(midEndName).append("\n");

            // API 2의 경로 데이터
            sb.append("API 2 Paths:\n");
            for (SubPathData subPath : api2Path.subPaths) {
                sb.append("- SubPath: Bus Nos: ")
                        .append(String.join(", ", subPath.busNos))
                        .append(", Start: ").append(subPath.startName)
                        .append(", End: ").append(subPath.endName).append("\n");
            }

            sb.append("\n"); // 경로 간 구분을 위한 줄바꿈
        }

        return sb.toString();
    }
}

