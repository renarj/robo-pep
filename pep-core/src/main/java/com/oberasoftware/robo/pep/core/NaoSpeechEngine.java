package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.helper.proxies.ALAnimatedSpeech;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.SpeechEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.oberasoftware.robo.pep.core.NaoUtil.safeExecuteTask;

/**
 * @author Renze de Vries
 */
@Component
public class NaoSpeechEngine implements SpeechEngine {
    private static final Logger LOG = LoggerFactory.getLogger(NaoSpeechEngine.class);

    @Autowired
    private NaoSessionManager naoSessionManager;

    private ALAnimatedSpeech textToSpeech;

    @Override
    public void say(String text, String language) {
        safeExecuteTask(() -> textToSpeech.say(text));
    }

    @Override
    public void activate(Robot robot, Map<String, String> properties) {
        try {
            textToSpeech = new ALAnimatedSpeech(naoSessionManager.getSession());
        } catch (Exception e) {
            LOG.error("Could not create text to speech proxy", e);
        }
    }

    @Override
    public void shutdown() {

    }
}
