package ru.javawebinar.topjava.util;

import org.slf4j.Logger;
import ru.javawebinar.topjava.storage.MapMealStorage;

import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class IdGenerator {
    private static final Logger log = getLogger(MapMealStorage.class);
    private static final AtomicInteger id = new AtomicInteger(7);

    public static int generateUniqueId() {
        int newId = id.incrementAndGet();
        log.debug("generateUniqueId(): Generated a new id = {}", newId);
        return newId;
    }
}