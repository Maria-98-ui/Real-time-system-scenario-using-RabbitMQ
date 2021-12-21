package First_Simulation;

import org.json.JSONObject;
import org.json.JSONException;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

//CONSUMER THREAD
public class Consumer implements Runnable {
    //INITIALIZING LIST AND PASSING WEATHER CLASS TO GET AND SAVE THE DATA LOCALLY FROM API
    public List<Weather> All(String name, String date, Double temp, int humidity, String id,
                             int windD, int cloud, Double windmph) {
        try {
            List<Weather> info = new ArrayList<>(); //INITIALIZING NEW ARRAYLIST
            info.add(new Weather(name, date, temp, humidity, id, windD, cloud, windmph)); //ADDING REQUIRED INFO
            return info;
        } catch (Exception e) {
            return null;
        }
    }
    String name; //DECLARING NAME
    LinkedBlockingQueue<HttpResponse<String>> takeresponse; //DECLARING SHARED QUEUE
    HttpResponse<String> response; //DECLARING HTTP RESPONSE
    public Consumer(String name, LinkedBlockingQueue takeresponse){ //CONSUMER CONSTRUCTOR, PASSING NAME AND SHARED QUEUE
        this.name=name;
        this.takeresponse=takeresponse;

    }

    @Override
    public void run() {
        try {
            processData(); //CALLING PROCESSDATA METHOD
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //METHOD TO TAKE RESPONSE FROM THE PRODUCER AND FETCHING REQUIRED DATA FROM API USING org.JSON
    public void processData() throws InterruptedException, JSONException {
        response = takeresponse.take(); //TAKING RESPONSE RETRIEVED BY THE PRODUCER


        System.out.println("Time received: " + java.time.LocalTime.now().withNano(0));
        Thread.sleep(2000);
        System.out.println(java.time.LocalTime.now().withNano(0) + name + " received the data: " + response.body());

        Thread.sleep(1000);
        JSONObject my = new JSONObject(response.body()); //INITIALIZING JSONObject AND PASSING RESPONSE BOBY
        //GETTING SPECIFIED DATA FROM API
        String str = (String) my.getJSONObject("location").get("name");
        String str1 = (String) my.getJSONObject("location").get("tz_id");
        String str2 = (String) my.getJSONObject("location").get("localtime");
        Integer str3 = (Integer) my.getJSONObject("current").get("humidity");
        Integer str5 = (Integer) my.getJSONObject("current").get("wind_degree");
        Integer str4 = (Integer) my.getJSONObject("current").get("cloud");
        Double str6 = (Double) my.getJSONObject("current").get("wind_mph");
        Double str7 = (Double) my.getJSONObject("current").get("temp_c");
        //LOOPING THROUGH THE WEATHER CLASS LIST AND GETTING THE DATA
        for (Weather w : All(str, str1, str7, str3, str2, str5, str4, str6)) {
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "LOCATION --> " + w.getName());
            Thread.sleep(2000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "ID --> " + w.getDate());
            Thread.sleep(2000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "LOCAL TIME --> " + w.getId());
            Thread.sleep(2000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "HUMIDITY --> " + w.getHumidity());
            Thread.sleep(2000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "WIND DEGREE -->" + w.getWindD());
            Thread.sleep(2000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "CLOUD --> " + w.getCloud());
            Thread.sleep(2000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "WIND MPH --> " + w.getWindmph());
            Thread.sleep(2000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "TEMPERATURE --> " + w.getTemp());
            Thread.sleep(2000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "PROCESSING INFORMATION...");
            Thread.sleep(3000);
            System.out.println(java.time.LocalTime.now().withNano(0) + name + "CALCULATING....");
            Thread.sleep(3000);
            //CALCULATING CURRENT WEATHER SUITABILITY FOR CYCLIST

            if (w.getTemp() > 30 && w.getWindmph() > 20 && w.getHumidity() > 50) {
                System.out.println(java.time.LocalTime.now().withNano(0) + name +":" + "The weather is unsuitable for biking..");
            } else if (w.getHumidity() > 50) {
                System.out.println(java.time.LocalTime.now().withNano(0) + name +":" + "Weather is humid");
                System.out.println(java.time.LocalTime.now().withNano(0) + " but suitable for biking");
            } else if (w.getTemp() < 10) {
                System.out.println(w.getTemp() + " temperature is too cold for biking..");
            } else if (w.getTemp() == 40) {
                System.out.println(w.getTemp() + " temperature is too hot for biking..");
            } else {
                System.out.println(java.time.LocalTime.now().withNano(0) + name +":" + "Weather is suitable for biking..");
            }

        }
    }


}

