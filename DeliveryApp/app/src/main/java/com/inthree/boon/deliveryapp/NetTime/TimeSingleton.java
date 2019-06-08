package com.inthree.boon.deliveryapp.NetTime;

public class TimeSingleton{
    private static volatile TimeSingleton instance;
    public static TimeSingleton getInstance() {
        if (instance == null) {
            synchronized (TimeSingleton.class) {
                if (instance == null) {
                    instance = new TimeSingleton();
                }
            }
        }
        return instance;
    }

    public boolean TimeSingleton(){
        /**
         * Intialize the  network time  in devices  //commented kani
         */
       TrueTimeRx.isInitialized();
       return true;
    }

    // private constructor and other methods...
}
