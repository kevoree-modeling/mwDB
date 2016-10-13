package lu.snt.pixel;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.concurrent.TimeUnit;

/**
 * Created by assaad on 28/01/16.
 */
public class InfluxDbTest {
    public static InfluxDB influxDB;
    public static String dbName = "aTimeSeries";
    final long timeOrigin = 1000l;

    public static void createdb(){

        String ip = "127.0.0.1";
        influxDB = InfluxDBFactory.connect("http://" + ip + ":8086", "root", "root");
        boolean influxDBstarted = false;
        do {
            Pong response;
            try {
                response = influxDB.ping();
                // System.out.println(response);
                if (!response.getVersion().equalsIgnoreCase("unknown")) {
                    influxDBstarted = true;
                }
            } catch (Exception e) {
                // NOOP intentional
                // e.printStackTrace();
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                //  e.printStackTrace();
            }
        } while (!influxDBstarted);
        //influxDB.setLogLevel(InfluxDB.LogLevel.FULL);
        // String logs = CharStreams.toString(new InputStreamReader(containerLogsStream,
        // Charsets.UTF_8));
        System.out.println("##################################################################################");
        // System.out.println("Container Logs: \n" + logs);
        System.out.println("#  Connected to InfluxDB Version: " + influxDB.version() + " #");
        System.out.println("##################################################################################");

        System.out.println(influxDB.version());

        influxDB.createDatabase(dbName);
        // Flush every 2000 Points, at least every 100ms
        influxDB.enableBatch(10000, 10000, TimeUnit.MILLISECONDS);
    }


    public static void main(String[] arg){
        createdb();


        testoneUniverse(10000000);



        influxDB.deleteDatabase(dbName);
        System.out.println("done");

    }

    public static double[] testoneUniverse(int timestamped){
        final long timeOrigin = 1000l;
        final double[] ress=new double[2];
        final int exp=2;
        long start,end;

        double res;

        start=System.nanoTime();
        for(int i=0;i<timestamped;i++){
            insert(timeOrigin+i,i*0.3);
        }

        end=System.nanoTime();
        res=(end-start)/(1000000);
        System.out.println("Inserting "+timestamped+" values in original stairs ins "+res+" ms");
        ress[0]=res;


     /*   start=System.nanoTime();

        for(int j=0;j<exp;j++) {
            for (int i = 0; i < timestamped; i++) {
                if( get(timeOrigin + i) !=i*0.3){
                    System.out.println("error");
                }
            }
        }

        end=System.nanoTime();
        res=(end-start)/(1000000*exp);
        ress[1]=res;
        System.out.println("Reading "+timestamped+" values in 2nd universe in "+res+" ms");*/
        return ress;
    }

    private static double get(long l) {
        Query query = new Query("SELECT * FROM cpu", dbName);
        QueryResult qr = influxDB.query(query);
        System.out.println(qr.getResults().get(0).getSeries().get(0));
        return 0;

    }

    private static void insert(long l, double v) {
        Point point1 = Point.measurement("ts")
                .time(l, TimeUnit.MILLISECONDS)
                .field("value", v)
                .build();

        influxDB.write(dbName, "default", point1);
    }
}
