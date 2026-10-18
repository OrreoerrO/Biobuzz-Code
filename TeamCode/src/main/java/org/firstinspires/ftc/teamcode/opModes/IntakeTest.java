package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.opModes.subClasses.Intake;
import org.firstinspires.ftc.teamcode.opModes.subClasses.OuttakeBack;
import org.firstinspires.ftc.teamcode.opModes.subClasses.OuttakeFront;
import org.firstinspires.ftc.teamcode.opModes.subClasses.RobotConstants;
//import org.firstinspires.ftc.teamcode.opModes.subClasses.Outtake;

@Configurable
@TeleOp(name = "Intake Test", group = "Main")
public class IntakeTest extends OpMode {

    public Intake intake;
    //public Turret turret;
    public OuttakeFront outtakeFront;
    public OuttakeBack outtakeBack;
    public RobotConstants robotConstants;

    public DcMotor frontRightMotor;
    public DcMotor frontLeftMotor;
    public DcMotor backRightMotor;
    public DcMotor backLeftMotor;


    public void init() {

        intake = new Intake();
        intake.init(hardwareMap);

        //turret = new Turret();
        //turret.init(hardwareMap);

        outtakeFront = new OuttakeFront();
        outtakeFront.init(hardwareMap);

        outtakeBack = new OuttakeBack();
        outtakeBack.init(hardwareMap);

        robotConstants = new RobotConstants();

        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRight");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        RobotConstants.TURRET_POSITION = 0;

    }

    public void loop() {

        handleControls();

        driveRobot();

        telemetry();
        UpdatePIDF();


    }

    public void handleControls() {

        if (gamepad1.right_bumper) {
            intake.intake(RobotConstants.FRONT_INTAKE_SPEED, RobotConstants.BACK_INTAKE_SPEED);
        } else if (gamepad1.left_bumper) {
            intake.intake(-RobotConstants.FRONT_INTAKE_SPEED, -RobotConstants.BACK_INTAKE_SPEED);
        } else {
            intake.stopIntake();
        }

        if (gamepad1.cross) {
            outtakeFront.setTargetTPS(RobotConstants.FrontOuttakeTPS);
            outtakeBack.setTargetTPS(RobotConstants.BackOuttakeTPS);

        }

        if (gamepad1.triangle) {
            outtakeFront.stopOuttake();
            outtakeBack.stopOuttake();
        }

        if (gamepad1.dpadUpWasPressed()) {
            RobotConstants.FRONT_INTAKE_SPEED += 0.01;
            RobotConstants.BACK_INTAKE_SPEED += 0.01;
        }

        if (gamepad1.dpadDownWasPressed()) {
            RobotConstants.FRONT_INTAKE_SPEED -= 0.01;
            RobotConstants.BACK_INTAKE_SPEED -= 0.01;
        }

        RobotConstants.TURRET_POSITION = gamepad2.left_stick_x * 180;
       // turret.aimTurret((int) Math.round(TURRET_POSITION));
    }

    public void driveRobot() {
        double forward = gamepad1.left_stick_y * RobotConstants.DRIVE_SPEED;
        double strafe = -gamepad1.left_stick_x * RobotConstants.DRIVE_SPEED;
        double turn = -gamepad1.right_stick_x * RobotConstants.DRIVE_SPEED;

        double denominator = Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(turn), 1);
        double frontLeftPower = (forward + strafe + turn) / denominator;
        double backLeftPower = (forward - strafe + turn) / denominator;
        double frontRightPower = (forward - strafe - turn) / denominator;
        double backRightPower = (forward + strafe - turn) / denominator;

        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);

    }

    public void telemetry() {
        telemetry.addData("FrontOuttakeSpeed", OuttakeFront.leftVelocity);
        telemetry.addData("BackOuttakeSpeed", OuttakeBack.rightVelocity);
        telemetry.addData("IntakeFrontSpeed", RobotConstants.FRONT_INTAKE_SPEED);
        telemetry.addData("IntakeBackSpeed", RobotConstants.BACK_INTAKE_SPEED);
        telemetry.update();
    }

    public void UpdatePIDF() {
        outtakeFront.updatePIDF();
        outtakeBack.updatePIDF();
    }
    /*public void handleTurret() {

    }*/


}
