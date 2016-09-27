package org.mwg.ml.algorithm.preprocessing;

import org.mwg.ml.common.matrix.Matrix;
import org.mwg.ml.common.matrix.SVDDecompose;

/**
 * Created by assaad on 27/09/16.
 */
public class PCA {
    private Matrix _data;
    private double[] _min;
    private double[] _max;
    private double[] _avg;
    private int _processType;
    private double _percentToRetain;

    SVDDecompose _svdDecompose;

    public static double EPS = 1e-30;

    public static int NOPROCESS=0;
    public static int CENTER_ON_AVG=1;
    public static int NORMALIZE=2;


    private void normalizeData(Matrix data) {
        double d = 0;
        for (int j = 0; j < data.columns(); j++) {
            if ((_max[j] - _min[j]) < EPS) {
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, 0);
                }
            } else {
                d = 1 / (_max[j] - _min[j]);
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, (data.get(i, j) - _avg[j]) * d);
                }
            }
        }
    }


    private void inverseNormalizeData(Matrix data) {
        double d = 0;
        for (int j = 0; j < data.columns(); j++) {
            if ((_max[j] - _min[j]) < EPS) {
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, _min[j]);
                }
            } else {
                d = _max[j] - _min[j];
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, data.get(i, j) * d + _avg[j]);
                }
            }
        }
    }


    private void calculateMinMaxAvg() {
        this._min = new double[_data.columns()];
        this._max = new double[_data.columns()];
        this._avg = new double[_data.columns()];

        for (int j = 0; j < _data.columns(); j++) {
            _min[j] = _data.get(0, j);
            _max[j] = _min[j];
            _avg[j]=_min[j];
        }

        double d;

        for (int i = 1; i < _data.rows(); i++) {
            for (int j = 0; j < _data.columns(); j++) {
                d = _data.get(i, j);
                _avg[j]+=d;
                if (d < _min[j]) {
                    _min[j] = d;
                } else if (d > _max[j]) {
                    _max[j] = d;
                }
            }
        }

        for (int j = 0; j < _data.columns(); j++) {
            _avg[j]=_avg[j]/_data.rows();
        }

//        System.out.println();
//        for(int i=0;i<_data.columns();i++){
//            System.out.println(_min[i]+" , "+_max[i]+" , "+_avg[i]);
//        }
//        System.out.println();

    }


    private static Matrix shiftColumn(Matrix data, double[] shift, boolean workInPlace){
        Matrix temp=data;
        if(!workInPlace){
            temp=data.clone();
        }
        for(int i=0;i<temp.rows();i++){
            for(int j=0;j<temp.columns();j++){
                temp.set(i,j,temp.get(i,j)-shift[j]);
            }
        }
        return temp;
    }

    private static Matrix inverseShift(Matrix data, double[] shift, boolean workInPlace){
        Matrix temp=data;
        if(!workInPlace){
            temp=data.clone();
        }

        for(int i=0;i<temp.rows();i++){
            for(int j=0;j<temp.columns();j++){
                temp.set(i,j,temp.get(i,j)+shift[j]);
            }
        }
        return temp;
    }


    public int retainDynamic(double[] svector){
        double d=0;
        for(int i=0;i<svector.length;i++){
            d+=svector[i]*svector[i];
        }


        double integrator=0;
        double previoust=0;
        double t=svector[1]*svector[1]/(svector[0]*svector[0]);
        integrator=svector[0]*svector[0]+svector[1]*svector[1];
        for(int i=2;i<svector.length;i++){
            previoust=t;
            t=svector[i]*svector[i]/(svector[i-1]*svector[i-1]);
            System.out.println(i+" "+t/previoust+" , energy: "+integrator*100/d+" %");
            if(t/previoust<0.98){
                _percentToRetain=integrator*100/d;
                return i;
            }
            integrator+=svector[i]*svector[i];
        }
        _percentToRetain=integrator*100/d;
        System.out.println(svector.length+" "+t/previoust+" , energy: "+integrator*100/d+" %");
        System.out.println("");
        return svector.length;
    }



    public static int retain(double[] svector, double percent){
        double d=0;
        for(int i=0;i<svector.length;i++){
            d+=svector[i]*svector[i];
        }
        d=d*percent;
        double t=0;
        for(int i=0;i<svector.length;i++){
            t+=svector[i]*svector[i];
            if(t>d){
                return i+1;
            }
        }
        return svector.length;
    }




    public PCA(Matrix data, int processType) {
        this._data = data;
        this._processType = processType;
        calculateMinMaxAvg();

        if(processType==CENTER_ON_AVG){
            shiftColumn(_data,_avg,true);
        }
        else if(processType==NORMALIZE){
            normalizeData(_data);
        }



        //shiftColumn(_data,_avg,true);

//        if (normalize) {
//            normalizeData(_data);
//        }



        _svdDecompose = Matrix.defaultEngine().decomposeSVD(_data, true);

        double[] singularValues = _svdDecompose.getS();


//        System.out.println("Singular values");
//        for (int i = 0; i < singularValues.length; i++) {
//            System.out.println(singularValues[i]);
//        }
//        System.out.println("");


        System.out.println("Need to retain: "+retainDynamic(singularValues)+" / "+data.columns()+" dimensions");
        System.out.println("Energy retained: "+_percentToRetain+ " %");

        int x = 0;

    }


}
