package com.example.moija;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.moija.data.PathType12Data;

import java.util.List;

public class PathAdapter extends ArrayAdapter<PathType12Data> {
    private LayoutInflater inflater;

    public PathAdapter(Context context, List<PathType12Data> paths) {
        super(context, R.layout.activity_pathlist_item_place, paths);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_pathlist_item_place, parent, false);
        }

        TextView TotalTimeText= view.findViewById(R.id.TotalTimeText);
        TextView InBusText = view.findViewById(R.id.InBusText);
        TextView OutBusText=view.findViewById(R.id.OutBusText);
        TextView MidBusText = view.findViewById(R.id.MidBusText);

        PathType12Data path = getItem(position);

        if (path != null) {
            // 장소 이름을 텍스트뷰에 설정
            TotalTimeText.setText("소요 시간: "+ path.getTotalTime1() + path.getTotalTime2() + path.getTotalTime3());
            InBusText.setText("");
            OutBusText.setText("");
            //장소 주소를 설정
            int minBus = Math.min(path.getBusNos1().size(), path.getBusNos2().size());
            for(int i=0; i<minBus; i++)
            {
                InBusText.append("버스 번호: " + path.getBusNos1().get(i).toString() + "\n");
                InBusText.append("승차: " + path.getStartNames1().get(i).toString() + "\n");
                InBusText.append("하차: " + path.getEndNames1().get(i).toString() + "\n");

                MidBusText.append("승차: " + path.getStartNames1().toString() + "\n");
                MidBusText.append("하차: " + path.getMidEndName().toString() + "\n");

                OutBusText.append("버스 번호: " + path.getBusNos1().get(i).toString() + "\n");
                OutBusText.append("승차: " + path.getStartNames1().get(i).toString() + "\n");
                OutBusText.append("하차: " + path.getEndNames1().get(i).toString() + "\n");
            }

        }
        return view;
    }

}