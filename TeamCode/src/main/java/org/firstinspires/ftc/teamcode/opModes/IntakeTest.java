package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.opModes.subClasses.Intake;
import org.firstinspires.ftc.teamcode.opModes.subClasses.Turret;
//import org.firstinspires.ftc.teamcode.opModes.subClasses.Outtake;

@Configurable
@TeleOp(name = "Intake Test", group = "Main")
public class IntakeTest extends OpMode {

    public Intake intake;
    //public Turret turret;
    //public Outtake outtake;

    public DcMotor frontRightMotor;
    public DcMotor frontLeftMotor;
    public DcMotor backRightMotor;
    public DcMotor backLeftMotor;

    public DcMotor frontOuttake;
    public DcMotor backOuttake;


    public static double FRONT_INTAKE_SPEED = 1;
    public static double BACK_INTAKE_SPEED = 1;
    public static double TURRET_POSITION = 0;

    public static double DRIVE_SPEED = 1;

    public static double outtakeSpeed = 0.5;
    public static double test = 1;

    public static double frontMulti = 1.0;
    public static double backMulti = 1.0;

    public void init() {

        intake = new Intake();
        intake.init(hardwareMap);

        //turret = new Turret();
        //turret.init(hardwareMap);

        //outtake = new Outtake();
        //outtake.init(hardwareMap);

        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRight");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");

        frontOuttake = hardwareMap.get(DcMotor.class, "frontOuttake");
        backOuttake = hardwareMap.get(DcMotor.class, "backOuttake");

        frontOuttake.setDirection(DcMotorSimple.Direction.REVERSE);
        backOuttake.setDirection(DcMotorSimple.Direction.REVERSE);

        frontOuttake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backOuttake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        TURRET_POSITION = 0;

    }

    public void loop() {

        handleControls();

        driveRobot();

        telemetry();
    }

    public void handleControls() {

        if (gamepad1.right_bumper) {
            intake.intake(FRONT_INTAKE_SPEED, BACK_INTAKE_SPEED);
        } else if (gamepad1.left_bumper) {
            intake.intake(-FRONT_INTAKE_SPEED, -BACK_INTAKE_SPEED);
        } else {
            intake.stopIntake();
        }

        if (gamepad1.cross) {
            frontOuttake.setPower(outtakeSpeed);
            backOuttake.setPower(outtakeSpeed);
        }

        if (gamepad1.triangle) {
            frontOuttake.setPower(0);
            backOuttake.setPower(0);
        }

        TURRET_POSITION = gamepad2.left_stick_x * 180;
       // turret.aimTurret((int) Math.round(TURRET_POSITION));
    }

    public void driveRobot() {
        double forward = gamepad1.left_stick_y * DRIVE_SPEED;
        double strafe = -gamepad1.left_stick_x * DRIVE_SPEED;
        double turn = -gamepad1.right_stick_x * DRIVE_SPEED;

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
        telemetry.addData("test", test);
        //telemetry.addData("leftOuttakeSpeed", Outtake.leftVelocity);
        //telemetry.addData("rightOuttakeSpeed", Outtake.rightVelocity);
    }
    /*public void handleTurret() {

    }*/


}
