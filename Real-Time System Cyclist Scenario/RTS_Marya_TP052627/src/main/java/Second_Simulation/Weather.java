package Second_Simulation;

import java.io.Serializable;
//WEATHER CLASS TO SAVE AND GET DATA LOCALLY, IMPLEMENTNG SERIALIZABLE

public class Weather implements Serializable {
    private String name, country, date,id;
    private int windD, humidity,cloud;
    Double windmph,temp;
    String data;

    //WEATHER CONSTRUCTOR

    public Weather(String name,String date, Double temp, int humidity,String id,
                   int windD,int cloud,Double windmph){
        this.name=name;
        //  this.country=country;

        this.date=date;

        this.humidity=humidity;
        this.id=id;
        this.windD=windD;
        this.cloud=cloud;

        this.cloud=cloud;
        this.windmph=windmph;
        this.temp=temp;

    }

    //SETTER AND GETTERS
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWindD() {
        return windD;
    }

    public void setWindD(int windD) {
        this.windD = windD;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getCloud() {
        return cloud;
    }

    public void setCloud(int cloud) {
        this.cloud = cloud;
    }

    public Double getWindmph() {
        return windmph;
    }

    public void setWindmph(Double windmph) {
        this.windmph = windmph;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
