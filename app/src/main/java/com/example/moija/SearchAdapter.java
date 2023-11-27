package com.example.moija;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.moija.map.Mylocation;
import com.example.moija.map.Place;

import java.util.List;

public class SearchAdapter extends ArrayAdapter<Place> {
    private LayoutInflater inflater;

    public SearchAdapter(Context context, List<Place> places) {
        super(context, R.layout.searchlist_item_place, places);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.searchlist_item_place, parent, false);
        }

        TextView placeNameTextView = view.findViewById(R.id.placeNameTextView);
        TextView placeAddressTextView = view.findViewById(R.id.placeAddressTextView);
        TextView placedistance=view.findViewById(R.id.distance);
        Place place = getItem(position);

        if (place != null) {
            // 장소 이름을 텍스트뷰에 설정
            placeNameTextView.setText(place.getPlaceName());
            placeNameTextView.setTextSize(20);
            //장소 주소를 설정
            placeAddressTextView.setText(place.getAddressName());
            //장소의 Location을 받아오고
            Location myplace=new Location("my location");
            Location findplace=new Location("finded location");
            findplace.setLatitude(place.getY());
            findplace.setLongitude(place.getX());
            if(Mylocation.Lastlocation!=null)
            {
                float distancetoFind=Mylocation.Lastlocation.distanceTo(findplace)/1000;
                String distancetoString=String.format("%.1f",distancetoFind);
                //현재위치와의 거리를 나타냄 (시작점을 검색으로 하면 시작점과의 거리로 바꿔야할수도 있음)
                placedistance.setText(distancetoString+"km");
            }

        }

        return view;
    }
}