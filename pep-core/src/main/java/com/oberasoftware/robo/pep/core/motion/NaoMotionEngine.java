package com.oberasoftware.robo.pep.core.motion;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALBehaviorManager;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.oberasoftware.robo.api.MotionEngine;
import com.oberasoftware.robo.api.MotionTask;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.motion.KeyFrame;
import com.oberasoftware.robo.api.motion.MotionResource;
import com.oberasoftware.robo.api.motion.WalkDirection;
import com.oberasoftware.robo.api.motion.controller.MotionController;
import com.oberasoftware.robo.pep.core.NaoSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.oberasoftware.robo.pep.core.NaoUtil.safeExecuteTask;

/**
 * @author Renze de Vries
 */
@Component
public class NaoMotionEngine implements MotionEngine {
    private static final Logger LOG = LoggerFactory.getLogger(NaoMotionEngine.class);

    @Autowired
    private NaoSessionManager sessionManager;

    @Autowired
    private HandsMotionController handsMotionController;

    private ALMotion alMotion;
    private ALBehaviorManager behaviorManager;
    private ALRobotPosture alPosture;

    @Override
    public void activate(Robot robot, Map<String, String> properties) {
        try {
            Session session = sessionManager.getSession();
            alMotion = new ALMotion(session);
            alPosture = new ALRobotPosture(session);
            behaviorManager = new ALBehaviorManager(session);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    @Override
    public void shutdown() {
        safeExecuteTask(() -> alMotion.killAll());
        safeExecuteTask(() -> behaviorManager.stopAllBehaviors());
        rest();
    }

    @Override
    public <T extends MotionController> Optional<T> getMotionController(String controllerName) {
        if(controllerName.equalsIgnoreCase("hands")) {
            return Optional.of((T)handsMotionController);
        }
        return Optional.empty();
    }

    @Override
    public KeyFrame getCurrentPositionAsKeyFrame() {
        return null;
    }

    @Override
    public List<String> getMotions() {
        try {
            return behaviorManager.getBehaviorNames();
        } catch (CallError | InterruptedException e) {
            LOG.error("Could not load behaviours", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean prepareWalk() {
        return safeExecuteTask(() -> alPosture.goToPosture("Stand", 0.5f));
    }

    @Override
    public void loadResource(MotionResource resource) {

    }

    @Override
    public MotionTask goToPosture(String posture) {
        safeExecuteTask(() -> alPosture.goToPosture(posture, 0.5f));

        return null;
    }

    @Override
    public MotionTask walkForward() {
        return walk(WalkDirection.FORWARD);
    }

    @Override
    public MotionTask walk(WalkDirection direction) {
        if(direction == WalkDirection.BACKWARD) {
            safeExecuteTask(() -> alMotion.move(-1.0f, 0.0f, 0.0f));
        } else {
            //forward
            safeExecuteTask(() -> alMotion.move(1.0f, 0.0f, 0.0f));
        }
        safeExecuteTask(() -> alMotion.waitUntilMoveIsFinished());

        return null;
    }

    @Override
    public MotionTask walk(WalkDirection walkDirection, float i) {
        if(walkDirection == WalkDirection.FORWARD) {
            safeExecuteTask(() -> alMotion.moveTo(i, 0f, 0.0f));
        } else {
            safeExecuteTask(() -> alMotion.moveTo(-i, 0f, 0.0f));
        }

        return null;
    }

    @Override
    public boolean rest() {
        return safeExecuteTask(() -> alMotion.rest());
    }

    @Override
    public MotionTask runMotion(String motionName) {
        LOG.info("Executing motion: {}", motionName);
        safeExecuteTask(() -> behaviorManager.runBehavior(motionName));
        return null;
    }

    @Override
    public List<MotionTask> getActiveTasks() {
        return null;
    }

    @Override
    public boolean stopTask(MotionTask task) {
        return false;
    }

    @Override
    public boolean stopAllTasks() {
        return safeExecuteTask(() -> alMotion.killAll());
    }

    @Override
    public boolean stopWalking() {
        return safeExecuteTask(() -> alMotion.move(0.0f, 0.0f, 0.0f));
    }

}
