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
                    alMotion.openHand("LHand");
                    break;
                case RIGHT:
                    alMotion.openHand("RHand");
                    break;
                case ALL:
                    alMotion.openHand("LHand");
                    alMotion.openHand("RHand");
                    break;
            }
        });
    }

    @Override
    public void closeHand(HAND_ID hand_id) {
        NaoUtil.safeExecuteTask(() -> {
            switch(hand_id) {
                case LEFT:
                    alMotion.closeHand("LHand");
                    break;
                case RIGHT:
                    alMotion.closeHand("RHand");
                    break;
                case ALL:
                    alMotion.closeHand("LHand");
                    alMotion.closeHand("RHand");
                    break;
            }
        });
    }
}
