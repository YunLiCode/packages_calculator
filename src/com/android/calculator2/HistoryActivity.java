package com.android.calculator2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HistoryActivity extends Activity implements OnClickListener {
    private static String TAG = "Calculator";

    private ListView mList;
    private Button mGobackButton;
    private Button mClearButton;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        Log.d(TAG, "History onCreate");
        sp = getSharedPreferences("historySp", MODE_PRIVATE);
        editor = sp.edit();

        mList = (ListView) findViewById(R.id.history_list);
        mGobackButton = (Button) findViewById(R.id.goback_bt);
        mClearButton = (Button) findViewById(R.id.clear_bt);

        mGobackButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        showList();
    }

    private void showList() {

        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        int size = sp.getInt("size", 0);
        for (int i = size; i >= 1; i--) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            
            String result = sp.getString("index:" + i, null);
            String expr = sp.getString(result, null);
            listItem.put("historyExpr", expr);
            listItem.put("historyResult", "=" + result);
            
            listItems.add(listItem);
        }
        // Map<String, String> historyMap = (Map<String, String>) sp.getAll();
        // Object[] results = historyMap.keySet().toArray();
        // for (int i = 0; i < results.length; i++) {
        // Log.d(TAG, "result : " + results[i] + " expr : " +
        // historyMap.get(results[i]));
        // Log.d(TAG, "result : " + results[i] + " expr : " +
        // historyMap.get(results[i]));
        // }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.history_item, new String[] { "historyExpr",
                        "historyResult" }, new int[] { R.id.historyExpr,
                        R.id.historyResult });
        mList.setAdapter(simpleAdapter);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.goback_bt:
            onBackPressed();
            break;

        case R.id.clear_bt:
            clearHistory();
            break;
        default:
            break;
        }
    }

    private void clearHistory() {
        editor.clear();
        editor.commit();
        showList();
    }
}
