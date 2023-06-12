package com.guflimc.multicore;

import java.time.Duration;
import java.time.Instant;

public interface Subscription {

    void unsubscribe();

    Subscription until(Instant instant);

    Subscription until(Duration duration);

    Subscription until(int maxCalls);

}
