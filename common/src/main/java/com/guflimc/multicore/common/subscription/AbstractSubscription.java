package com.guflimc.multicore.common.subscription;

import com.guflimc.multicore.Subscription;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public abstract class AbstractSubscription implements Subscription {

    private final Instant createdAt = Instant.now();
    private final Set<Supplier<Boolean>> conditions = new HashSet<>();

    public boolean isNotExpired() {
        boolean result = conditions.stream().allMatch(Supplier::get);
        if ( !result ) {
            unsubscribe();
        }
        return result;
    }

    //

    @Override
    public final Subscription until(Instant instant) {
        conditions.add(() -> Instant.now().isBefore(instant));
        return this;
    }

    @Override
    public final Subscription until(Duration duration) {
        conditions.add(() -> Instant.now().isBefore(createdAt.plus(duration)));
        return this;
    }

    @Override
    public final Subscription until(int maxCalls) {
        AtomicReference<Integer> calls = new AtomicReference<>(0);
        conditions.add(() -> calls.getAndUpdate(i -> ++i) < maxCalls);
        return this;
    }
}
