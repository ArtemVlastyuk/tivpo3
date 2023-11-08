package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

class Converter{
    public static HashMap<String, String> currencies=null;

    public static HashMap<String, String> getAllCurrencies() {

        String url = "http://apilayer.net/api/list?access_key=3d5d34d453b47c5045d66c9405507f4a";
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder line1 = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    line1.append(line);
                }
                System.out.println(line1);
                JSONParser jsonParser=new JSONParser();
                JSONObject object=(JSONObject)jsonParser.parse(line1.toString());
                JSONObject currenciesJSON = (JSONObject) object.get("currencies");
                currencies=new Gson().fromJson(currenciesJSON.toString(), HashMap.class);

//                System.out.println(currencies);
            }

        }
        catch (Exception e){
            System.out.println("ERROR!!!");
            e.printStackTrace();

        }
        return currencies;
    }
    public static Double convertLive(String from, String to, Double amount) {

        String url = "http://apilayer.net/api/live?access_key=3d5d34d453b47c5045d66c9405507f4a&source="+from;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder line1 = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    line1.append(line);
                }
                JSONParser jsonParser=new JSONParser();
                JSONObject object=(JSONObject)jsonParser.parse(line1.toString());
                JSONObject currenciesJSON = (JSONObject) object.get("quotes");
                Double rate= (Double) currenciesJSON.get(from+to);
                return rate*amount;

//                System.out.println(currencies);
            }

        }
        catch (Exception e){
            System.out.println("ERROR!!!");
            e.printStackTrace();

        }
        return null;
    }


    public static Double convertHistorical(String from, String to, Double amount, String date) {
        String url = "http://apilayer.net/api/historical?access_key=3d5d34d453b47c5045d66c9405507f4a&source="+from+"&currencies="+to+"&date="+date;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder line1 = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    line1.append(line);
                }
                JSONParser jsonParser=new JSONParser();
                JSONObject object=(JSONObject)jsonParser.parse(line1.toString());
                JSONObject currenciesJSON = (JSONObject) object.get("quotes");
                Double rate= (Double) currenciesJSON.get(from+to);
                return rate*amount;

//                System.out.println(currencies);
            }

        }
        catch (Exception e){
            System.out.println("ERROR!!!");
            e.printStackTrace();

        }
        return null;
    }


    public static void extrapolateExchangeRate(List<Double> exchangeRates) {
        WeightedObservedPoints points = new WeightedObservedPoints();
        System.out.println(exchangeRates);
        // Заполняем известные значения курса
        for (int i = 1; i <= exchangeRates.size(); i++) {
            points.add(i, exchangeRates.get(i - 1));
        }

        // Выбираем степень полинома для аппроксимации
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);

        // Вычисляем коэффициенты полинома для аппроксимации
        double[] coefficients = fitter.fit(points.toList());

        // Создаем объект полинома на основе полученных коэффициентов
        PolynomialFunction function = new PolynomialFunction(coefficients);

        // Экстраполируем значения для следующих дней
        double nextDay = function.value(exchangeRates.size() + 1); // Экстраполяция на следующий день
        double dayAfterNext = function.value(exchangeRates.size() + 2); // Экстраполяция на послезавтра

        System.out.println("Прогноз курса на следующий день: " + nextDay);
        System.out.println("Прогноз курса на послезавтра: " + dayAfterNext);

    }

    public static void convertForNextDays(String from, String to) {
        String endDate= LocalDate.now().toString();
        String startDate= LocalDate.now().minusMonths(1).toString();
        String url = "http://api.currencylayer.com/timeframe?access_key=3d5d34d453b47c5045d66c9405507f4a&start_date="+startDate+"&end_date="+endDate+"&currencies="+to+"&source="+from;
        System.out.println(url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder line1 = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    line1.append(line);
                }
                JSONParser jsonParser=new JSONParser();
                JSONObject object=(JSONObject)jsonParser.parse(line1.toString());
                JSONObject currenciesJSON = (JSONObject) object.get("quotes");
                List<JSONObject> rareString=  currenciesJSON.values().stream().toList();
                List<Double> rateDoudle=new ArrayList<>();
                for(int i=0;i<rareString.size();i++){

                    rateDoudle.add((Double) rareString.get(i).values().stream().toList().get(0));
                }
//                System.out.println(rateDoudleList);
                extrapolateExchangeRate(rateDoudle);


//                System.out.println(currencies);
            }

        }
        catch (Exception e){
            System.out.println("ERROR!!!");
            e.printStackTrace();

        }

    }
}

