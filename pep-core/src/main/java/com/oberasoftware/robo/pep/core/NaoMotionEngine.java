package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.oberasoftware.robo.api.MotionEngine;
import com.oberasoftware.robo.api.MotionResource;
import com.oberasoftware.robo.api.MotionTask;
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
public class NaoMotionEngine implements MotionEngine {
    private static final Logger LOG = LoggerFactory.getLogger(NaoMotionEngine.class);

    @Autowired
    private NaoSessionManager sessionManager;

    private ALMotion alMotion;
    private ALRobotPosture posture;

    @Override
    public void activate(Map<String, String> properties) {
        try {
            Session session = sessionManager.getSession();
            alMotion = new ALMotion(session);
            posture = new ALRobotPosture(session);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    @Override
    public boolean prepareWalk() {
        try {
            posture.applyPosture("Stand", 0.5f);
            return true;
        } catch (CallError | InterruptedException e) {
            LOG.error("", e);
            return false;
        }
    }

    @Override
    public void loadResource(MotionResource resource) {

    }

    @Override
    public MotionTask walk() {
        try {
            alMotion.move(1.0f, 0.0f, 0.0f);
        } catch (CallError | InterruptedException e) {
            LOG.error("", e);

        }
        return null;
    }

    @Override
    public MotionTask runMotion(String motionName) {
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
        try {
            alMotion.killAll();
        } catch (CallError | InterruptedException e) {
            LOG.error("", e);
        }
        return false;
    }

    @Override
    public boolean stopWalking() {
        return stopAllTasks();
    }


}
