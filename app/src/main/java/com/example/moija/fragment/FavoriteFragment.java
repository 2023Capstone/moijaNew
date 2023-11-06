package com.example.moija.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.moija.R;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private ListView favoriteListView;
    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.activity_favorite_fragment,container,false);
        favoriteListView=rootview.findViewById(R.id.favoriteListView);
        ArrayList<String> favorites=new ArrayList<>();
        favorites.add("연암공과대학교-서울역");
        favorites.add("연암공과대학교-해운대");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(rootview.getContext(),android.R.layout.simple_list_item_1,favorites);
        favoriteListView.setAdapter(adapter);
        return rootview;
    }
}