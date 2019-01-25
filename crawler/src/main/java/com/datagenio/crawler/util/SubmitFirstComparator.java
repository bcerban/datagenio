package com.datagenio.crawler.util;

import com.datagenio.crawler.api.Eventable;

import java.util.Comparator;
import java.util.Random;

public class SubmitFirstComparator implements Comparator<Eventable> {
    @Override
    public int compare(Eventable o1, Eventable o2) {
        return -(o1.getEventType().compareTo(o2.getEventType()));
    }
}
