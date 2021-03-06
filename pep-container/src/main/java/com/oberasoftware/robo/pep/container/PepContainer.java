package com.oberasoftware.robo.pep.container;

import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.home.core.mqtt.MQTTConfiguration;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.SpeechEngine;
import com.oberasoftware.robo.api.events.BumperEvent;
import com.oberasoftware.robo.api.events.DistanceSensorEvent;
import com.oberasoftware.robo.api.events.TextualSensorEvent;
import com.oberasoftware.robo.cloud.RemoteCloudDriver;
import com.oberasoftware.robo.cloud.RemoteConfiguration;
import com.oberasoftware.robo.core.CoreConfiguration;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.core.sensors.BumperSensor;
import com.oberasoftware.robo.pep.core.*;
import com.oberasoftware.robo.pep.core.motion.NaoMotionEngine;
import com.oberasoftware.robo.pep.core.sensors.NaoSensorDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Renze de Vries
 */
@Configuration
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class })
@Import({
        NaoConfiguration.class,
        CoreConfiguration.class,
        RemoteConfiguration.class,
        MQTTConfiguration.class
})
@ComponentScan
public class PepContainer {
    private static final Logger LOG = LoggerFactory.getLogger(PepContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting Robot Service Application container");

        SpringApplication springApplication = new SpringApplication(PepContainer.class);
        ConfigurableApplicationContext context = springApplication.run(args);
        NaoSessionManager sessionManager = context.getBean(NaoSessionManager.class);
        NaoUtil.safeExecuteTask(() -> new ALAutonomousLife(sessionManager.getSession()).setState("disabled"));

        Robot robot = new SpringAwareRobotBuilder("peppy", context)
                .motionEngine(NaoMotionEngine.class)
                .servoDriver(NaoServoDriver.class)
                .capability(NaoSpeechEngine.class)
                .capability(NaoQRScanner.class)
//                .sensor(new DistanceSensor("distance", NaoSensorDriver.SONAR_PORT), NaoSensorDriver.class)
                .sensor(new BumperSensor("head", NaoSensorDriver.TOUCH_HEAD), NaoSensorDriver.class)
                .remote(RemoteCloudDriver.class)
                .build();
        LOG.info("Robot has been constructed");

        RobotEventHandler eventHandler = new RobotEventHandler(robot);
        robot.listen(eventHandler);

        robot.getMotionEngine().prepareWalk();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }


    public static class RobotEventHandler implements EventHandler {
        private Robot robot;

        public RobotEventHandler(Robot robot) {
            this.robot = robot;
        }

        @EventSubscribe
        public void receive(DistanceSensorEvent event) {
            LOG.info("Received a distance event: {}", event);

            if(event.getDistance() < 30) {
                LOG.info("Stopping walking motion");
                robot.getMotionEngine().stopWalking();
            }
        }

        @EventSubscribe
        public void receive(TextualSensorEvent event) {
            LOG.info("Barcode scanned: {}", event.getValue());

            robot.getCapability(NaoSpeechEngine.class).say(event.getValue(), "english");
        }

        @EventSubscribe
        public void receive(BumperEvent event) {
            String source = event.getValue().getSource();
            if(event.isTriggered()) {
                LOG.info("Head was touched on: {}", event.getLabel());
                if(source.equalsIgnoreCase("MiddleTactilTouched")) {
                    robot.getCapability(SpeechEngine.class).say("Someone just touched my head", "english");
                }
            }
        }
    }
}
