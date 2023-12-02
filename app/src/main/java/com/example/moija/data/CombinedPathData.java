package com.example.moija.data;

public class CombinedPathData {
    private PathType12Data.PathData api1Path;
    private PathType12Data.PathData api2Path;
    private int totalPathTime;
    private String midStartName;
    private String midEndName;

    public CombinedPathData(PathType12Data.PathData api1Path, PathType12Data.PathData api2Path, int totalTime2, String midStartName, String midEndName) {
        this.api1Path = api1Path;
        this.api2Path = api2Path;
        this.totalPathTime = api1Path.getTotalTime() + totalTime2 + api2Path.getTotalTime();
        this.midStartName = midStartName;
        this.midEndName = midEndName;
    }

    // 여기에 필요한 getter 메서드 추가...

    // 경로 정보를 하나의 문자열로 결합하는 메서드
    public String getCombinedPathInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("경로 1 정보: ").append(api1Path.toString()).append("\n");
        builder.append("중간 경로: ").append(midStartName).append(" -> ").append(midEndName).append("\n");
        builder.append("경로 2 정보: ").append(api2Path.toString()).append("\n");
        builder.append("총 소요 시간: ").append(totalPathTime).append("분\n");
        return builder.toString();
    }
}