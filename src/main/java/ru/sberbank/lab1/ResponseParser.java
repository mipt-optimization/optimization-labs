package ru.sberbank.lab1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseParser {
    private final String response;

    ResponseParser(String response) {
        this.response = response;
    }

    public Double extractTemperature() throws JSONException {
        JSONObject json = new JSONObject(response);
        String hourly = json.getString("hourly");
        JSONArray data = new JSONObject(hourly).getJSONArray("data");

        return new JSONObject(data.get(0).toString()).getDouble("temperature");
    }
}
