package com.example.moija;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moija.data.PathInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PathAdapter extends ArrayAdapter<PathInfo> {
    private LayoutInflater inflater;

    public PathAdapter(Context context, List<PathInfo> paths) {
        super(context, R.layout.pathlist_item_place, paths);
        inflater = LayoutInflater.from(context);
            Collections.sort(paths, new Comparator<PathInfo>() {
                @Override
                public int compare(PathInfo path1, PathInfo path2) {
                    // 내림차순으로 정렬
                    return Integer.compare(path1.getTotalTime(), path2.getTotalTime());
                }
            });

            notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.pathlist_item_place, parent, false);
        } else {
            view = convertView;
        }

        TextView TotalTimeText = view.findViewById(R.id.TotalTimeText);
        TextView BusText = view.findViewById(R.id.BusText);
        PathInfo path = getItem(position);

        if (path != null) {
            // 장소 이름을 텍스트뷰에 설정
            TotalTimeText.setText(path.getTotalTime() + "분");
            TotalTimeText.setTextSize(30);
            BusText.setText("");
            //장소 주소를 설정
            int busidno = 0;
            for (int i = 0; i < path.getBusNos().size(); i++) {

                if (!path.getBusNos().get(i).contains("도보") && !path.getBusNos().get(i).contains("시외버스") && !path.getBusNos().get(i).contains("고속버스")) {

                    BusText.append("버스 번호: " + path.getBusNos().get(i).get(0).toString() + "\n");
                    BusText.append("승차: " + path.getStartNames().get(i).toString() + "\n");
                    BusText.append("하차: " + path.getEndNames().get(i).toString() + "\n");
                } else if (path.getBusNos().get(i).contains("시외버스")) {
                    BusText.append("----시외버스 탑승----" + "\n");
                    BusText.append("승차: " + path.getStartNames().get(i).toString() + "\n");
                    BusText.append("하차: " + path.getEndNames().get(i).toString() + "\n");
                } else if (path.getBusNos().get(i).contains("고속버스")) {
                    BusText.append("----고속버스 탑승----" + "\n");
                    BusText.append("승차: " + path.getStartNames().get(i).toString() + "\n");
                    BusText.append("하차: " + path.getEndNames().get(i).toString() + "\n");
                }
            }
        }


        notifyDataSetChanged();
        return view;
    }

    // convertDpToPixel 메서드가 어떻게 구현되었는지 확인하고 필요에 따라 수정하세요.
    private int convertDpToPixel(int dp, Context context) {
        // 구현 방법은 상황에 따라 다르며, dp를 px로 변환하는 일반적인 방법은 다음과 같습니다.
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
