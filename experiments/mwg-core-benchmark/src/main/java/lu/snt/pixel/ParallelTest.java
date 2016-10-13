package lu.snt.pixel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by assaad on 08/02/16.
 */
public class ParallelTest {
    public static void main(String[] arg){
        long capacity = 2400000000l;

        long s=0;
        long start =System.nanoTime();
        for(long l=0;l<capacity;l++){
            s+=l;
        }
        long end =System.nanoTime();
        double restime=(end-start)/1000000 ;
        System.out.println("Time taken serial: "+restime+" ms");
        System.out.println("result serial: "+s);


        long step=10000000;

        ArrayList<Long> res = new ArrayList<Long>((int)(capacity*2/step));
        long sum=0;
        for(int i=0;i<capacity/step;i++){
            res.add(i*step);
        }
        System.out.println("Created: "+ res.size()+" jobs");
        sum=capacity;
        sum=sum*(capacity-1)/2;
        System.out.println("initial sum: "+sum);
        start =System.nanoTime();
        try {
            List<Long> result = processInputs(res,step);
            end =System.nanoTime();
            restime=(end-start)/1000000 ;
            System.out.println("Time taken //: "+restime+" ms");
            long newsum=0;
            for(int i=0;i<result.size();i++){
                newsum+=result.get(i);
            }
            System.out.println("final sum: "+newsum);


        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public static  List<Long> processInputs(List<Long> inputs, final long step)
            throws InterruptedException, ExecutionException {

        int threads = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of threads: "+threads);
        ExecutorService service = Executors.newFixedThreadPool(threads);

        List<Future<Long>> futures = new ArrayList<Future<Long>>();
        for (final Long input : inputs) {
            Callable<Long> callable = new Callable<Long>() {
                public Long call() throws Exception {
                    Long output=0l;
                    for(long i=0;i<step;i++){
                        output += i+input;
                    }
                    // process your input here and compute the output
                    return output;
                }
            };
            futures.add(service.submit(callable));
        }

        service.shutdown();

        List<Long> outputs = new ArrayList<Long>();
        for (Future<Long> future : futures) {
            outputs.add(future.get());
        }
        return outputs;
    }
}
