package test;

/**
 * Created by assaad on 03/09/15.
 */
public class Test2 {
    public static void main(String[] arg){

        boolean b =false;

        if ((b ? 0 : 1) != 0){
            System.out.println("A");
        }
        else{
            System.out.println("B");
        }
    }
}