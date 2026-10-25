package org.firstinspires.ftc.teamcode.opModes.subClasses;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Configurable
public class Outtake {

    // FTC Robot Configuration names
    public static String FRONT_MOTOR_NAME = "frontOuttake";
    public static String BACK_MOTOR_NAME = "backOuttake";

    // PF tuning values, using encoder ticks per second
    public static double kF = 0.00045;
    public static double kP = 0.00268;

    // Maximum motor power
    public static double MAX_POWER = 1.0;

    private DcMotorEx frontMotor;
    private DcMotorEx backMotor;

    private double targetTPS = 0.0;
    private double currentTPS = 0.0;
    private double errorTPS = 0.0;
    private double commandedPower = 0.0;

    public void init(HardwareMap hardwareMap) {
        frontMotor = hardwareMap.get(
                DcMotorEx.class,
                FRONT_MOTOR_NAME
        );

        backMotor = hardwareMap.get(
                DcMotorEx.class,
                BACK_MOTOR_NAME
        );

        frontMotor.setDirection(
                DcMotorSimple.Direction.REVERSE
        );

        backMotor.setDirection(
                DcMotorSimple.Direction.FORWARD
        );

        frontMotor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.FLOAT
        );

        backMotor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.FLOAT
        );

        /*
         * We calculate the motor power ourselves.
         * Encoder velocity remains available through getVelocity().
         */
        frontMotor.setMode(
                DcMotor.RunMode.RUN_WITHOUT_ENCODER
        );

        backMotor.setMode(
                DcMotor.RunMode.RUN_WITHOUT_ENCODER
        );

        stopOuttake();
    }

    /**
     * Sets the shooter target in encoder ticks per second.
     */
    public void setTargetTPS(double targetTPS) {
        this.targetTPS = Math.max(0.0, targetTPS);
    }

    /**
     * Runs the PF controller.
     *
     * Call this once during every OpMode loop.
     */
    public void update() {
        readVelocity();

        if (targetTPS <= 0.0) {
            errorTPS = 0.0;
            commandedPower = 0.0;
            setMotorPower(0.0);
            return;
        }

        errorTPS = targetTPS - currentTPS;

        commandedPower =
                (kF * targetTPS)
                        + (kP * errorTPS);

        commandedPower = clip(
                commandedPower,
                0.0,
                MAX_POWER
        );

        setMotorPower(commandedPower);
    }

    /**
     * Compatibility method for existing OpModes.
     */
    public void updatePF() {
        update();
    }

    private void readVelocity() {
        double frontTPS =
                Math.abs(frontMotor.getVelocity());

        double backTPS =
                Math.abs(backMotor.getVelocity());

        currentTPS = (frontTPS + backTPS) / 2.0;
    }

    private void setMotorPower(double power) {
        frontMotor.setPower(power);
        backMotor.setPower(power);
    }

    public void stopOuttake() {
        targetTPS = 0.0;
        currentTPS = 0.0;
        errorTPS = 0.0;
        commandedPower = 0.0;

        if (frontMotor != null) {
            frontMotor.setPower(0.0);
        }

        if (backMotor != null) {
            backMotor.setPower(0.0);
        }
    }

    public boolean isAtSpeed(double toleranceTPS) {
        if (targetTPS <= 0.0) {
            return false;
        }

        return Math.abs(errorTPS)
                <= Math.abs(toleranceTPS);
    }

    public double getTargetTPS() {
        return targetTPS;
    }

    public double getCurrentTPS() {
        return currentTPS;
    }

    public double getErrorTPS() {
        return errorTPS;
    }

    public double getCommandedPower() {
        return commandedPower;
    }

    public double getFrontMotorPower() {
        return frontMotor.getPower();
    }

    public double getBackMotorPower() {
        return backMotor.getPower();
    }

    public double getFrontVelocityTPS() {
        return Math.abs(frontMotor.getVelocity());
    }

    public double getBackVelocityTPS() {
        return Math.abs(backMotor.getVelocity());
    }

    private double clip(
            double value,
            double minimum,
            double maximum
    ) {
        return Math.max(
                minimum,
                Math.min(maximum, value)
        );
    }
}