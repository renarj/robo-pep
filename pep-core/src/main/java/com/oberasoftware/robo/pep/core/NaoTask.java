package com.oberasoftware.robo.pep.core;

/**
 * @author Renze de Vries
 */
@FunctionalInterface
public interface NaoTask {
    void run() throws Exception;
}
