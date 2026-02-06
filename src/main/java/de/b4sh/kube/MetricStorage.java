package de.b4sh.kube;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.runtime.Startup;
import jakarta.inject.Singleton;

import java.util.concurrent.atomic.AtomicInteger;

@Startup
@Singleton
public class MetricStorage {
    private MeterRegistry registry;
    private final AtomicInteger alertCode;

    public MetricStorage(MeterRegistry registry) {
        this.registry = registry;
        this.alertCode = new AtomicInteger(0);
        this.registry.gauge("de.b4sh.kube.alert",this.alertCode);
    }

    public final int getAlertCode(){
        return alertCode.get();
    }

    public final void setAlertCode(int alertCode) {
        this.alertCode.set(alertCode);
    }
}
