package com.oberasoftware.robo.pep.container;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.robo.api.GenericRobotEventHandler;
import com.oberasoftware.robo.api.Robot;
import com.oberasoftware.robo.api.events.DistanceSensorEvent;
import com.oberasoftware.robo.api.sensors.EventSource;
import com.oberasoftware.robo.core.SpringAwareRobotBuilder;
import com.oberasoftware.robo.core.sensors.AnalogToDistanceConverter;
import com.oberasoftware.robo.core.sensors.AnalogToPercentageConverter;
import com.oberasoftware.robo.core.sensors.DistanceSensor;
import com.oberasoftware.robo.core.sensors.GyroSensor;
import com.oberasoftware.robo.pep.core.NaoMotionEngine;
import com.oberasoftware.robo.pep.core.NaoServoDriver;
import com.oberasoftware.robo.pep.core.sensors.NaoDistanceSensorPort;
import com.oberasoftware.robo.service.MotionFunction;
import com.oberasoftware.robo.service.PositionFunction;
import com.oberasoftware.robo.service.ServiceConfiguration;
import com.oberasoftware.robo.service.model.MotionModel;
import com.oberasoftware.robo.service.model.ServoModel;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;
import com.sdl.odata.service.ODataServiceConfiguration;
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

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Renze de Vries
 */
@Configuration
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class })
@Import({
        ODataServiceConfiguration.class,
        ServiceConfiguration.class
})
@ComponentScan
public class PepContainer {
    private static final Logger LOG = LoggerFactory.getLogger(PepContainer.class);

    public static void main(String[] args) {
        LOG.info("Starting Robot Service Application container");

        SpringApplication springApplication = new SpringApplication(PepContainer.class);
        springApplication.setShowBanner(false);
        ConfigurableApplicationContext context = springApplication.run(args);

        ODataEdmRegistry registry = context.getBean(ODataEdmRegistry.class);
        registry.registerClasses(newArrayList(MotionModel.class, ServoModel.class, MotionFunction.class, PositionFunction.class));

        Robot robot = new SpringAwareRobotBuilder(context)
                .motionEngine(NaoMotionEngine.class)
                .servoDriver(NaoServoDriver.class)
                .sensor(new DistanceSensor("distance", new NaoDistanceSensorPort()))
                .sensor(new GyroSensor("gyro", adsDriver.getPort("A2"), adsDriver.getPort("A3"), new AnalogToPercentageConverter()))
//                .remote("http:://192.168.99.100", "user", "password")
                .build();
        RobotEventHandler eventHandler = new RobotEventHandler(robot);
        robot.listen(eventHandler);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Killing the robot gracefully on shutdown");
            robot.shutdown();
        }));
    }

    public static class RobotEventHandler implements GenericRobotEventHandler {
        private Robot robot;

        public RobotEventHandler(Robot robot) {
            this.robot = robot;
        }

        @EventSubscribe
        @EventSource("distance")
        public void receive(DistanceSensorEvent event) {
            LOG.info("Received a distance event: {}", event);

            if(event.getDistance() < 20) {
                LOG.info("Killing all tasks");
                robot.getMotionEngine().stopAllTasks();
            }
        }
    }}
