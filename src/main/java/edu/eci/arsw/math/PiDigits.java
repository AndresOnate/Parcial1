package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.List;

///  <summary>
///  An implementation of the Bailey-Borwein-Plouffe formula for calculating hexadecimal
///  digits of pi.
///  https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula
///  *** Translated from C# code: https://github.com/mmoroney/DigitsOfPi ***
///  </summary>
public class PiDigits {

    /**
     * Returns a range of hexadecimal digits of pi.
     * @param start The starting location of the range.
     * @param count The number of digits to return
     * @return An array containing the hexadecimal digits.
     */
    public static byte[] getDigits(int start, int count, int N){
        byte[] digits = new byte[count];
        ArrayList<BBPThread> threads = new ArrayList<BBPThread>();
        if (start < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        if (count < 0) {
            throw new RuntimeException("Invalid Interval");
        }
        Boolean imparSearch = false;
        int initialRange = start;
        int lastInterval = 0;
        int numDigits = count / N;
        if(numDigits*N != count){
            lastInterval = count - numDigits*(N-1);
            imparSearch = true;
        }
        for(int i = 0; i < N; i++){
            BBPThread thread;
            if(imparSearch && i == N-1){
                thread = new BBPThread(initialRange,lastInterval);
            }else{
                thread = new BBPThread(initialRange,numDigits);
            }
            thread.start();
            threads.add(thread);
            initialRange += numDigits;
        }
        for(BBPThread t: threads){
            try{
                t.join();
            }catch (InterruptedException e){
                System.out.println(e.getMessage());
            }
        }
        int index = 0;
        for(BBPThread t: threads){
            for(byte b: t.getDigits()){
                digits[index] = b ;
                index += 1;
            }
        }
        return digits;
    }



}
