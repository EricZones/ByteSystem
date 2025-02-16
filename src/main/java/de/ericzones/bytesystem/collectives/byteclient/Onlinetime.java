// Created by Eric B. 13.11.2021 15:14
package de.ericzones.bytesystem.collectives.byteclient;

public class Onlinetime {

    private final long previousOnlinetime;
    private final long joinTime;

    public Onlinetime(long previousOnlinetime) {
        this.previousOnlinetime = previousOnlinetime;
        this.joinTime = System.currentTimeMillis();
    }

    public long getCurrentOnlinetimeMillis() {
        return System.currentTimeMillis() - joinTime;
    }

    public String getCurrentOnlinetime() {
        long onlinetimeMillis = getCurrentOnlinetimeMillis();
        long seconds = 0, minutes = 0, hours = 0, days = 0;
        while(onlinetimeMillis > 1000) {
            onlinetimeMillis-=1000;
            seconds++;
        }
        while(seconds > 60) {
            seconds-=60;
            minutes++;
        }
        while(minutes > 60) {
            minutes-=60;
            hours++;
        }
        while(hours > 24) {
            hours-=24;
            days++;
        }
        return days + "d " + hours + "h " + minutes + "m";
    }

    public long getTotalOnlinetimeMillis() {
        return getCurrentOnlinetimeMillis() + previousOnlinetime;
    }

    public String getTotalOnlinetime() {
        long onlinetimeMillis = getTotalOnlinetimeMillis();
        long seconds = 0, minutes = 0, hours = 0, days = 0;
        while(onlinetimeMillis > 1000) {
            onlinetimeMillis-=1000;
            seconds++;
        }
        while(seconds > 60) {
            seconds-=60;
            minutes++;
        }
        while(minutes > 60) {
            minutes-=60;
            hours++;
        }
        while(hours > 24) {
            hours-=24;
            days++;
        }
        return days + "d " + hours + "h " + minutes + "m";
    }

}
