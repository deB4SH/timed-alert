package de.b4sh.kube;

import com.aayushatharva.brotli4j.common.annotations.Local;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

/**
 * Simple Health endpoint that also switches a specific metrics that shall trigger
 * alerts in any monitoring stack for testing purposes.
 */
@Path("/health")
@Produces("text/plain")
public class HealthEndpoint {
    final Map<String, String> env = System.getenv();
    final LocalTime alertStart = LocalTime.of(Integer.parseInt(env.get("startHour")),Integer.parseInt(env.get("startMinute")));
    final LocalTime alertEnd = LocalTime.of(Integer.parseInt(env.get("endHour")),Integer.parseInt(env.get("endMinute")));
    final DayOfWeek dayToCheck = DayOfWeek.valueOf(System.getenv("dayOfAlert"));

    @Inject
    MetricStorage metricStorage;

    @GET
    public String getHealth(){
        //switch errorCode if required
        final Date date = new Date();
        final Instant instant = date.toInstant();
        final ZonedDateTime zdt = instant.atZone(ZoneId.of("Europe/Berlin"));
        final LocalTime now = LocalTime.now();
        if(zdt.getDayOfWeek() == dayToCheck && now.isAfter(alertStart) && now.isBefore(alertEnd)){
            metricStorage.setAlertCode(1);
        } else {
            metricStorage.setAlertCode(0);
        }
        return String.format("Service is alive. Current Configuration: error code: %s, startHour: %s, startMinute: %s, endHour: %s, endMinute: %s, dayOfWeek: %s",
                metricStorage.getAlertCode(),
                Integer.parseInt(env.get("startHour")),
                Integer.parseInt(env.get("startMinute")),
                Integer.parseInt(env.get("endHour")),
                Integer.parseInt(env.get("endMinute")),
                dayToCheck.toString());
    }
}
