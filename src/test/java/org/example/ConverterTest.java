package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {

    @Test
    void getAllCurrencies() throws IOException, ParseException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("curl", "-X", "GET", "http://apilayer.net/api/list?access_key=3d5d34d453b47c5045d66c9405507f4a");
        Process process = processBuilder.start();
        process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder line1 = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            line1.append(line);
        }
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(line1.toString());
        jsonObject= (JSONObject) jsonObject.get("currencies");
        var keys =jsonObject.keySet().iterator();
        HashMap<String, String> currencies=new HashMap<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = String.valueOf(jsonObject.get(key));
            currencies.put(key, value);
        }
        assertEquals(currencies,Converter.getAllCurrencies());
    }

    @Test
    void convertHistorical() throws InterruptedException, IOException, ParseException {

        ProcessBuilder processBuilder = new ProcessBuilder("curl", "-X", "GET", "\"http://apilayer.net/api/historical" +
                "?access_key=3d5d34d453b47c5045d66c9405507f4a&source=USD&currencies=RUB&date=2015-01-15");
        Process process = processBuilder.start();
        process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder line1 = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            line1.append(line);
        }
        JSONParser jsonParser=new JSONParser();
        JSONObject object=(JSONObject)jsonParser.parse(line1.toString());
        JSONObject currenciesJSON = (JSONObject) object.get("quotes");
        Double rate= (Double) currenciesJSON.get("USDRUB");
        assertEquals( rate*100,Converter.convertHistorical("USD","RUB", 100D, "2015-01-01"));
    }
    @Test
    void convertLive() throws InterruptedException, IOException, ParseException {

        ProcessBuilder processBuilder = new ProcessBuilder("curl", "-X", "GET", "\"http://apilayer.net/api/historical" +
                "?access_key=3d5d34d453b47c5045d66c9405507f4a&source=USD&currencies=RUB&date=2015-01-15");
        Process process = processBuilder.start();
        process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder line1 = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            line1.append(line);
        }
        JSONParser jsonParser=new JSONParser();
        JSONObject object=(JSONObject)jsonParser.parse(line1.toString());
        JSONObject currenciesJSON = (JSONObject) object.get("quotes");
        Double rate= (Double) currenciesJSON.get("USDRUB");
        assertEquals( rate*100,Converter.convertHistorical("USD","RUB", 100D, "2015-01-01"));

    }
}