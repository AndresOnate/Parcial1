package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        if (start < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        if (count < 0) {
            throw new RuntimeException("Invalid Interval");
        }
        Object lock = new Object();
        byte[] digits = new byte[count];
        ArrayList<BBPThread> threads = new ArrayList<BBPThread>();
        Boolean imparSearch = false;
        Boolean isRunning = true;
        int initialRange = start;
        int lastInterval = 0;
        int numDigits = count / N;
        if(numDigits*N != count){
            lastInterval = count - numDigits*(N-1);
            imparSearch = true;
        }
        //Lógica con la distribución de los segmentos de cada hilo
        for(int i = 0; i < N; i++){
            BBPThread thread;
            if(imparSearch && i == N-1){
                thread = new BBPThread(initialRange,lastInterval, i, lock);
            }else{
                thread = new BBPThread(initialRange,numDigits, i, lock);
            }
            thread.start();
            threads.add(thread);
            initialRange += numDigits;
        }
        /*
        for(BBPThread t: threads){
            try{
                t.join();
            }catch (InterruptedException e){
                System.out.println(e.getMessage());
            }
        }
        */

        int threadsDead = 0;
        Scanner scanner = new Scanner(System.in);
        // Lógica que detiene el programa cada 5 segundos y espera un ENTER del usuario
        while(isRunning){
            System.out.println("Ejecutando programa...");
            try {
                Thread.sleep(5000); // Esperar 5 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(BBPThread t:  threads){
                System.out.println("Thread " + t.getThreadId() + ": " + t.getDigits().length);
                if(!t.Alive()){
                    threadsDead +=1;
                }
            }
            if(threadsDead != N){
                isRunning = false;
            }
            // Espera un ENTER del usuario para seguir con el programa, en caso de que aún se esté ejecutando
            if(isRunning) {
                System.out.println("Presiona ENTER para reanudar...");
                String input = scanner.nextLine();
                while (isRunning && !input.equals("")) {
                    System.out.println("Presiona ENTER para reanudar...");
                    input = scanner.nextLine();
                }
                System.out.println("En 5 segundos se mostrará el número de dígitos procesados por cada hilo...");
                synchronized (lock) {
                    lock.notifyAll();
                }
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
