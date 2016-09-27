package org.mwg.ml.common.matrix;

import org.mwg.ml.common.matrix.blassolver.BlasMatrixEngine;
import org.mwg.ml.common.matrix.jamasolver.JamaMatrixEngine;
import org.mwg.ml.common.matrix.operation.PInvSVD;

/**
 * Created by assaad on 08/06/16.
 */
public class HybridMatrixEngine implements MatrixEngine {

    private MatrixEngine blas;
    private MatrixEngine jama;

    public HybridMatrixEngine(){
        blas=new BlasMatrixEngine();
        jama=new JamaMatrixEngine();
    }

    /**
     * @native ts
     * private static MULT_LIMIT: number=12;
     */
    private final static int MULT_LIMIT=9;

    /**
     * @native ts
     * private static INVERT_LIMIT: number=6;
     */
    private final static int INVERT_LIMIT=10;

    /**
     * @native ts
     * private static PINV_LIMIT: number=5;
     */
    private final static int PINV_LIMIT=8;

    /**
     * @native ts
     * private static SOLVELU_LIMIT: number=6;
     */
    private final static int SOLVELU_LIMIT=8;

    /**
     * @native ts
     * private static SOLVEQR_LIMIT: number=10;
     */
    private final static int SOLVEQR_LIMIT=8;

    /**
     * @native ts
     * private static SOLVESVD_LIMIT: number=8;
     */
    private final static int SOLVESVD_LIMIT=35;


    @Override
    public Matrix multiplyTransposeAlphaBeta(TransposeType transA, double alpha, Matrix matA, TransposeType transB, Matrix matB, double beta, Matrix matC) {
        if(matA.leadingDimension()<MULT_LIMIT&&matB.leadingDimension()<MULT_LIMIT){
            return jama.multiplyTransposeAlphaBeta(transA,alpha,matA,transB,matB, beta, matC);
        }
        else {
            return blas.multiplyTransposeAlphaBeta(transA,alpha,matA,transB,matB,beta, matC);
        }
    }

    @Override
    public Matrix invert(Matrix mat, boolean invertInPlace) {
        if(mat.rows()<INVERT_LIMIT){
            return jama.invert(mat,invertInPlace);
        }
        else {
            return blas.invert(mat,invertInPlace);
        }
    }

    @Override
    public Matrix pinv(Matrix mat, boolean invertInPlace) {
        /*if(mat.rows()<PINV_LIMIT){
            return jama.pinv(mat,invertInPlace);
        }
        else {
            return blas.pinv(mat,invertInPlace);
        }*/

        PInvSVD res = new PInvSVD();
        res.factor(mat,invertInPlace);
        return res.getPInv();
    }

    @Override
    public Matrix solveLU(Matrix matA, Matrix matB, boolean workInPlace, TransposeType transB) {
        if(matA.leadingDimension()<SOLVELU_LIMIT&&matB.leadingDimension()<SOLVELU_LIMIT){
            return jama.solveLU(matA, matB, workInPlace, transB);
        }
        else {
            return blas.solveLU(matA, matB, workInPlace, transB);
        }
    }

    @Override
    public Matrix solveQR(Matrix matA, Matrix matB, boolean workInPlace, TransposeType transB) {
        /*if(matA.leadingDimension()<SOLVEQR_LIMIT &&matB.leadingDimension()<SOLVEQR_LIMIT){
            return jama.solveQR(matA, matB, workInPlace, transB);
        }
        else {
            return blas.solveQR(matA, matB, workInPlace, transB);
        }*/
       return blas.solveQR(matA, matB, workInPlace, transB);
    }

    @Override
    public SVDDecompose decomposeSVD(Matrix matA, boolean workInPlace) {

        return jama.decomposeSVD(matA,workInPlace);
//        if(matA.leadingDimension()<SOLVESVD_LIMIT){
//            return jama.decomposeSVD(matA,workInPlace);
//        }
//        else {
//            return blas.decomposeSVD(matA,workInPlace);
//        }
    }
}
