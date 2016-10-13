package lu.snt.pixel;


/**
 * Created by assaad on 27/04/16.
 */
public class GaussianProfile {
    private int features;
    private double[] min;
    private double[] max;
    private double[] sum;
    private double[] sumSquares;
    private int total;

    public int getTotal() {
        return total;
    }

    public double[] getSumSquares() {
        return sumSquares;
    }

    public double[] getSum() {
        return sum;
    }

    public double[] getMax() {
        return max;
    }

    public double[] getMin() {
        return min;
    }


    public void learn(double[] values){
        if(total==0){
            features=values.length;
            min=new double[features];
            max=new double[features];
            sum=new double[features];
            sumSquares=new double[features*(features+1)/2];
            int count=0;
            for(int i=0;i<features;i++){
                min[i]=values[i];
                max[i]=values[i];
                sum[i]=values[i];
                for (int j = i; j < features; j++) {
                    sumSquares[count] = values[i] * values[j];
                    count++;
                }
            }
        }
        else{
            int count=0;
            for(int i=0;i<features;i++){
                if(values[i]<min[i]){
                    min[i]=values[i];
                }
                if(values[i]>max[i]) {
                    max[i] = values[i];
                }
                sum[i]+=values[i];
                for (int j = i; j < features; j++) {
                    sumSquares[count] += values[i] * values[j];
                    count++;
                }
            }
        }
        total++;
    }


    public double[] getAvg(){
        double[] avg = new double[features];
        if(features==0||total==0){
            return avg;
        }
        else {
            for(int i=0;i<features;i++){
                avg[i]=sum[i]/total;
            }
        }
        return avg;
    }

    public void print() {
        double[] avg=getAvg();
        for(int i=0;i<features;i++){
            System.out.println("feature "+i+": min: "+min[i]+" , max: "+max[i]+" , avg: "+avg[i]+" , sum: "+sum[i]+ " , total: "+total);
        }
        for(int i=0;i<sumSquares.length;i++){
            System.out.println("sumsq["+i+"]: "+sumSquares[i]);
        }

    }
}
