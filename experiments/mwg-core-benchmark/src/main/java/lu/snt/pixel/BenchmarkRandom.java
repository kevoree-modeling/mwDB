package lu.snt.pixel;


import org.uncommons.maths.random.CMWC4096RNG;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.XORShiftRNG;

import java.util.Random;

/**
 * Created by assaad on 23/02/16.
 */
public class BenchmarkRandom {
    public static void main(String[] arg){
        Random random=new Random();
        int bench=100000000;
        long start=System.nanoTime();
        for(int i=0;i<bench;i++){
            random.nextInt();
        }
        long end = System.nanoTime();
        double d=(end-start);
        d=d/1000000;
        System.out.println("Java random: "+d+" ms");


        MersenneTwisterRNG mtr = new MersenneTwisterRNG();
        start=System.nanoTime();
        for(int i=0;i<bench;i++){
            mtr.nextInt();
        }
        end = System.nanoTime();
        d=(end-start);
        d=d/1000000;
        System.out.println("Mersenne Twister RNG "+d+" ms");

        XORShiftRNG xor = new XORShiftRNG() ;
        start=System.nanoTime();
        for(int i=0;i<bench;i++){
            xor.nextInt();
        }
        end = System.nanoTime();
        d=(end-start);
        d=d/1000000;
        System.out.println("XORShiftRNG "+d+" ms");

        CMWC4096RNG cdc = new CMWC4096RNG();
        start=System.nanoTime();
        for(int i=0;i<bench;i++){
            cdc.nextInt();
        }
        end = System.nanoTime();
        d=(end-start);
        d=d/1000000;
        System.out.println("CMWC4096RNG "+d+" ms");



    }
}
