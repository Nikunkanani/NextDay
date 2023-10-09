package com.example.nextday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nextday.Adpter.UserAdapter;
import com.example.nextday.Model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PREFS_NAME = "MyPrefs";
    private static final String FAVORITE_KEY = "favoriteKey";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<User> favoriteList;

    private  int a = 1;

    NestedScrollView nestedSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        nestedSV = findViewById(R.id.linearLayout2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        favoriteList = new ArrayList<>();


        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                /*if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    count++;
                    // on below line we are making our progress bar visible.
                    loadingPB.setVisibility(View.VISIBLE);
                    getPatentDefultList( search,count, startDate, date);
                }*/

              /*  if (scrollY < oldScrollY) {
                    Log.i("TAG", "Scroll UP");
                    count--;
                    // on below line we are making our progress bar visible.
                    loadingPB.setVisibility(View.VISIBLE);
                    getPatentDefultList(search, count, startDate, date);
                }*/
                if (scrollY > oldScrollY) {
                    Log.i("TAG", "Scroll DOWN");
                    a++;
                    // on below line we are making our progress bar visible.

                    fetchUsers();

                }

                   /* if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                scrollY > oldScrollY) {
                            //code to fetch more data for endless scrolling
                            count++;
                            // on below line we are making our progress bar visible.
                            loadingPB.setVisibility(View.VISIBLE);
                            getPatentDefultList(search, count, startDate, date);
                        }
                    }*/

            }
        });

        userAdapter = new UserAdapter(userList, favoriteList, new UserAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(User user) {
                if (favoriteList.contains(user)) {
                    favoriteList.remove(user);
                    saveFavoriteList();
                    Toast.makeText(MainActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteList.add(user);
                    saveFavoriteList();
                    Toast.makeText(MainActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                }
                userAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(userAdapter);

        loadFavoriteList();
        fetchUsers();
    }


    private void fetchUsers() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://reqres.in/api/users?page="+ a)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject userObject = jsonArray.getJSONObject(i);
                            int id = userObject.getInt("id");
                            String firstName = userObject.getString("first_name");
                            String lastName = userObject.getString("last_name");
                            String avatar = userObject.getString("avatar");

                            User user = new User(id, firstName, lastName, avatar);
                            userList.add(user);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userAdapter.notifyDataSetChanged();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                Log.e(TAG, "Error: " + e.getMessage());

            }

        });
    }


    private void saveFavoriteList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoriteList);
        editor.putString(FAVORITE_KEY, json);
        editor.apply();
    }

    private void loadFavoriteList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FAVORITE_KEY, null);
        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        favoriteList = gson.fromJson(json, type);

        if (favoriteList == null) {
            favoriteList = new ArrayList<>();
        }
    }
}