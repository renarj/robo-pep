package com.oberasoftware.robo.pep.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Renze de Vries
 */
public class NaoUtil {
    private static final Logger LOG = LoggerFactory.getLogger(NaoUtil.class);

    public static boolean safeExecuteTask(NaoTask task) {
        try {
            task.run();
            return true;
        } catch (Exception e) {
            LOG.error("Could not execute NAO Task", e);
            return false;
        }
    }
}
