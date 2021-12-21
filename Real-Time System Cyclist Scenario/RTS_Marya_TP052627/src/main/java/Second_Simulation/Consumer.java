package Second_Simulation;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

//CONSUMER THREAD
public class Consumer implements Runnable{
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock(); //INITIALIZING LOCK
        //SCHEDULING CONSUMERS TO A SPECIFIC DELAY TIME BETWEEN EACH INTERVAL AND PASSING LOCK

        ScheduledExecutorService sch = Executors.newScheduledThreadPool(3);
        sch.schedule(new Consumer(" Consumer 1 ",lock),0,TimeUnit.MINUTES);
        sch.schedule(new Consumer(" Consumer 2 ",lock),1,TimeUnit.MINUTES);
        sch.schedule(new Consumer(" Consumer 3 ",lock),2,TimeUnit.MINUTES);


    }
    //INITIALIZING LIST AND PASSING WEATHER CLASS TO GET AND SAVE THE DATA LOCALLY FROM API
    public List<Weather> All(String name, String date, Double temp, int humidity, String id,
                             int windD, int cloud, Double windmph) { //INITIALIZING NEW ARRAYLIST
        try {
            List<Weather> info = new ArrayList<>();
            info.add(new Weather(name,date,temp,humidity,id,windD,cloud,windmph));
            return info;
        } catch (Exception e) {
            return null;
        }
    }

    static String name; //DECLARING NAME
    static sharedExchange s = new sharedExchange(); //DECLARING SHARED EXCHANGE CLASS
    static LinkedBlockingQueue<String> get= new LinkedBlockingQueue(); //DECLARING LINKED BLOCKING QUEUE TO PUT THE RESPONSE BODY FROM PRODUCER
    private ReentrantLock lock; //DECLARING LOCK

    public Consumer(String name,ReentrantLock lock){ //CONSUMER CONSTRUCTOR
        this.name=name;
        this.lock=lock;
    }

    @Override
    public void run() {
        try {
            try {
                //CONSUMER LOCKING AND UNLOCKING AFTER FINISHING SPECIFIED TASKS
                lock.lock();
                factoryConsume(); //RABBIT MQ CONSUMER SIDE FOR RECIEVING THE RESPONSE BODY
                processData(); //CALLING METHOD
            }finally {
                lock.unlock(); //UNLOCKING
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {

        }
    }
    public void factoryConsume() throws IOException, TimeoutException, JSONException, ParseException, InterruptedException {
        //METHOD TO RECIEVE DATA FROM PRODUER VIA RABBIT MQ ON SHARED EXCHANGE NAME
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Read user input
        channel.exchangeDeclare(s.EXCHANGE_NAME, "direct");

        String queueName = channel.queueDeclare().getQueue();
        // String queueName2 = channel.queueDeclare().getQueue();
        //String queueName3 = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, sharedExchange.EXCHANGE_NAME, "city");
        //channel.queueBind(queueName2,sharedExchange.EXCHANGE_NAME,"city1");
        //channel.queueBind(queueName3,sharedExchange.EXCHANGE_NAME,"city2");

        channel.basicConsume(queueName, true, (consumerTag, message) -> {
            String m = new String(message.getBody(), "UTF-8");
           System.out.println("Time received: " +java.time.LocalTime.now().withNano(0));
           System.out.println(name +"received the data: " +m);
            try {
                get.put(m); //PUTTING THE MESSAGE RECIEVED INTO LINKED BLOCKIN QUEUE
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, ConsumerTag -> {

        });
        Thread.sleep(1000);

    }

   public void processData() throws InterruptedException, JSONException {

            //METHOD TO PROCESS ANF GET SPECIFIED DATA FROM JSON STRING RECIEVED FROM PRODUCER
           String data = get.take();
           System.out.println("Time received: " +java.time.LocalTime.now().withNano(0));
           System.out.println(name +"received the data: " +data);
           JSONObject my = new JSONObject(data);
           String str = (String) my.getJSONObject("location").get("name");
           //System.out.println("LOCATION: "+ str);
           String str1 = (String) my.getJSONObject("location").get("tz_id");
           //System.out.println("ID: "+str1);
           String str2 = (String) my.getJSONObject("location").get("localtime");
           //System.out.println("Local Time "+str2);
           Integer str3 = (Integer) my.getJSONObject("current").get("humidity");
           //System.out.println("HUMIDITY "+str3);
           Integer str5 = (Integer) my.getJSONObject("current").get("wind_degree");
           // System.out.println("WIND: "+str5);
           Integer str4 = (Integer) my.getJSONObject("current").get("cloud");
           //System.out.println("CLOUD: "+str4);
           Double str6 = (Double) my.getJSONObject("current").get("wind_mph");
           // System.out.println("WIND mph: "+str6);
           Double str7 = (Double) my.getJSONObject("current").get("temp_c");
           // System.out.println("TEMPERATURE: "+str7);

           for (Weather w : All(str, str1, str7, str3, str2, str5, str4, str6)) {
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " LOCATION --> " + w.getName());
               Thread.sleep(1000);
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " ID: " + w.getDate());
               Thread.sleep(1000);
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " LOCAL TIME: " + w.getId());
               Thread.sleep(1000);
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " HUMIDITY: " + w.getHumidity());
               Thread.sleep(1000);
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " WIND DEGREE: " + w.getWindD());
               Thread.sleep(1000);
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " CLOUD: " + w.getCloud());
               Thread.sleep(1000);
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " WIND MPH: " + w.getWindmph());
               Thread.sleep(1000);
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " TEMPERATURE: " + w.getTemp());
               Thread.sleep(2000);

               System.out.println(java.time.LocalTime.now().withNano(0) + name + " PROCESSING INFORMATION...");
               Thread.sleep(2000);
               System.out.println(java.time.LocalTime.now().withNano(0) + name + " CALCULATING....");
               Thread.sleep(2000);

                //CALCULATING CURRENT WEATHER FOR SUITABILITY FOR CYCLISTS
               if (w.getTemp() > 30 && w.getWindmph() > 20 && w.getHumidity() > 50) {
                   System.out.println(java.time.LocalTime.now().withNano(0) + name + " The weather is unsuitable for biking..");
               } else if (w.getHumidity() > 50) {
                   System.out.println(java.time.LocalTime.now().withNano(0) + name + " Weather is humid");
                   System.out.println("but suitable for biking");
               } else if (w.getTemp() < 10) {
                   System.out.println(w.getTemp() + " temperature is too cold for biking..");
               } else if (w.getTemp() == 40) {
                   System.out.println(w.getTemp() + " temperature is too hot for biking..");
               } else {
                   System.out.println(java.time.LocalTime.now().withNano(0) + name + " Weather is suitable for biking..");
               }
           }
       }
       }




