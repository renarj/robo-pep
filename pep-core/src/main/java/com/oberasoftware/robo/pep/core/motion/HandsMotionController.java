package com.oberasoftware.robo.pep.core.motion;

import com.aldebaran.qi.helper.proxies.ALMotion;
import com.oberasoftware.robo.api.motion.controller.HandsController;
import com.oberasoftware.robo.pep.core.NaoUtil;

/**
 * @author Renze de Vries
 */
public class HandsMotionController implements HandsController {

    private final ALMotion alMotion;

    protected HandsMotionController(ALMotion alMotion) {
        this.alMotion = alMotion;
    }

    @Override
    public void openHands() {
        openHand(HAND_ID.ALL);
    }

    @Override
    public void closeHands() {
        closeHand(HAND_ID.ALL);
    }

    @Override
    public void openHand(HAND_ID hand_id) {
        NaoUtil.safeExecuteTask(() -> {
            switch(hand_id) {
                case LEFT:
                    alMotion.async().openHand("LHand");
                    break;
                case RIGHT:
                    alMotion.async().openHand("RHand");
                    break;
                case ALL:
                    alMotion.async().openHand("LHand");
                    alMotion.async().openHand("RHand");
                    break;
            }
        });
    }

    @Override
    public void closeHand(HAND_ID hand_id) {
        NaoUtil.safeExecuteTask(() -> {
            switch(hand_id) {
                case LEFT:
                    alMotion.async().closeHand("LHand");
                    break;
                case RIGHT:
                    alMotion.async().closeHand("RHand");
                    break;
                case ALL:
                    alMotion.async().closeHand("LHand");
                    alMotion.async().closeHand("RHand");
                    break;
            }
        });
    }
}
