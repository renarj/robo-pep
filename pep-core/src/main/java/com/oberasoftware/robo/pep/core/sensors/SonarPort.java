package com.oberasoftware.robo.pep.core.sensors;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALSonar;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.sensors.EventSource;
import com.oberasoftware.robo.api.sensors.SensorValue;
import com.oberasoftware.robo.pep.core.NumberEvent;
import com.oberasoftware.robo.pep.core.SensorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Renze de Vries
 */
public class SonarPort extends NaoMemoryPort<SensorValue<Integer>> implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SonarPort.class);

    private ALSonar sonar;

    public SonarPort(Session session, SensorManager sensorManager) {
        super(session, sensorManager);
    }

    @Override
    public void close() {
        try {
            sonar.unsubscribe(NaoSensorDriver.SUBSCRIBE_ID);
        } catch (CallError | InterruptedException e) {
            LOG.error("Could not cleanly unsubscribe from sonar port", e);
        }
    }

    @Override
    public void initialize() {
        try {
            sonar = new ALSonar(getSession());
            sonar.subscribe(NaoSensorDriver.SUBSCRIBE_ID);
            getSensorManager().registerListener(this);
            LOG.debug("SonarPort initialized");
        } catch (Exception e) {
            throw new RoboException("Could not load robot session");
        }
    }


    @EventSubscribe
    @EventSource({"Device/SubDeviceList/US/Left/Sensor/Value", "Device/SubDeviceList/US/Right/Sensor/Value"})
    public void receive(NumberEvent numberEvent) {
        LOG.info("Received a distance: {} from source: {}", numberEvent.getNumber(), numberEvent.getSource());
        notify(numberEvent::getNumber);
    }
}
