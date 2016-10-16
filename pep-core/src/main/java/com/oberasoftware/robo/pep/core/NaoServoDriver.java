package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.commands.PositionAndSpeedCommand;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.oberasoftware.robo.pep.core.NaoUtil.safeExecuteTask;

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
    public void activate(Robot robot, Map<String, String> properties) {
        try {
            Session session = sessionManager.getSession();
            alMotion = new ALMotion(session);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    @Override
    public boolean setServoSpeed(String servoId, int speed) {
        return false;
    }

    @Override
    public boolean setTargetPosition(String servoId, int targetPosition) {
        float r = toRadial(targetPosition);
        LOG.info("Setting servo: {} to radial: {}", servoId, r);
        safeExecuteTask(() -> alMotion.setAngles(servoId, r, 0.2f));

        return true;
    }

    public static float toRadial(int position) {
        if(position > 512 && position <= 1024) {
            int remainder = position - 512;
            return -(1 / (float)512) * remainder;
        } else if(position >= 0 && position <= 512) {
            int remainder = 512 - position;
            return (1 / (float)512) * remainder;
        } else {
            throw new RuntimeException("Invalid input");
        }
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
        List<Servo> servos = new ArrayList<>();
        safeExecuteTask(() -> {
            List<String> jointNames = alMotion.getBodyNames("Body");
            jointNames.forEach(s -> servos.add(new NaoServo(s, alMotion)));
        });

        return servos;
    }

    @Override
    public boolean setTorgue(String servoId, int limit) {
        return false;
    }

    @Override
    public boolean setTorgue(String servoId, boolean state) {
        return false;
    }

    @Override
    public Servo getServo(String servoId) {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
