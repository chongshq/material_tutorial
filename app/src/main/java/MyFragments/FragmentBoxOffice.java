package MyFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.qian.test.AdapterBoxOffice;
import com.example.qian.test.MyApplication;
import com.example.qian.test.R;
import com.example.qian.test.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import network.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentBoxOffice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBoxOffice extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String WEATHER_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private TextView volleyError;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Weather> listWeather=new ArrayList<>();
    private RecyclerView listWeathers;
    private AdapterBoxOffice adapterBoxOffice;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static String getRequestUrl(String cityName,int day){
        return WEATHER_URL+"q="+cityName+"&cnt="+day+"&appid="+ MyApplication.API_KEY;
    }



    public FragmentBoxOffice() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentBoxOffice.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentBoxOffice newInstance(String param1, String param2) {
        FragmentBoxOffice fragment = new FragmentBoxOffice();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("weatherstate",listWeather);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        volleySingleton=VolleySingleton.getsInstance();
        requestQueue=volleySingleton.getRequestQueue();


    }

    private void sendJsonRequest() {
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,getRequestUrl("Shanghai",5)
                ,new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                volleyError.setVisibility(View.GONE);
                Log.e("s",getRequestUrl("Shanghai",5));
                parseJSONResponse(response);
                adapterBoxOffice.setWeatherList(listWeather);
                //Weather._alert(getActivity(), response.toString());
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                volleyError.setVisibility(View.VISIBLE);
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    volleyError.setText("network error");
                }else if(error instanceof NetworkError){
                    volleyError.setText("network error");
                }

            }
        });
        requestQueue.add(request);
    }

    private ArrayList<Weather> parseJSONResponse(JSONObject response){
        if(response==null){
            return null;
        }
        try {
            StringBuilder data=new StringBuilder();
            JSONObject objectWeather = response.getJSONObject("city");
            String cityName=objectWeather.getString("name");
            data.append(cityName + ":\n");
            JSONArray arrayList =response.getJSONArray("list");

            for(int i=0;i<arrayList.length();i++){
                JSONObject current=arrayList.getJSONObject(i);
                JSONObject temperature= current.getJSONObject("temp");
                JSONArray weather= current.getJSONArray("weather");
                String temp_avg=temperature.getString("day");
                String temp_min=temperature.getString("min");
                String temp_max=temperature.getString("max");
                JSONObject weather_current=weather.getJSONObject(0);
                String weather_info=weather_current.getString("main");
                String pressure=current.getString("pressure");
                data.append(temp_avg + "\n"+temp_min + "\n"+temp_max + "\n"+weather_info + "\n"+pressure + "\n");
                Weather _weather = new Weather();
                _weather.setCity_name(cityName);

                _weather.setPressure(pressure);
                _weather.setTemp_max(temp_max);
                _weather.setTemp_min(temp_min);
                _weather.setTemperature(temp_avg);
                _weather.setWeather(weather_info);
                listWeather.add(_weather);
            }

            Weather._alert(getActivity(), listWeather.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listWeather;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_box_office, container, false);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        volleyError= (TextView) view.findViewById(R.id.textVolleyError);
        listWeathers= (RecyclerView) view.findViewById(R.id.listWeathers);
        listWeathers.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterBoxOffice=new AdapterBoxOffice(getActivity());
        listWeathers.setAdapter(adapterBoxOffice);
        if(savedInstanceState!=null){
            listWeather=savedInstanceState.getParcelableArrayList("weatherstate");
            adapterBoxOffice.setWeatherList(listWeather);
        }
        else {
            sendJsonRequest();
        }

        return view;
    }

    @Override
    public void onRefresh() {
        Weather._alert(getActivity(),"Loading...");

    }
}
