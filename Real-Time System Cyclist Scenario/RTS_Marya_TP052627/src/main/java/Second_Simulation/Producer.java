package Second_Simulation;

import Second_Simulation.Queue.Exchange;
import com.rabbitmq.client.ConnectionFactory;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//PRODUCER THREAD: SIMULATION TWO
public class Producer implements Runnable {
    public static void main(String[] args) {
       ReentrantLock lock = new ReentrantLock(); //INITIALIZING REENTRANT LOCK TO PASS IN THE PRODUCER PARAMETER

        //SCHEDULED EXECUTOR TO PASS IN THREE THREADS OF PRODUCER WITH DELAY TIME OF 0,1, AND 2 MINUTES BETWEEN EACH INTERVAL
        ScheduledExecutorService sch = Executors.newScheduledThreadPool(3);
        sch.schedule(new Producer("Producer : ",lock),0,TimeUnit.MINUTES);
        sch.schedule(new Producer("Producer 2: ",lock),1,TimeUnit.MINUTES);
        sch.schedule(new Producer("Producer 3: ",lock),2,TimeUnit.MINUTES);
        sch.shutdown();
    }
    static HttpResponse<String> response; //DECLARING HTTP RESPONSE
    Exchange e = new Exchange(); //INITIALIZING NEW EXCHANGE CLASS TO CALL THE TRANSFER METHOD
    ConnectionFactory factory; //DECLARING CONNECTION FACTORY
    ArrayList<String> countries = new ArrayList<>();
    String data;
    String name;
    private ReentrantLock lock; //DECALRING PRIVATE REENTRANT LOCK

    public Producer(String name,ReentrantLock lock){ //PRODUCER CONSTRUCTOR TO PASS IN NAME AND LOCK
        factory = new ConnectionFactory();
        this.name=name;
        this.lock=lock;
    }

    @Override
    public void run() {
        try {
            lock.lock(); //LOCKING THE SECTION, AS TO ONLY ONE PRODUCER GETS THE LOCK, FINISHES THE TASK AND UNLOCKS IT
           System.out.println(name+ "has the key now..");
            Thread.sleep(2000);
            data = retrieve(); //CALLING RETRIEVE METHOD
            System.out.println(data);
            e.TransferQ(data,factory); //PASSING INTO TRANSFER METHOD WITH THE FACTORY
            lock.unlock(); //UNLOCKING

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

    }

    public String retrieve() throws IOException, InterruptedException {
        //RETRIEVE METHOD TO GET THE DATA FROM EXTERNAL API

        countries.add("London");
        countries.add("Malaysia");
        countries.add("Sweden");
        countries.add("India");
        countries.add("Iceland");
        countries.add("Dubai");
        //RANDOM COUNRTY GENERATOR
        String city = countries.get(new Random().nextInt(countries.size()));

        String API_KEY = "4f31b82599214bc99fd84337211406"; //API KEY

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://api.weatherapi.com/v1/current.json?key="+API_KEY+"&q="+city))
                .header("x-rapidapi-key", "c0a1d03405msh67f6003c5aa906fp1bb172jsn1e6820452617")
                .header("x-rapidapi-host", "airport-info.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(name+ "retrieved the data...");
        Thread.sleep(2000);
        System.out.println("Sending the data to the consumer..");

        return response.body(); //RETURNING THE RESPONSE BODY


    }
    //BENCHMARK CLASS
    @State(Scope.Benchmark)
    public static class testbenchmark{

        ReentrantLock l; //DECLARING LOCK
        ReentrantLock l2;

        @Setup(Level.Trial)
        public void innit(){
            l= new ReentrantLock(); //INITIALIZING LOCK
            l2 = new ReentrantLock();
            new Producer("p",l); //INITIALING CLASS
            new Consumer("c",l2);

        }

        @Benchmark
        @BenchmarkMode(Mode.Throughput)
        // @Measurement(iterations = 3)
        @Fork(value = 1)
        @OutputTimeUnit(TimeUnit.SECONDS)
        @Warmup(iterations = 1)
        public void test(testbenchmark r){
            //STARTING THREADS
            new Thread(new Producer("p",r.l)).start();
            new Thread(new Consumer("c",r.l2)).start();


        }

    }
}
