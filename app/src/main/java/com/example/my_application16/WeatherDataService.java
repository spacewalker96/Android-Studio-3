package com.example.my_application16;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID  = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID  = "https://www.metaweather.com/api/location/";

    Context context;

    String cityID;

    public WeatherDataService(Context context) {
        this.context = context;
    }

//    public void getCityForecastByID(String s) {
//
//    }

    public interface VolleyResponseListener{
        void onError(String message);
        void onResponse(String cityID);
    }
    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener){

        String url = QUERY_FOR_CITY_ID + cityName ;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cityID = "";
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                volleyResponseListener.onResponse(cityID);
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyResponseListener.onError("Something wrong");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
//        return cityID;
    }

    public interface ForeCastByIDResponse{
        void onError(String message);

        void onResponce(List<WeatherReportModel> weatherReportModels);
    }



    public void  getCityForecastByID(String cityID, ForeCastByIDResponse foreCastByIDResponse ){

        List<WeatherReportModel>   weatherReportModel = new ArrayList<>();

        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityID;

        ////get the json object

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                try {
                    JSONArray consolodated_weather_list = response.getJSONArray("consolidated_weather");
                    ///////////////// get the first item in the array

                    WeatherReportModel  first_day = new WeatherReportModel();




                    for (int i=0; i< consolodated_weather_list.length(); i++) {
                        JSONObject first_day_form_api = (JSONObject) consolodated_weather_list.get(i);


                        first_day.setId(first_day_form_api.getInt("id"));
                        first_day.setWeather_state_name(first_day_form_api.getString("weather_state_name"));
                        first_day.setWeather_state_abbr(first_day_form_api.getString("weather_state_abbr"));
                        first_day.setWind_direction_compass(first_day_form_api.getString("wind_direction_compass"));
                        first_day.setCreated(first_day_form_api.getString("created"));
                        first_day.setApplicable_date(first_day_form_api.getString("applicable_date"));
                        first_day.setMin_temp(first_day_form_api.getLong("min_temp"));
                        first_day.setMax_temp(first_day_form_api.getLong("max_temp"));
                        first_day.setThe_temp(first_day_form_api.getLong("the_temp"));
                        first_day.setWind_speed(first_day_form_api.getLong("wind_speed"));
                        first_day.setWind_direction(first_day_form_api.getLong("wind_direction"));
                        first_day.setAir_pressure(first_day_form_api.getInt("air_pressure"));
                        first_day.setHumidity(first_day_form_api.getInt("humidity"));
                        first_day.setVisibility(first_day_form_api.getLong("visibility"));
                        first_day.setPredictability(first_day_form_api.getInt("predictability"));
                        weatherReportModel.add(first_day);

                    }

                    foreCastByIDResponse.onResponce(weatherReportModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);


        ////get the property called "consolodated_weather"


                ////get each item in the array and assign it to a new
    }

//    public List<weatherReportModel> getCityForecastByID(String cityID){
//
//    }
//
//    public List<weatherReportModel> getCityForecastByName(String cityName){
//
//    }

    public interface GetCityForecastByNameCallback{
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityforecasByName(String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback){
        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityID) {
                getCityForecastByID(cityID, new ForeCastByIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponce(List<WeatherReportModel> weatherReportModels) {
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });
    }
}
