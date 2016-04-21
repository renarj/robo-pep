package com.oberasoftware.robo.pep.core;

import com.oberasoftware.robo.api.MotionEngine;
import com.oberasoftware.robo.api.MotionResource;
import com.oberasoftware.robo.api.MotionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Renze de Vries
 */
@Component
public class NaoMotionEngine implements MotionEngine {
    @Autowired
    private NaoSessionManager sessionManager;

    @Override
    public boolean prepareWalk() {
        return false;
    }

    @Override
    public void loadResource(MotionResource resource) {

    }

    @Override
    public MotionTask walk() {
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
        return false;
    }

    @Override
    public boolean stopWalking() {
        return false;
    }
}
