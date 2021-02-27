/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: EventHelper
# Created by constantin at 13:22, Feb 28 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.etc.event;

import me.constantindev.ccl.etc.event.arg.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventHelper {
    public static class BUS {
        static Map<EventType, List<Consumer<Event>>> events = new HashMap<>();

        public static void init() {
            for (EventType ev : EventType.values()) {
                events.put(ev, new ArrayList<>());
            }
        }

        public static void registerEvent(EventType type, Consumer<Event> onCall) {
            events.get(type).add(onCall);
        }

        public static boolean invokeEventCall(EventType type, Event earg) {
            for (Consumer<Event> runnable : events.get(type)) {
                runnable.accept(earg);
                if (!earg.passed) break;
            }
            return !earg.cancel;
        }
    }
}