package com.oberasoftware.robo.pep.core.sensors;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALSonar;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.sensors.EventSource;
import com.oberasoftware.robo.api.sensors.SensorValue;
import com.oberasoftware.robo.pep.core.SensorManager;
import com.oberasoftware.robo.pep.core.TriggerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Renze de Vries
 */
public class SonarPort extends NaoMemoryPort<SensorValue<Integer>> implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SonarPort.class);
    private static final String LEFT_SONAR = "Device/SubDeviceList/US/Left/Sensor/Value";
    private static final String RIGHT_SONAR = "Device/SubDeviceList/US/Right/Sensor/Value";

    private ALSonar sonar;
    private ALMemory memory;

    public SonarPort(Session session, SensorManager sensorManager) {
        super(session, sensorManager);
    }

    @Override
    public void close() {
        try {
            LOG.info("Closing used Sonar resource");
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

            memory = new ALMemory(getSession());
            getSensorManager().registerListener(this);
            LOG.debug("SonarPort initialized");
        } catch (Exception e) {
            throw new RoboException("Could not load robot session");
        }
    }

    private int getSensorData(ObstacleSide side) {
        String memoryPath = side == ObstacleSide.LEFT ? LEFT_SONAR : RIGHT_SONAR;
        try {
            Float distance = (Float) memory.getData(memoryPath);
            int roundedDistance = (int)(distance * 100);
            LOG.debug("Rounded distance for path: {} is: {}", memoryPath, roundedDistance);
            return roundedDistance;
        } catch (CallError | InterruptedException e) {
            LOG.error("", e);
        }
        return -1;
    }


    @EventSubscribe
    @EventSource({"SonarLeftDetected", "SonarRightDetected", "SonarLeftNothingDetected", "SonarRightNothingDetected"})
    public void receive(TriggerEvent obstacleEvent) {
        LOG.debug("Detected an obstacle on: {} side", obstacleEvent.getSourceName());

//        notify(() -> getSensorData(ObstacleSide.LEFT));
//        notify(() -> getSensorData(ObstacleSide.RIGHT));
    }
}
