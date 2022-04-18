package com.example.pharminder_2_0;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSonParser {
    private HashMap<String, String> parseJsonObject(JSONObject object){
        //Inicializamos HASHMAP
        HashMap<String,String> datalist = new HashMap<>();
        try{
            //Get name from object
            String name = object.getString("name");
            //get latitude from object
            String latitude = object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            //get longitud from object
            String longitude = object.getJSONObject("geometry").getJSONObject("location").getString("lng");
            //Put the values in HashMap
            datalist.put("Name",name);
            datalist.put("lat", latitude);
            datalist.put("lng", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datalist;
    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        //Initialize   Hash Map list
        List<HashMap<String,String>> dataList = new ArrayList<>();
        for (int i =0; i< jsonArray.length(); i++){
            try {
                //Initize Hash Map
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                //añadimos los datos en hash map list
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //return hash map list
        return dataList;
    }

    public List<HashMap<String,String>> parseResult (JSONObject object){
        //Inicializamos json Array
        JSONArray jsonArray = null;
        //get result array
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }

}







