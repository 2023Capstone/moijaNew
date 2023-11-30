package com.example.moija;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.moija.data.PathType12Data;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<String> {
    private List<PathType12Data.PathData> pathDataList;

    public ListViewAdapter(Context context, List<String> objects, List<PathType12Data.PathData> pathDataList) {
        super(context, 0, objects);
        this.pathDataList = pathDataList;
    }

    // getView 메서드 및 기타 메서드 생략...
}