package com.oberasoftware.robo.pep.core.sensors;

/**
 * @author Renze de Vries
 */
public enum ObstacleSide {
    LEFT,
    RIGHT,
    NOTHING;

    public static ObstacleSide fromSource(String source) {
        if(source.equalsIgnoreCase("SonarLeftDetected")) {
            return LEFT;
        } else if (source.equalsIgnoreCase("SonarRightDetected")){
            return RIGHT;
        } else {
            return NOTHING;
        }
    }
}
