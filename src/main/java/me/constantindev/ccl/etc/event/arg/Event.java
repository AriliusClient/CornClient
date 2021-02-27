/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: EventArg
# Created by constantin at 13:25, Feb 28 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.etc.event.arg;

public class Event {
    public boolean cancel = false;
    public boolean passed = true;

    public void cancel() {
        cancel = true;
    }

    public void endEventCallQueue() {
        passed = false;
    }
}