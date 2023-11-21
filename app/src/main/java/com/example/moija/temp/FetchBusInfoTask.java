package com.example.moija.temp;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FetchBusInfoTask extends AsyncTask<String, Void, ArrayList<BusInfo>> {

    @Override
    protected ArrayList<BusInfo> doInBackground(String... params) {
        try {
            String apiKey = "{YOUR_API_KEY}";
            String sx = params[0];
            String sy = params[1];
            String ex = params[2];
            String ey = params[3];

            String urlInfo = "https://api.odsay.com/v1/api/searchPubTransPathT?SX=" + sx + "&SY=" + sy + "&EX=" + ex + "&EY=" + ey + "&apiKey="
                    + URLEncoder.encode(apiKey, "UTF-8");
            URL url = new URL(urlInfo);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            conn.disconnect();

            // 여기서 API 응답을 파싱하여 BusInfo 리스트로 변환
            return parseBusInfo(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ArrayList<BusInfo> result) {
        super.onPostExecute(result);
        // ListView에 결과 표시
        // 예: adapter.updateData(result);
    }

    private ArrayList<BusInfo> parseBusInfo(String json) {
        ArrayList<BusInfo> busInfos = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONObject result = jsonObj.getJSONObject("result");
            JSONArray paths = result.getJSONArray("path");

            // 최대 3개의 경로를 파싱합니다.
            for (int i = 0; i < paths.length() && i < 3; i++) {
                JSONObject path = paths.getJSONObject(i);
                JSONArray subPaths = path.getJSONArray("subPath");

                for (int j = 0; j < subPaths.length(); j++) {
                    JSONObject subPath = subPaths.getJSONObject(j);

                    // 버스 정보만 처리합니다.
                    if (subPath.getInt("trafficType") == 2) { // 2는 버스를 의미
                        JSONArray lanes = subPath.getJSONArray("lane");
                        for (int k = 0; k < lanes.length(); k++) {
                            JSONObject lane = lanes.getJSONObject(k);
                            String busNo = lane.getString("busNo");

                            String startName = subPath.getString("startName");
                            double startX = subPath.getDouble("startX");
                            double startY = subPath.getDouble("startY");
                            String endName = subPath.getString("endName");
                            double endX = subPath.getDouble("endX");
                            double endY = subPath.getDouble("endY");

                            BusInfo busInfo = new BusInfo(busNo, startName, startX, startY, endName, endX, endY);
                            busInfos.add(busInfo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return busInfos;
    }
}