public class Main {

    public static void main(String[] args) throws InterruptedException {

        int k=1;
        Scanner scanner=new Scanner(System.in);
        while (k!=0) {
            System.out.println("Вырерите:\n1. Перевод валют на данный момент\n2. Исторический перевод валют\n3. Предсказание курса\n4. Выход ");
            System.out.print("Ввод: ");
            k=scanner.nextInt();
            if(k==1){
                String from;
                String to;
                System.out.println("Из какой валюты перевод?");
                Map<String, String> currencies;
                if(Converter.currencies==null){
                   currencies=Converter.getAllCurrencies();
                }
                else {
                    currencies=Converter.currencies;
                }
                List<String> currenciesKeyList=currencies.keySet().stream().toList();
                List<String> currenciesValueList=currencies.values().stream().toList();
                for(int i=0;i<currencies.size();i++){
                    System.out.println(i+". "+currenciesKeyList.get(i)+" - "+currenciesValueList.get(i));
                }
                System.out.print("Ввод: ");
                k=scanner.nextInt();
                from=currenciesKeyList.get(k);
                System.out.println("В какую валюту перевод?");
                for(int i=0;i<currencies.size();i++){
                    System.out.println(i+". "+currenciesKeyList.get(i)+" - "+currenciesValueList.get(i));
                }
                System.out.print("Ввод: ");
                k=scanner.nextInt();
                to=currenciesKeyList.get(k);
                System.out.print("Сумма: ");
                Double amount= scanner.nextDouble();
                System.out.println("\nРезультат (Из "+from+" в "+to+"): "+Converter.convertLive(from, to, amount));
                Thread.sleep(3000);
                System.out.println("\n");
            }
            else if(k==2){
                String from;
                String to;
                System.out.println("Из какой валюты перевод?");
                Map<String, String> currencies;
                if(Converter.currencies==null){
                    currencies=Converter.getAllCurrencies();
                }
                else {
                    currencies=Converter.currencies;
                }
                List<String> currenciesKeyList=currencies.keySet().stream().toList();
                List<String> currenciesValueList=currencies.values().stream().toList();
                for(int i=0;i<currencies.size();i++){
                    System.out.println(i+". "+currenciesKeyList.get(i)+" - "+currenciesValueList.get(i));
                }
                System.out.print("Ввод: ");
                k=scanner.nextInt();
                from=currenciesKeyList.get(k);
                System.out.println("В какую валюту перевод?");
                for(int i=0;i<currencies.size();i++){
                    System.out.println(i+". "+currenciesKeyList.get(i)+" - "+currenciesValueList.get(i));
                }
                System.out.print("Ввод: ");
                k=scanner.nextInt();
                to=currenciesKeyList.get(k);
                System.out.print("Сумма: ");
                Double amount= scanner.nextDouble();
                System.out.print("Дата начиная с 1999 г. в формате YYYY-MM-DD: ");
                String date=scanner.next();

                System.out.println("\nРезультат (Из "+from+" в "+to+"): "+Converter.convertHistorical(from, to, amount, date));
                Thread.sleep(3000);
                System.out.println("\n");
            }
            else if(k==3){
                String from;
                String to;
                System.out.println("Из какой валюты перевод?");
                Map<String, String> currencies;
                if(Converter.currencies==null){
                    currencies=Converter.getAllCurrencies();
                }
                else {
                    currencies=Converter.currencies;
                }
                List<String> currenciesKeyList=currencies.keySet().stream().toList();
                List<String> currenciesValueList=currencies.values().stream().toList();
                for(int i=0;i<currencies.size();i++){
                    System.out.println(i+". "+currenciesKeyList.get(i)+" - "+currenciesValueList.get(i));
                }
                System.out.print("Ввод: ");
                k=scanner.nextInt();
                from=currenciesKeyList.get(k);
                System.out.println("В какую валюту перевод?");
                for(int i=0;i<currencies.size();i++){
                    System.out.println(i+". "+currenciesKeyList.get(i)+" - "+currenciesValueList.get(i));
                }
                System.out.print("Ввод: ");
                k=scanner.nextInt();
                to=currenciesKeyList.get(k);


                Converter.convertForNextDays(from, to);
                Thread.sleep(3000);
                System.out.println("\n");
            }
        }
    }
}