package com.example.moija;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {
    public CustomAdapter(Context context, List<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            // convertView의 높이 조정
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            layoutParams.height = convertDpToPx(80, getContext()); // 높이 80dp로 설정
            convertView.setLayoutParams(layoutParams);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView = convertView.findViewById(R.id.textView);
        String item = getItem(position);

        textView.setText(item);

        // 이미지뷰 크기 설정
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                convertDpToPx(64, getContext()), // 너비 64dp
                convertDpToPx(64, getContext())); // 높이 64dp
        imageView.setLayoutParams(imageLayoutParams);

        // 이미지 설정 및 가시성
        if (/* 여기에 조건 로직 */) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.city_bus);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView.setVisibility(View.GONE);
        }

        return convertView;
    }

    // dp를 픽셀 단위로 변환하는 메서드
    private int convertDpToPx(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
