package com.oberasoftware.robo.pep.core;

import com.aldebaran.qi.helper.proxies.ALBarcodeReader;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.ActivatableCapability;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.events.TextualSensorEvent;
import com.oberasoftware.robo.api.sensors.EventSource;
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
public class NaoQRScanner implements ActivatableCapability, EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NaoQRScanner.class);

    @Autowired
    private NaoSessionManager sessionManager;

    @Autowired
    private SensorManager sensorManager;

    private ALBarcodeReader barcodeReader;
    private Robot robot;

    @Override
    public void activate(Robot robot, Map<String, String> properties) {
        try {
            LOG.info("Activating barcode reader");
            barcodeReader = new ALBarcodeReader(sessionManager.getSession());
            barcodeReader.subscribe(robot.getName());
            this.robot = robot;

            sensorManager.registerListener(this);
        } catch(Exception e) {
            LOG.error("Could not create barcode reader", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            barcodeReader.unsubscribe(robot.getName());
        } catch (Exception e) {
            LOG.error("Could not unsubscrube from the barcode reader", e);
        }
    }

    @EventSubscribe
    @EventSource("BarcodeReader/BarcodeDetected")
    public void receiveQRData(ListValueEvent event) {
        List<Object> values = event.getValues();
        LOG.info("Barcodes detected: {}", values.size());

        for(Object barcode : values) {
            List<Object> data = (List<Object>)barcode;

            String barcodeValue = (String) data.get(0);
            LOG.info("Barcode detected: {} raw data: {}", barcodeValue, data);
            robot.publish(new TextualSensorEvent(robot.getName(), "barcode", "text", barcodeValue));
        }
    }
}
