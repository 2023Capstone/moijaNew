package com.example.moija.data;

import java.util.ArrayList;
import java.util.List;

public class PathType12Data {

    private CallApiData callApiData = new CallApiData();
    private int totalTime1, totalTime2, totalTime3;
    private String midStartName, midEndName;
    private double midStartX, midStartY, midEndX, midEndY;


    public PathType12Data() {
        api1Paths = new ArrayList<>();
        api2Paths = new ArrayList<>();
    }




    // PathData를 저장한다.
    private List<PathData> api1Paths = new ArrayList<>();
    private List<PathData> api2Paths = new ArrayList<>();

    public List<PathData> getApi1Paths() {
        return api1Paths;
    }

    public void setApi1Paths(List<PathData> api1Paths) {
        this.api1Paths = api1Paths;
    }

    public List<PathData> getApi2Paths() {
        return api2Paths;
    }

    public void setApi2Paths(List<PathData> api2Paths) {
        this.api2Paths = api2Paths;
    }

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


    public double getMidStartX() {
        return midStartX;
    }

    public void setMidStartX(double midStartX) {
        this.midStartX = midStartX;
    }

    public double getMidStartY() {
        return midStartY;
    }

    public void setMidStartY(double midStartY) {
        this.midStartY = midStartY;
    }

    public double getMidEndX() {
        return midEndX;
    }

    public void setMidEndX(double midEndX) {
        this.midEndX = midEndX;
    }

    public double getMidEndY() {
        return midEndY;
    }

    public void setMidEndY(double midEndY) {
        this.midEndY = midEndY;
    }

    public void addApi1Path(PathData path) {
        api1Paths.add(path);
    }

    public void addApi2Path(PathData path) {
        api2Paths.add(path);
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

        public List<SubPathData> getSubPaths() {
            return subPaths;
        }

        public void setSubPaths(List<SubPathData> subPaths) {
            this.subPaths = subPaths;
        }

        public int getTotalTime() {
            return totalTime;
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

    public class SubPathData {
        private List<String> busNos;
        private String startName;
        private String endName;
        private int trafficType; // 교통 수단 유형
        private List<Integer> busId; // 버스 ID
        private double startX; // 도보 이동 시작점의 X 좌표
        private double startY; // 도보 이동 시작점의 Y 좌표
        private double endX; // 도보 이동 끝점의 X 좌표
        private double endY; // 도보 이동 끝점의 Y 좌표

        public SubPathData(int trafficType) {
            this.trafficType = trafficType;
        }
        public SubPathData(List<String> busNos, String startName, String endName, double startX, double startY, List<Integer> busId, int trafficType) {
            this.busNos = busNos;
            this.startName = startName;
            this.endName = endName;
            this.startX = startX;
            this.startY = startY;
            this.busId = busId;
            this.trafficType = trafficType;
        }

        public List<String> getBusNos() {
            return busNos;
        }

        public void setBusNos(List<String> busNos) {
            this.busNos = busNos;
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
    }

    private List<SubPathData> subPaths1 = new ArrayList<>();
    private List<SubPathData> subPaths2 = new ArrayList<>();


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


    public int getApi1PathsSize() {
        return api1Paths.size();
    }

    // api2Paths의 크기를 반환하는 메서드
    public int getApi2PathsSize() {
        return api2Paths.size();
    }

    public String getIndividualPathString(int index) {
        if (index >= api1Paths.size() || index >= api2Paths.size()) {
            return ""; // 인덱스가 경로의 수를 초과하는 경우 빈 문자열 반환
        }

        StringBuilder pathStringBuilder = new StringBuilder();
        PathData api1Path = api1Paths.get(index);
        PathData api2Path = api2Paths.get(index);

        // 총 소요 시간
        int totalPathTime = api1Path.totalTime + totalTime2 + api2Path.totalTime;
        pathStringBuilder.append("TotalTime: ").append(totalPathTime).append("분\n");

        // API 1 경로 정보
        for (SubPathData subPath : api1Path.subPaths) {
            pathStringBuilder.append("- SubPath: Bus Nos: ")
                    .append(String.join(", ", subPath.busNos)).append("\n")
                    .append(", Start: ").append(subPath.startName).append("\n")
                    .append(", End: ").append(subPath.endName).append("\n");
        }

        // 중간 경로 정보
        pathStringBuilder.append("Mid Route: ").append(midStartName).append(" -> ").append(midEndName).append("\n");

        // API 2 경로 정보
        for (SubPathData subPath : api2Path.subPaths) {
            pathStringBuilder.append("- SubPath: Bus Nos: ")
                    .append(String.join(", ", subPath.busNos)).append("\n")
                    .append(", Start: ").append(subPath.startName).append("\n")
                    .append(", End: ").append(subPath.endName).append("\n");
        }

        return pathStringBuilder.toString();
    }

    private void appendSubPathInfo(StringBuilder sb, SubPathData subPath) {
        if (subPath.busNos != null && !subPath.busNos.isEmpty()) {
            sb.append("  - 버스 번호: ").append(String.join(", ", subPath.busNos)).append("\n");
        }
        sb.append("    시작: ").append(subPath.startName).append("\n")
                .append("    끝: ").append(subPath.endName).append("\n");
    }

    public String toStringApi1() {
        StringBuilder sb = new StringBuilder();
        for (PathData pathData : api1Paths) {
            sb.append(pathData.toString()).append("\n");
        }
        return sb.toString();
    }

    public String toStringApi2() {
        StringBuilder sb = new StringBuilder();
        for (PathData pathData : api2Paths) {
            sb.append(pathData.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int minSize = Math.min(api1Paths.size(), api2Paths.size()); // 더 작은 사이즈를 기준으로 설정

        for (int i = 0; i < minSize; i++) {
            PathData api1Path = api1Paths.get(i);
            PathData api2Path = api2Paths.get(i);

            // API 1의 경로 데이터
            sb.append("TotalTime: ").append(api1Path.totalTime + totalTime2 + api2Path.totalTime).append("\n");
            for (SubPathData subPath : api1Path.subPaths) {
                sb.append("- SubPath: Bus Nos: ")
                        .append(String.join(", ", subPath.busNos)).append("\n")
                        .append(", Start: ").append(subPath.startName).append("\n")
                        .append(", End: ").append(subPath.endName).append("\n");
            }

            // 중간 경로 데이터
            sb.append("Mid Route: ").append(midStartName).append(" ->> ").append(midEndName).append("\n");

            // API 2의 경로 데이터
            for (SubPathData subPath : api2Path.subPaths) {
                sb.append("- SubPath: Bus Nos: ")
                        .append(String.join(", ", subPath.busNos)).append("\n")
                        .append(", Start: ").append(subPath.startName).append("\n")
                        .append(", End: ").append(subPath.endName).append("\n");
            }

            sb.append("\n"); // 경로 간 구분을 위한 줄바꿈
        }

        return sb.toString();
    }
}

