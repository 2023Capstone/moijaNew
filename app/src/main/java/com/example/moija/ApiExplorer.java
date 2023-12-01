package com.example.moija;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ApiExplorer implements Runnable {
    private Handler handler;  // Handler 추가
    private volatile boolean isRunning = false; // 스레드 실행 상태 플래그
    private Thread thread; // 스레드 인스턴스

    private Map<Integer, Integer> cityCodes;
    private List<String> nodeNames;
    private int totalCount;

    public ApiExplorer(Handler handler) {
        this.handler = handler;
        cityCodes = new HashMap<>();
        nodeNames = new ArrayList<>();
        initializeCityCodes();
    }

    private void initializeCityCodes() {
        String rawData = "1180\t31370\n" + "7010\t38090\n" + "7110\t38390\n" + "4010\t37100\n" + "4020\t37020\n"
                + "3010\t34070\n" + "4110\t37370\n" + "7120\t32400\n" + "10150\t38340\n" + "1020\t31100\n"
                + "9060\t35370\n" + "5070\t36350\n" + "5080\t36320\n" + "3020\t34020\n" + "1060\t31110\n"
                + "1160\t31060\n" + "5010\t36060\n" + "1110\t31250\n" + "5000\t24\n" + "11070\t33360\n"
                + "5090\t36330\n" + "1080\t31120\n" + "4030\t37050\n" + "9010\t35020\n" + "4120\t37310\n"
                + "1280\t31160\n" + "9020\t35060\n" + "4040\t37030\n" + "1170\t31230\n" + "7020\t38070\n"
                + "5020\t36040\n" + "1120\t31130\n" + "9030\t35050\n" + "7130\t38350\n" + "3030\t34060\n"
                + "11090\t33380\n" + "3090\t34390\n" + "4000\t22\n" + "3000\t25\n" + "1240\t31080\n"
                + "5030\t36010\n" + "5110\t36420\n" + "9070\t35330\n" + "4050\t37090\n" + "7040\t38080\n"
                + "11030\t33320\n" + "4130\t37410\n" + "7000\t21\n" + "3100\t34330\n" + "1050\t31050\n"
                + "7050\t38060\n" + "7140\t38370\n" + "3050\t34050\n" + "1010\t31020\n" + "3300\t12\n"
                + "10000\t32060\n" + "1100\t31010\n" + "9090\t35360\n" + "5040\t36030\n" + "1140\t31150\n"
                + "5130\t36480\n" + "3060\t34040\n" + "4070\t37040\n" + "1040\t31090\n" + "1270\t31220\n"
                + "1030\t31040\n" + "10130\t32380\n" + "7060\t38100\n" + "10160\t32410\n" + "1250\t31260\n"
                + "1190\t31380\n" + "5050\t36020\n" + "1310\t31320\n" + "3120\t31350\n" + "5140\t36440\n"
                + "4150\t37350\n" + "11050\t33340\n" + "5150\t36410\n" + "4160\t37340\n" + "10090\t32330\n"
                + "4080\t37060\n" + "4090\t37070\n" + "4170\t37400\n" + "1210\t31140\n" + "11040\t33330\n"
                + "5160\t36460\n" + "1130\t31190\n" + "4180\t37430\n" + "6000\t26\n" + "4190\t37420\n"
                + "11080\t33370\n" + "7150\t38310\n" + "4200\t37320\n" + "1290\t31170\n" + "1090\t31030\n"
                + "1300\t31210\n" + "9040\t35030\n" + "10140\t32390\n" + "2000\t23\n" + "9110\t35350\n"
                + "5170\t36450\n" + "9120\t35340\n" + "5180\t36380\n" + "9000\t35010\n" + "9050\t35040\n"
                + "8000\t39\n" + "11020\t33030\n" + "5190\t36470\n" + "9130\t35320\n" + "7070\t38030\n"
                + "11060\t33350\n" + "7160\t38330\n" + "7090\t38010\n" + "3070\t34010\n" + "10110\t32360\n"
                + "4210\t37360\n" + "4220\t37330\n" + "11000\t33010\n" + "10170\t32010\n" + "11010\t33020\n"
                + "4230\t37390\n" + "10040\t32050\n" + "7100\t38050\n" + "1150\t31200\n" + "1220\t31070\n"
                + "1260\t31270\n" + "4100\t37010\n" + "1070\t31180\n" + "7170\t38360\n" + "7180\t38320\n"
                + "7190\t38380\n" + "5200\t36430\n" + "7200\t38400\n" + "5210\t36400\n" + "10070\t32310\n"
                + "1230\t31240\n" + "10120\t32370\n" + "10080\t32020\n";

        String[] lines = rawData.split("\n");
        for (String line : lines) {
            String[] parts = line.split("\t");
            int key = Integer.parseInt(parts[0]);
            int value = Integer.parseInt(parts[1]);
            cityCodes.put(key, value);
        }
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();
    }

    @Override
    public void run() {
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            try {
                executeApiCall();

                // 데이터 로딩 완료 후 메시지 전송
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
//                bundle.putString("nodeNames", nodeNames.toString());
                bundle.putStringArrayList("nodeNames", new ArrayList<>(nodeNames));
                bundle.putInt("totalCount", totalCount);
                msg.setData(bundle);
                handler.sendMessage(msg);

                Thread.sleep(10000); // 10초 대기 후 다시 실행

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 스레드 중단
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeApiCall() throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1613000/BusLcInfoInqireService/getRouteAcctoBusLcList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=oN5Nb0f8GC1%2FULPYTW0DMcWIjmNQ2VxOvGBkQatyEDrIrvdOO%2F4Z3dmPKP15PJbt9tBv%2FRO%2BHKJULbGs2UHsJg%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); /*데이터 타입(xml, json)*/
        urlBuilder.append("&" + URLEncoder.encode("cityCode","UTF-8") + "=" + URLEncoder.encode(cityCodes.get(3000).toString(), "UTF-8")); /*도시코드 [상세기능3 도시코드 목록 조회]에서 조회 가능*/
        urlBuilder.append("&" + URLEncoder.encode("routeId","UTF-8") + "=" + URLEncoder.encode("DJB30300052", "UTF-8")); /*노선ID [국토교통부(TAGO)_버스노선정보]에서 조회가능*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            ByteArrayInputStream input = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
            Document doc = dBuilder.parse(input);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("item");

            nodeNames.clear(); // 새 요청 전에 리스트를 비웁니다.
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String nodenm = eElement.getElementsByTagName("nodenm").item(0).getTextContent();
                    nodeNames.add(nodenm);
                }
            }

            String totalCountStr = doc.getElementsByTagName("totalCount").item(0).getTextContent();
            totalCount = Integer.parseInt(totalCountStr);

        } catch (ParserConfigurationException | SAXException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // Getter 메소드 추가
    public List<String> getNodeNames() {
        return nodeNames;
    }

    public int getTotalCount() {
        return totalCount;
    }
}