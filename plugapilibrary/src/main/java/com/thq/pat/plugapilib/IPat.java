package com.thq.pat.plugapilib;

public interface IPat {

    public void startLife();
    public void endLife();
    public void sleep();
    public void wakeUp();
    public void command(String action);
}
