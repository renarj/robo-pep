package com.oberasoftware.robo.pep.core.sensors;

import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.sensors.DirectPort;
import com.oberasoftware.robo.api.sensors.SensorDriver;
import com.oberasoftware.robo.api.sensors.SensorValue;
import com.oberasoftware.robo.pep.core.NaoSessionManager;
import com.oberasoftware.robo.pep.core.SensorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Renze de Vries
 */
@Component
public class NaoSensorDriver implements SensorDriver<DirectPort> {
    private static final Logger LOG = LoggerFactory.getLogger(NaoSensorDriver.class);

    protected static final String SUBSCRIBE_ID = "nao";

    private List<NaoMemoryPort> memoryPorts = new ArrayList<>();

    public static final String SONAR_PORT = "sonar";

    @Autowired
    private NaoSessionManager sessionManager;

    @Autowired
    private SensorManager sensorManager;

    public void initialize() {
        LOG.info("Initializing sensors");
        sensorManager.init();
    }

    public void close() {
        LOG.info("Closing sensor resources");
        memoryPorts.forEach(NaoMemoryPort::close);

        sensorManager.shutdown();
    }

    @Override
    public List<DirectPort> getPorts() {
        return null;
    }

    @Override
    public DirectPort getPort(String portId) {
        switch (portId) {
            case SONAR_PORT:
                return getSonarPort();
        }
        return null;
    }

    public DirectPort<SensorValue<Integer>> getSonarPort() {
        return initialize(new SonarPort(sessionManager.getSession(), sensorManager));
    }

    public DirectPort<SensorValue<?>> getGyroPort() {
        return null;
    }

    public <T extends SensorValue> DirectPort<T> initialize(NaoMemoryPort<T> port) {
        try {
            port.initialize();
            return port;
        } catch(RoboException e) {
            LOG.warn("Could not initialize sensor: {}", port);
            return null;
        }
    }
}
