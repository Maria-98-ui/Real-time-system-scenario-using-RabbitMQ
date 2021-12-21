package First_Simulation;

import com.sun.jdi.Value;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//CURRENT WEATHER CHECKER SIMULATION USING LINKEDBLOCKINGQUEUE AND FIXED THREAD POOL
public class Producer implements Runnable {

    public static void main(String[] args) {
        //INITIALIZING HTTP LINKED BLOCKINGQUEUE FOR SHARED QUEUE
        LinkedBlockingQueue<HttpResponse<String>> queue = new LinkedBlockingQueue<>();

        ExecutorService e = Executors.newFixedThreadPool(7); //PASSING 7 THREADS INTO THE FIXED THREAD POOL

        e.submit(new Producer(queue, "Producer: ")); //SUBMITTING PRODUCER AND PASSING SHARED QUEUE
        e.submit(new Consumer(" Consumer 1: ", queue)); ////SUBMITTING 6 CONSUMER AND PASSING SHARED QUEUE
        e.submit(new Consumer(" Consumer 2: ", queue));
        e.submit(new Consumer(" Consumer 3: ", queue));
        e.submit(new Consumer(" Consumer 4: ", queue));
        e.submit(new Consumer(" Consumer 5: ", queue));
        e.submit(new Consumer(" Consumer 6: ", queue));

        e.shutdown(); //SHUTTING DOWN EXECUTOR SERVICE

    }

    HttpResponse<String> response; //DECLARING HTTPRESPONSE
    LinkedBlockingQueue<HttpResponse<String>> q; //DECLARING LINKED BLOCKINGQUEUE
    String name; //DECLARING STRING NAME
    ArrayList<String> countries = new ArrayList<>(); //INITIALIZING NEW ARRAYLIST FOR COUNTRIES

    public Producer(LinkedBlockingQueue q, String name) { //PRODUCER CONSTRUCTOR AND PASSING LINKED BLOCKINGQUEUE AND STRING NAME
        this.name = name;
        this.q = q;
    }


    public void run() {
        try {
            retrieve(); //CALLING RETRIEVE METHOD
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //RETRIEVE METHOD TO SEND HTTP REQUEST TO THE EXTERNAL API
    public void retrieve() throws InterruptedException, IOException {
        //ADDING SPECIFIED COUNTRIES INTO THE ARRAYLIST
        countries.add("London");
        countries.add("Malaysia");
        countries.add("Sweden");
        countries.add("India");
        countries.add("Iceland");
        countries.add("Dubai");

        //SETTING RANDOM GENERATOR FOR THE COUNTRIES AS TO PASS RANDOMLY SELECTED COUNTRY INTO THE API
        String c = countries.get(new Random().nextInt(countries.size()));

        String API_KEY = "4f31b82599214bc99fd84337211406"; //API KEY

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=" + c))
                .header("x-rapidapi-key", "c0a1d03405msh67f6003c5aa906fp1bb172jsn1e6820452617")
                .header("x-rapidapi-host", "airport-info.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(name + "retrieved" + response.body());
        q.put(response); //PUTTING RETRIEVED RESPONSE INTO THE QUEUE

    }
    //BENCHMARK STATIC CLASS
    @State(Scope.Benchmark)
    public static class testBench {
        LinkedBlockingQueue<HttpResponse<String>> queue;

        // Producer p;
        LinkedBlockingQueue<HttpResponse<String>> takedata;

        //BENCHMARK SETUP SET TO TRIAL LEVEL
        @Setup(Level.Trial)
        public void init() throws IOException, InterruptedException {
            queue = new LinkedBlockingQueue<>(); //INITIALING QUEUE ON PRODUCER SIDE
            takedata = new LinkedBlockingQueue<>(); //INITIALIZING QUEUE ON CONSUMER SIDE
            new Producer(queue, "p");
            new Consumer("c", takedata);

        }

        @Benchmark
        @BenchmarkMode(Mode.Throughput)
        // @Measurement(iterations = 3)
        @Fork(value = 1)
        @OutputTimeUnit(TimeUnit.SECONDS)
        @Warmup(iterations = 1)
        public void test(testBench b) {
            //STARTING BOTH THREADS
            new Thread(new Producer(queue, "p")).start();
            new Thread(new Consumer("c", b.takedata)).start();
        }

    }
}



