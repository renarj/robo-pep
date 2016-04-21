package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.Session;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class DefaultNaoSessionManager implements NaoSessionManager {
    @Override
    public Session getSession() {
        return null;
    }
}
