package org.kie.kogito.jobs;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;


public class DurationExpirationTime implements ExpirationTime {

    private final ZonedDateTime expirationTime;
    private Long repeatInterval;
    
    private DurationExpirationTime(ZonedDateTime expirationTime, Long repeatInterval) {
        this.expirationTime = Objects.requireNonNull(expirationTime);
        this.repeatInterval = repeatInterval;
    }
    
    @Override
    public ZonedDateTime get() {
        return expirationTime;
    }

    @Override
    public Long repeatInterval() {     
        return repeatInterval;
    }
    
    public static DurationExpirationTime now() {
        return new DurationExpirationTime(ZonedDateTime.now(), null);
    }
    
    public static DurationExpirationTime after(long delay) {
        return after(delay, ChronoUnit.MILLIS);
    }
    
    public static DurationExpirationTime after(long delay, TemporalUnit unit) {
        return new DurationExpirationTime(ZonedDateTime.now().plus(delay, unit), null);
    }
    
    public static DurationExpirationTime repeat(long delay) {
        return repeat(delay, null, ChronoUnit.MILLIS);
    }
    
    public static DurationExpirationTime repeat(long delay, Long repeatInterval) {
        return repeat(delay, repeatInterval, ChronoUnit.MILLIS);
    }
    
    public static DurationExpirationTime repeat(long delay, Long repeatInterval, TemporalUnit unit) {
        return new DurationExpirationTime(ZonedDateTime.now().plus(delay, unit), repeatInterval);
    }
}
