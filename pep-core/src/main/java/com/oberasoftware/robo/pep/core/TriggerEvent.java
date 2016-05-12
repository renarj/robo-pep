package com.oberasoftware.robo.pep.core;

import com.oberasoftware.robo.api.events.RobotEvent;

/**
 * @author Renze de Vries
 */
public class TriggerEvent implements RobotEvent {
    private final boolean on;
    private final String source;

    public TriggerEvent(boolean on, String source) {
        this.on = on;
        this.source = source;
    }

    @Override
    public String getRobotName() {
        return null;
    }

    @Override
    public String getCapability() {
        return null;
    }

    @Override
    public String getSourceName() {
        return source;
    }

    public boolean isOn() {
        return on;
    }

    @Override
    public String toString() {
        return "TriggerEvent{" +
                "on=" + on +
                ", source='" + source + '\'' +
                '}';
    }
}
