package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
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

    private ALTextToSpeech textToSpeech;

    @Override
    public void say(String text, String language) {
        safeExecuteTask(() -> textToSpeech.say(text, language));
    }

    @Override
    public void activate(Map<String, String> properties) {
        try {
            textToSpeech = new ALTextToSpeech(naoSessionManager.getSession());
        } catch (Exception e) {
            LOG.error("Could not create text to speech proxy", e);
        }
    }

    @Override
    public void shutdown() {

    }
}