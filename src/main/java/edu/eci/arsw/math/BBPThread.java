package edu.eci.arsw.math;

public class BBPThread extends Thread{

    private static int DigitsPerSum = 8;
    private static double Epsilon = 1e-17;
    private int start;
    private int count;
    private byte[] digits;
    private int threadId;
    private Object lock;

    private Boolean running;

    private Boolean alive;

    private int processedDigits;


    public BBPThread(int start, int count, int Id, Object lock){
        this.start = start;
        this.count = count;
        digits = new byte[count];
        this.threadId = Id;
        this.lock = lock;
        this.running = true;
        this.alive = true;
        this.processedDigits = 0;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        double sum = 0;
        for (int i = 0; i < count; i++) {
            if(System.currentTimeMillis() - startTime >= 5000){
                running = false;
                synchronized (lock){
                    while(!running){
                        try {
                            lock.wait();
                            startTime  = System.currentTimeMillis();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (i % DigitsPerSum == 0) {
                sum = 4 * sum(1, start)
                        - 2 * sum(4, start)
                        - sum(5, start)
                        - sum(6, start);

                start += DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));
            digits[i] = (byte) sum;
            processedDigits += 1;
        }
        alive = false;
    }

    /// <summary>
    /// Returns the sum of 16^(n - k)/(8 * k + m) from 0 to k.
    /// </summary>
    /// <param name="m"></param>
    /// <param name="n"></param>
    /// <returns></returns>
    private static double sum(int m, int n) {
        double sum = 0;
        int d = m;
        int power = n;

        while (true) {
            double term;

            if (power > 0) {
                term = (double) hexExponentModulo(power, d) / d;
            } else {
                term = Math.pow(16, power) / d;
                if (term < Epsilon) {
                    break;
                }
            }

            sum += term;
            power--;
            d += 8;
        }

        return sum;
    }

    /// <summary>
    /// Return 16^p mod m.
    /// </summary>
    /// <param name="p"></param>
    /// <param name="m"></param>
    /// <returns></returns>
    private static int hexExponentModulo(int p, int m) {
        int power = 1;
        while (power * 2 <= p) {
            power *= 2;
        }

        int result = 1;

        while (power > 0) {
            if (p >= power) {
                result *= 16;
                result %= m;
                p -= power;
            }

            power /= 2;

            if (power > 0) {
                result *= result;
                result %= m;
            }
        }

        return result;
    }

    public byte[] getDigits(){
        return this.digits;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public Boolean Alive(){
        return alive;
    }

    public int getThreadId(){
        return this.threadId;
    }

    public int getProcessedDigits() {
        return processedDigits;
    }
}
