package com.example.moija;

import static com.example.moija.time.DateTime.getCurrentDateTime;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class recordPlace extends AppCompatActivity {

    private EditText input1, input2;
    private Button recordPlace_add;
    private ListView recordPlaceList;
    private RecordPlaceDB recordPlaceDB;
    private ArrayAdapter<String> adapter;
    private Queue<String> dataList;
    private static final int MAX_QUEUE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_place);

        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        recordPlace_add = findViewById(R.id.recordPlace_add);
        recordPlaceList = findViewById(R.id.recordPlaceList);
        dataList = new LinkedList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(dataList));
        recordPlaceList.setAdapter(adapter);

        recordPlaceDB = new RecordPlaceDB(getApplicationContext());

        recordPlace_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = input1.getText().toString();
                String end = input2.getText().toString();

                SQLiteDatabase database = recordPlaceDB.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("startPlace", start);
                values.put("endPlace", end);
                values.put("time", getCurrentDateTime());
                long newRowId = database.insert("recordPlace_DB", null, values);

                if (newRowId == -1) {
                    // 데이터베이스에 추가 실패한 경우
                    Toast.makeText(getApplicationContext(), "데이터베이스에 정보를 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 데이터베이스에 성공적으로 추가한 경우
                    Toast.makeText(getApplicationContext(), "데이터베이스에 정보를 추가했습니다.", Toast.LENGTH_SHORT).show();
                    dataMaxRows(database);
                    // 데이터베이스 업데이트 후 리스트 업데이트
                    updateList();
                }
            }
        });

        // 리스트 아이템 클릭 리스너 추가
        recordPlaceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 아이템의 값을 가져와서 입력 필드에 표시
                String selectedItem = dataList.toArray(new String[0])[position];
                String[] parts = selectedItem.split(" - ");
                if (parts.length == 2) {
                    input1.setText(parts[0]);
                    input2.setText(parts[1]);
                }
            }
        });

        // 초기에 리스트 업데이트
        updateList();
    }

    private void updateList() {
        dataList.clear();
        SQLiteDatabase database = recordPlaceDB.getReadableDatabase();

        Cursor cursor = database.query("recordPlace_DB", null, null, null, null, null, "time DESC", "10");

        int startPlaceIndex = cursor.getColumnIndex("startPlace");
        int endPlaceIndex = cursor.getColumnIndex("endPlace");

        if (cursor.moveToFirst()) {
            do {
                String startPlace = cursor.getString(startPlaceIndex);
                String endPlace = cursor.getString(endPlaceIndex);
                dataList.offer(startPlace + " - " + endPlace);

                if (dataList.size() > MAX_QUEUE_SIZE) {
                    dataList.poll();
                }
            } while (cursor.moveToNext());
        } else {
            // 데이터가 없음을 사용자에게 알림
            Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            Log.d("updateList", "커서가 데이터를 가리키지 않습니다.");
        }
        cursor.close(); // 커서 사용 후 닫기

        // 어댑터 업데이트
        adapter.clear();
        adapter.addAll(dataList);
        adapter.notifyDataSetChanged();
        Log.d("updateList", "리스트가 업데이트되었습니다: " + dataList.size());
    }

    private void dataMaxRows(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM recordPlace_DB", null);
        int rowCount = 0;
        if (cursor.moveToFirst()) {
            rowCount = cursor.getInt(0);
        }
        cursor.close();

        if (rowCount > MAX_QUEUE_SIZE) {
            String deleteQuery = "DELETE FROM recordPlace_DB WHERE time IN (SELECT MIN(time) FROM recordPlace_DB)";
            database.execSQL(deleteQuery);
        }
    }
}