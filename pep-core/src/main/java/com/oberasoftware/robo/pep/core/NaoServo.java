package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.helper.proxies.ALMotion;
import com.oberasoftware.robo.api.servo.Servo;
import com.oberasoftware.robo.api.servo.ServoData;

/**
 * @author Renze de Vries
 */
public class NaoServo implements Servo {

    private final String servoId;
    private final ALMotion alMotion;

    public NaoServo(String servoId, ALMotion alMotion) {
        this.servoId = servoId;
        this.alMotion = alMotion;
    }

    @Override
    public String getId() {
        return servoId;
    }

    @Override
    public ServoData getData() {
        return null;
    }

    @Override
    public void moveTo(int position) {

    }

    @Override
    public void setSpeed(int speed) {

    }

    @Override
    public void setTorgueLimit(int torgueLimit) {

    }

    @Override
    public void enableTorgue() {

    }

    @Override
    public void disableTorgue() {

    }
}
