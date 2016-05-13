package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.helper.proxies.ALMotion;
import com.oberasoftware.robo.api.commands.PositionAndSpeedCommand;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Renze de Vries
 */
@Component
public class NaoServoDriver implements ServoDriver {
    private static final Logger LOG = LoggerFactory.getLogger(NaoServoDriver.class);

    @Autowired
    private NaoSessionManager sessionManager;

    private ALMotion alMotion;

    @Override
    public boolean setServoSpeed(String servoId, int speed) {
        return false;
    }

    @Override
    public boolean setTargetPosition(String servoId, int targetPosition) {
        return false;
    }

    @Override
    public boolean setPositionAndSpeed(String servoId, int speed, int targetPosition) {
        return false;
    }

    @Override
    public boolean bulkSetPositionAndSpeed(Map<String, PositionAndSpeedCommand> commands) {
        return false;
    }

    @Override
    public List<Servo> getServos() {


        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void activate(Map<String, String> properties) {

    }
}
