package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.oberasoftware.base.event.EventBus;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.robo.api.events.RobotEvent;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.sensors.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Arrays.asList;

/**
 * @author Renze de Vries
 */
@Component
public class SensorManager {
    private static final Logger LOG = LoggerFactory.getLogger(SensorManager.class);

    private ALMemory memory;
    private final EventBus eventBus = new LocalEventBus();

    private List<Long> eventIds = new CopyOnWriteArrayList<>();

    @Autowired
    private NaoSessionManager sessionManager;

    public void init() {
        try {
            LOG.info("Initializing the Sensor manager");
            this.memory = new ALMemory(sessionManager.getSession());
            eventBus.registerFilter((o, handlerEntry) -> {
                if(o instanceof RobotEvent) {
                    RobotEvent roboEvent = (RobotEvent) o;
                    Method eventMethod = handlerEntry.getEventMethod();
                    EventSource eventSource = eventMethod.getAnnotation(EventSource.class);
                    if(eventSource != null) {
                        Optional<String> supportedSource = Arrays.asList(eventSource.value()).stream()
                                .filter(s -> s.equalsIgnoreCase(roboEvent.getSourceName()))
                                .findFirst();

                        if(!supportedSource.isPresent()) {
                            return true;
                        }
                    }
                }
                return false;
            });
        } catch (Exception e) {
            throw new RoboException("Unable to create Memory connection to NAO", e);
        }
    }

    public void listenToEvent(String event) {
        try {
            LOG.info("Subscribing to event: {}", event);
            eventIds.add(memory.subscribeToEvent(event, o -> {
                LOG.debug("Received event: {} for source: {}", o, event);
                if(o instanceof Float) {
                    eventBus.publish(new TriggerEvent(((Float)o > 0), event));
                } else if(o instanceof Integer) {
                    eventBus.publish(new NumberEvent(event, (Integer)o));
                } else if(o instanceof List) {
                    List<Object> values = (List<Object>)o;

                    eventBus.publish(new ListValueEvent(values, event));
                } else {
                    LOG.debug("Some unknown type was sent: {}", o);
                }

            }));
        } catch (Exception e) {
            throw new RoboException("Unable to subscribe to event: " + event, e);
        }
    }

    public void registerListener(EventHandler handler) {
        this.eventBus.registerHandler(handler);

        Method[] methods = handler.getClass().getMethods();
        asList(methods).forEach(m -> {
            EventSource eventSource = m.getAnnotation(EventSource.class);
            if(eventSource != null) {
                Arrays.asList(eventSource.value()).forEach(this::listenToEvent);
            }
        });
    }

    public void shutdown() {
        eventIds.forEach(e -> {
            try {
                memory.unsubscribeToEvent(e);
            } catch (InterruptedException | CallError ex) {
                LOG.error("Unable to cleanly unsubscribe from event: ", ex.getMessage());
            }
        });
    }

    public void setSessionManager(NaoSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
