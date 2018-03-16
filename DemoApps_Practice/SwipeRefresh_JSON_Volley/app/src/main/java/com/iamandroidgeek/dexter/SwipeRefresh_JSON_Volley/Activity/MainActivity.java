package com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.app.MyApplication;
import com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.helper.MyAdapter;
import com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.R;
import com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.model.Fish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnLoadMoreListener
                ,SwipeRefreshLayout.OnRefreshListener{
    private String TAG = "tag";
    private SwipeRefreshLayout swipeLayout;

    private ArrayList<Fish> fishList;
    private MyAdapter adapter;


//    public String TAG = "handle";

    int start;
    int end;

    JSONObject jsonObject;


    public String url = "https://script.googleusercontent.com/macros/echo?user_content_key=XIKOK7nLX1dnM0XkXK4D7KTY66p4cw5b4NrFD5DBc_XseaHsi6iI9RFXIfvXxouVumAXSVyslFEXv6MKbV37RB4DC7en_hyROJmA1Yb3SEsKFZqtv3DaNYcMrmhZHmUMWojr9NvTBuBLhyHCd5hHa1ZsYSbt7G4nMhEEDL32U4DxjO7V7yvmJPXJTBuCiTGh3rUPjpYM_V0PJJG7TIaKp1fWRIAlhIXxdflANm3o71jIFiswZfPdMwx2nrBEeeLFv6w7MnW8X78HKs4Akj_P6DJo29EdgbCP-QIVeBHLGdw&lib=MbpKbbfePtAVndrs259dhPT7ROjQYJ8yx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fishList = new ArrayList<>();
        swipeLayout = findViewById(R.id.swiperefresh);

        RecyclerView recyclerView = findViewById(R.id.rec_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(this);
        adapter.setLinearLayoutManager(layoutManager);
        adapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        start = adapter.getItemCount();
        end=start+4;

        swipeLayout.setOnRefreshListener(this);

        Log.i(TAG,"starting app .....\nstart = "+start+"\nend = "+end+"\nitems = "+adapter.getItemCount());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"starting activity .....\nstart = "+start+"\nend = "+end);
        fetchFishes();
    }

    @Override
    public void onRefresh() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                start=0;
                end=adapter.getItemCount()-1;
                swipeLayout.setRefreshing(true);
                Log.i(TAG,"refreshing .....\nstart = "+start+"\nend = "+end);
                fetchFishes();
            }
        });

    }

    public void fetchFishes() {

        Log.i(TAG,"Fetching items...\nstart = "+start+"\nend = "+end);

        swipeLayout.setRefreshing(true);
        fishList.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

//                if(response.length()>0) {
                    try {

                        jsonObject = new JSONObject(response.toString());


                        JSONArray fish = response.getJSONArray("fish");
                        Log.i(TAG, "length of jason = "+String.valueOf(fish.length()));
                        for (int i = start ; i <= end; i++) {
                            JSONObject fish1 = fish.getJSONObject(i);
                            String name = fish1.getString("fish_name");
                            String fish_img = fish1.getString("fish_img");

                            Log.i(TAG, fish1.toString());

                            fishList.add( new Fish(name,fish_img));
                        }

                    } catch (JSONException e) {
                        swipeLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

//                }
                adapter.addAll(fishList);
                Log.i(TAG,"items = "+adapter.getItemCount());
                swipeLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                swipeLayout.setRefreshing(false);
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);

    }

    @Override
    public void onLoadMore() {
        Log.i(TAG,"loading more..........");
        adapter.setProgressMore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fishList.clear();
                adapter.setProgressMore(false);
                start = adapter.getItemCount();
                int end = start + 4;
                Log.i(TAG, "start = "+String.valueOf(start)+"\nend = "+end);


                try {
                    JSONArray jArray = jsonObject.getJSONArray("fish");

                    for (int i = start; i <= end; i++) {

                        if (i == jArray.length())
                            break;

                        JSONObject jObject = jArray.getJSONObject(i);
                        String name = jObject.getString("fish_name");
                        String fish_img = jObject.getString("fish_img");
                        fishList.add(new Fish(name,fish_img));
                        Log.i(TAG,jObject.toString());

                    }

                } catch (JSONException e) {
//                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    Log.i(TAG,e.toString());

                }
                adapter.addItemMore(fishList);
                Log.i(TAG,"items = "+adapter.getItemCount());
                adapter.setMoreLoading(false);
            }
        },2000);

    }
}



