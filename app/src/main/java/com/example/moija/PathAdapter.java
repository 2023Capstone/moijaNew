package com.example.moija;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.moija.data.PathInfo;
import com.example.moija.map.Mylocation;
import com.example.moija.map.Place;

import java.util.List;

public class PathAdapter extends ArrayAdapter<PathInfo> {
    private LayoutInflater inflater;
    public PathAdapter(Context context, List<PathInfo> paths) {
        super(context, R.layout.pathlist_item_place, paths);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.pathlist_item_place, parent, false);
        }

        TextView TotalTimeText= view.findViewById(R.id.TotalTimeText);
        TextView BusText = view.findViewById(R.id.BusText);
        PathInfo path = getItem(position);

        if (path != null) {
            // 장소 이름을 텍스트뷰에 설정
            TotalTimeText.setText("소요 시간: "+path.getTotalTime());
            BusText.setText("");
            //장소 주소를 설정
            int busidno=0;
            for(int i=0; i<path.getBusNos().size(); i++)
            {

                if(!path.getBusNos().get(i).contains("도보") && !path.getBusNos().get(i).contains("시외버스") && !path.getBusNos().get(i).contains("고속버스")) {
                   BusText.append("버스 번호: " + path.getBusNos().get(i).get(0).toString() + "\n");
                    BusText.append("승차: " + path.getStartNames().get(i).toString() + "\n");
                    BusText.append("하차: " + path.getEndNames().get(i).toString() + "\n");
                }
                else if(path.getBusNos().get(i).contains("시외버스")){
                    BusText.append("----시외버스 탑승----"+"\n");
                    BusText.append("승차: " + path.getStartNames().get(i).toString() + "\n");
                    BusText.append("하차: " + path.getEndNames().get(i).toString() + "\n");
                } else if(path.getBusNos().get(i).contains("고속버스")){
                    BusText.append("----고속버스 탑승----"+"\n");
                    BusText.append("승차: " + path.getStartNames().get(i).toString() + "\n");
                    BusText.append("하차: " + path.getEndNames().get(i).toString() + "\n");
                }
            }

        }
        notifyDataSetChanged();
        return view;
    }
}