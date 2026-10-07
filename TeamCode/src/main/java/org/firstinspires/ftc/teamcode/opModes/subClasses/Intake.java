package org.firstinspires.ftc.teamcode.opModes.subClasses;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Configurable
public class Intake {

    public DcMotor IntakeMotorLeft;
    public DcMotor IntakeMotorRight;


    public void init(HardwareMap hardwareMap) {

        IntakeMotorLeft = hardwareMap.get(DcMotor.class, "intakeMotorFront");
        IntakeMotorRight = hardwareMap.get(DcMotor.class, "intakeMotorBack");

        IntakeMotorRight.setDirection(DcMotorSimple.Direction.REVERSE);
        IntakeMotorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void intake(double leftPower, double rightPower) {

        IntakeMotorLeft.setPower(leftPower);
        IntakeMotorRight.setPower(rightPower);

    }

    public void stopIntake() {
        IntakeMotorRight.setPower(0);
        IntakeMotorLeft.setPower(0);
    }
}
