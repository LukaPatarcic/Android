package com.jelena.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<MovieItem> movieList = new ArrayList<>();
    String data;
    Button btn;
    EditText searchMovie;
    EditText searchYear;
    String searchMovieStr;
    String searchYearStr;
    String response;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btn = (Button)findViewById(R.id.btnSearch);
        searchMovie = (EditText)findViewById(R.id.searchInputMovie);
        searchYear = (EditText)findViewById(R.id.searchInputYear);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMovieStr = searchMovie.getText().toString();
                searchYearStr = searchYear.getText().toString();
                if(searchMovieStr.length() == 0) {
                    Toast.makeText(MainActivity.this, "Please enter something in the search field", Toast.LENGTH_LONG).show();
                } else {
                    FetchData fetchData = new FetchData(searchMovieStr, searchYearStr, MainActivity.this);
                    fetchData.execute();

                }
            }
        });


    }

    public Void formatJSON(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            response = jsonObject.getString("Response");
            if(response.equals("False")) {
                Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_LONG).show();
            } else {
                JSONArray jsonArray = jsonObject.getJSONArray("Search");
                movieList.clear();

                for(int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String title = jsonObject1.getString("Title");
                    String poster = jsonObject1.getString("Poster");
                    String year = jsonObject1.getString("Year");
                    String type = jsonObject1.getString("Type");

                    URL url = new URL(poster);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    movieList.add(new MovieItem(bmp, title,year,type));
                }

            }


            recyclerView = findViewById(R.id.list);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            adapter = new MovieAdapter(movieList);

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            movieList.remove(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
        }
    };


}
