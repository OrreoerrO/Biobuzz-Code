package org.firstinspires.ftc.teamcode.opModes.subClasses;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
public class Turret {

    public Servo TurretServo;

    public void init(HardwareMap hardwareMap) {
        TurretServo = hardwareMap.get(Servo.class, "turretServo");
    }

    public void aimTurret(int position) {
        TurretServo.setPosition(position);
    }
}