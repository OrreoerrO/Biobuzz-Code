package org.firstinspires.ftc.teamcode.opModes.subClasses;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
public class Brakes {

    public Servo leftBrakeServo;
    public Servo rightBrakeServo;

    public void init(HardwareMap hardwareMap) {

        leftBrakeServo = hardwareMap.get(Servo.class, "leftBrakeServo");
        rightBrakeServo = hardwareMap.get(Servo.class, "rightBrakeServo");

        leftBrakeServo.setPosition(0);
        rightBrakeServo.setPosition(0);

    }

    public void lowerBrakes() {
        leftBrakeServo.setPosition(RobotConstants.leftLowerBrakePos);
        rightBrakeServo.setPosition(RobotConstants.rightLowerBrakePos);
    }

    public void liftBrakes() {
        leftBrakeServo.setPosition(RobotConstants.leftLiftBrakesPos);
        rightBrakeServo.setPosition(RobotConstants.rightLiftBrakesPos);
    }


}
