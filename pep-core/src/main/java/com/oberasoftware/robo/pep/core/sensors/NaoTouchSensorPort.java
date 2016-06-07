package com.oberasoftware.robo.pep.core.sensors;

import com.aldebaran.qi.Session;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.sensors.EventSource;
import com.oberasoftware.robo.api.sensors.TriggerValue;
import com.oberasoftware.robo.pep.core.SensorManager;
import com.oberasoftware.robo.pep.core.TriggerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaoTouchSensorPort extends NaoMemoryPort<TriggerValue> implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NaoTouchSensorPort.class);

    public NaoTouchSensorPort(Session session, SensorManager sensorManager) {
        super(session, sensorManager);
    }

    @Override
    public void close() {

    }

    @Override
    public void initialize() {
        getSensorManager().registerListener(this);
    }

    @EventSubscribe
    @EventSource({"FrontTactilTouched", "MiddleTactilTouched", "RearTactilTouched"})
    public void receive(TriggerEvent triggerEvent) {
        LOG.debug("Head was touched on spot: {}", triggerEvent.getLabel());

        notify(new TriggerValue() {
            @Override
            public String getSource() {
                return triggerEvent.getLabel();
            }

            @Override
            public Boolean getRaw() {
                return triggerEvent.isOn();
            }
        });
    }
}
