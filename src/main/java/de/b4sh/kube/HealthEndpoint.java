package de.b4sh.kube;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.time.*;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple Health endpoint that also switches a specific metrics that shall trigger
 * alerts in any monitoring stack for testing purposes.
 */
@Path("/health")
@Produces("text/plain")
public class HealthEndpoint {
    private final Logger log = Logger.getLogger(HealthEndpoint.class.getName());
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
        final String response = String.format("Service is alive. Current Configuration: error code: %s, startHour: %s, startMinute: %s, endHour: %s, endMinute: %s, dayOfWeek: %s. Now is: %s",
                metricStorage.getAlertCode(),
                Integer.parseInt(env.get("startHour")),
                Integer.parseInt(env.get("startMinute")),
                Integer.parseInt(env.get("endHour")),
                Integer.parseInt(env.get("endMinute")),
                dayToCheck.toString(),
                LocalTime.now().toString());
        log.log(Level.INFO, response);
        return response;
    }
}
