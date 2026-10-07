package org.firstinspires.ftc.teamcode.opModes.subClasses;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Configurable
public class Slider {

    public DcMotorEx SliderMotorTop;
    public DcMotor SliderMotorBottom;
    public Telemetry telemetry;

    public LinearSlidePidfController linearSlidePidfController;

    public static double kP = 0.002;
    public static double kI = 0.0;
    public static double kD = 0.0;
    public static double kG = 0.05;
    public static double kS = 0.0;
    public static double kV = 0.0;
    private static final double VELOCITY_DEADBAND = 10.0; // ticks/sec
    public static double TARGET_VELOCITY = 500;
    public static double TARGET_POSITION = 0;
    public static double currentPosition = 0;
    public static double currentVelocity = 0;
    public static double targetVelocity = 0;
    public static double targetPosition = 0;
    public static double MAX_EXTENSION = 800;
    public static double MIN_EXTENSION = 0;

    public void init(HardwareMap hardwareMap) {

        linearSlidePidfController = new LinearSlidePidfController(kP, kI, kD, kS, kG, kV, VELOCITY_DEADBAND);

        SliderMotorTop = hardwareMap.get(DcMotorEx.class, "slideOne");
        SliderMotorBottom = hardwareMap.get(DcMotor.class, "slideTwo");

        SliderMotorTop.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SliderMotorBottom.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        SliderMotorTop.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        SliderMotorTop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SliderMotorBottom.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        SliderMotorTop.setDirection(DcMotorSimple.Direction.REVERSE);
        SliderMotorBottom.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    /*
    public void stopSlider() {
        SliderMotorTop.setPower(0);
        SliderMotorBottom.setPower(0);
    }


    public void slide(int position, double power) {

        SliderMotorTop.setTargetPosition(position);
        SliderMotorBottom.setTargetPosition(position);

        SliderMotorTop.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        SliderMotorBottom.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        SliderMotorTop.setPower(power);
        SliderMotorBottom.setPower(power);

    } */

    public void update() {
        currentPosition = SliderMotorTop.getCurrentPosition();
        currentVelocity = SliderMotorTop.getVelocity();
        targetVelocity = TARGET_VELOCITY;
        targetPosition = TARGET_POSITION;
        double power = linearSlidePidfController.calculate(
                targetPosition,
                targetVelocity,
                currentPosition,
                currentVelocity
        );
        if (currentPosition < MAX_EXTENSION && currentPosition > MIN_EXTENSION) {
            SliderMotorTop.setPower(power);
            SliderMotorBottom.setPower(power);
        } else {
            SliderMotorTop.setPower(0);
            SliderMotorBottom.setPower(0);
        }

        /*telemetry.addData("Slide Target", targetPosition);
        telemetry.addData("Slide Current", currentPosition);
        telemetry.addData("Slide Error", targetPosition - currentPosition);
        telemetry.addData("Target Velocity", targetVelocity);
        telemetry.addData("Current Velocity", currentVelocity);
        telemetry.addData("Slide Power", power); */
    }

    public void stop() {
        SliderMotorTop.setPower(0);
        SliderMotorBottom.setPower(0);
    }



    public void forwardDirection() {
        SliderMotorTop.setDirection(DcMotorSimple.Direction.REVERSE);
        SliderMotorBottom.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void reverseDirection() {
        SliderMotorTop.setDirection(DcMotorSimple.Direction.FORWARD);
        SliderMotorBottom.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /*public void reset() {
        SliderMotorBottom.setPosi;
    }*/
}
