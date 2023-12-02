package com.example.moija;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.moija.data.CombinedPathData;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<CombinedPathData> {
    public ListViewAdapter(Context context, List<CombinedPathData> pathDataList) {
        super(context, 0, pathDataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // View 생성 및 데이터 바인딩 로직
        CombinedPathData item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_pathlist_item_place, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.textView);
        textView.setText(item.getCombinedPathInfo());
        return convertView;
    }

    // 기타 필요한 메서드들...
}