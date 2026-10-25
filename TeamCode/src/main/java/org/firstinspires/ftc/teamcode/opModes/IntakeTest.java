package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.opModes.subClasses.Brakes;
import org.firstinspires.ftc.teamcode.opModes.subClasses.Intake;
import org.firstinspires.ftc.teamcode.opModes.subClasses.Outtake;
import org.firstinspires.ftc.teamcode.opModes.subClasses.PoseStorage;
import org.firstinspires.ftc.teamcode.opModes.subClasses.RobotConstants;
import org.firstinspires.ftc.teamcode.opModes.subClasses.Turret;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Configurable
@TeleOp(name = "Intake Test", group = "Main")
public class IntakeTest extends OpMode {

    private final PoseFactory p = PoseFactory.degrees();

    private Intake intake;
    private Outtake outtake;

    private Brakes brakes;

    private Turret turret;

    private Follower follower;

    private DcMotor frontRightMotor;
    private DcMotor frontLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor backLeftMotor;

    private Servo blockServo;

    public static double TELEOP_GOAL_X = 6.0;
    public static double TELEOP_GOAL_Y = 138.0;

    private boolean previousG1DpadUp = false;
    private boolean previousG1DpadRight = false;
    private boolean previousLeftBumper = false;

    private boolean autoAimMode = true;

    private double estimatedFieldVelocityX = 0.0;
    private double estimatedFieldVelocityY = 0.0;

    @Override
    public void init() {

        follower = Constants.create(hardwareMap);

        intake = new Intake();
        intake.init(hardwareMap);

        outtake = new Outtake();
        outtake.init(hardwareMap);

        brakes = new Brakes();
        brakes.init(hardwareMap);

        turret = new Turret();
        turret.init(hardwareMap);

        frontRightMotor = hardwareMap.get(
                DcMotor.class,
                "frontRight"
        );

        frontLeftMotor = hardwareMap.get(
                DcMotor.class,
                "frontLeft"
        );

        backRightMotor = hardwareMap.get(
                DcMotor.class,
                "backRight"
        );

        backLeftMotor = hardwareMap.get(
                DcMotor.class,
                "backLeft"
        );

        blockServo = hardwareMap.get(Servo.class, "blockServo");

        frontRightMotor.setDirection(
                DcMotorSimple.Direction.REVERSE
        );

        backRightMotor.setDirection(
                DcMotorSimple.Direction.REVERSE
        );

        frontRightMotor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        frontLeftMotor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        backRightMotor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        backLeftMotor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        blockServo.setDirection(Servo.Direction.FORWARD);

        blockServo.setPosition(RobotConstants.blockClosePos);


        turret.setForward();

    }

    @Override
    public void loop() {

        handleControls();

        driveRobot();

        outtake.updatePF();

        displayTelemetry();

        Pose currentPose = follower.pose();
        PoseStorage.setPose(currentPose);

        double distCM = distanceToTeleOpGoalCM(currentPose);

        updateTurretAim(currentPose);

        handleTurretModeControls();

        DetermineGoal(currentPose);
    }

    private void handleControls() {

        // =====================================================
        // INTAKE
        // =====================================================

        if (gamepad1.right_bumper) {

            intake.intake(
                    RobotConstants.FRONT_INTAKE_SPEED,
                    RobotConstants.BACK_INTAKE_SPEED
            );
            blockServo.setPosition(RobotConstants.blockClosePos);
            RobotConstants.timer = 0;

        } else if (gamepad1.right_trigger > 0.3) {

            blockServo.setPosition(RobotConstants.blockOpenPos);
            RobotConstants.timer += 0.1;

            if (RobotConstants.timer > RobotConstants.delayAmount) {
                intake.intake(
                        RobotConstants.FRONT_INTAKE_SPEED,
                        RobotConstants.BACK_INTAKE_SPEED
                );
            }

        } else if (gamepad1.leftBumperWasPressed()) {

            intake.intake(
                    -RobotConstants.FRONT_INTAKE_SPEED,
                    -RobotConstants.BACK_INTAKE_SPEED
            );

        } else {

            intake.stopIntake();
            blockServo.setPosition(RobotConstants.blockClosePos);
            RobotConstants.timer = 0;
        }

        // =====================================================
        // OUTTAKE
        // =====================================================

        if (gamepad1.cross) {
            outtake.setTargetTPS(
                    RobotConstants.OUTTAKE_TPS
            );
        }

        if (gamepad1.triangle) {
            outtake.stopOuttake();
        }

        // =====================================================
        // INTAKE SPEED TUNING
        // =====================================================

        // =====================================================
        // FUTURE TURRET
        // =====================================================

        //RobotConstants.TURRET_POSITION =
        //        gamepad2.left_stick_x * 180.0;

        if (gamepad1.square) {
            RobotConstants.TURRET_POSITION = 0;
        }

        if (gamepad1.dpadRightWasPressed() && RobotConstants.TURRET_POSITION < 1) {
            RobotConstants.TURRET_POSITION += 0.05;
        } else if (gamepad1.dpadLeftWasPressed()  && RobotConstants.TURRET_POSITION > 0) {
            RobotConstants.TURRET_POSITION -= 0.05;
        }

        // =====================================================
        // BRAKES
        // =====================================================

        if (gamepad1.dpad_up) {
            brakes.liftBrakes();
        }

        if (gamepad1.dpad_down) {
            brakes.lowerBrakes();
        }
    }

    private void driveRobot() {

        double forward =
                gamepad1.left_stick_y
                        * RobotConstants.DRIVE_SPEED;

        double strafe =
                -gamepad1.left_stick_x
                        * RobotConstants.DRIVE_SPEED;

        double turn =
                -gamepad1.right_stick_x
                        * RobotConstants.DRIVE_SPEED;

        double denominator = Math.max(
                Math.abs(forward)
                        + Math.abs(strafe)
                        + Math.abs(turn),
                1.0
        );

        double frontLeftPower =
                (forward + strafe + turn)
                        / denominator;

        double backLeftPower =
                (forward - strafe + turn)
                        / denominator;

        double frontRightPower =
                (forward - strafe - turn)
                        / denominator;

        double backRightPower =
                (forward + strafe - turn)
                        / denominator;

        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);
    }

    private void displayTelemetry() {

        // =====================================================
        // OUTTAKE TARGETS
        // =====================================================

        telemetry.addData(
                "Configured TPS",
                RobotConstants.OUTTAKE_TPS
        );

        telemetry.addData(
                "Active TPS",
                outtake.getTargetTPS()
        );

        // =====================================================
        // VELOCITY
        // =====================================================

        telemetry.addData(
                "Current TPS",
                outtake.getCurrentTPS()
        );

        telemetry.addData(
                "Front TPS",
                outtake.getFrontVelocityTPS()
        );

        telemetry.addData(
                "Back TPS",
                outtake.getBackVelocityTPS()
        );

        // =====================================================
        // CONTROLLER
        // =====================================================

        telemetry.addData(
                "Error TPS",
                outtake.getErrorTPS()
        );

        telemetry.addData(
                "Commanded Power",
                outtake.getCommandedPower()
        );

        telemetry.addData(
                "At Speed",
                outtake.isAtSpeed(50.0)
        );

        // =====================================================
        // MOTOR OUTPUTS
        // =====================================================

        telemetry.addData(
                "Front Motor Power",
                outtake.getFrontMotorPower()
        );

        telemetry.addData(
                "Back Motor Power",
                outtake.getBackMotorPower()
        );

        // =====================================================
        // CONTROLLER CONSTANTS
        // =====================================================

        telemetry.addData(
                "kF",
                Outtake.kF
        );

        telemetry.addData(
                "kP",
                Outtake.kP
        );

        telemetry.addData(
                "Max Power",
                Outtake.MAX_POWER
        );

        // =====================================================
        // INTAKE
        // =====================================================

        telemetry.addData(
                "Front Intake Speed",
                RobotConstants.FRONT_INTAKE_SPEED
        );

        telemetry.addData(
                "Back Intake Speed",
                RobotConstants.BACK_INTAKE_SPEED
        );

        telemetry.addData("Timer", RobotConstants.timer);
        telemetry.addData("TurretPos", RobotConstants.TURRET_POSITION);

        telemetry.addData("Pose", follower.pose());

        telemetry.update();
    }

    private void updateTurretTargetFromTeleOpGoal() {
        turret.setTargetPose(getTeleOpGoalPose());
    }
    private Pose getTeleOpGoalPose() {
        return new Pose(
                TELEOP_GOAL_X,
                TELEOP_GOAL_Y,
                0
        );
    }

    private double distanceToTeleOpGoalCM(Pose robotPose) {
        if (robotPose == null) {
            return 0.0;
        }

        Pose goalPose = getTeleOpGoalPose();

        double dx = goalPose.x() - robotPose.x();
        double dy = goalPose.y() - robotPose.y();

        return Math.hypot(dx, dy) * 2.54;
    }

    private void handleTurretModeControls() {
        boolean currentLeftBumper = gamepad1.left_bumper;
        boolean currentDpadUp = gamepad1.dpad_up;

        if (currentLeftBumper && !previousLeftBumper) {
            autoAimMode = !autoAimMode;

            if (!autoAimMode) {
                turret.setForward();
            }
        }

        if (currentDpadUp && !previousG1DpadUp) {
            autoAimMode = true;
            updateTurretTargetFromTeleOpGoal();
        }

        previousLeftBumper = currentLeftBumper;
        previousG1DpadUp = currentDpadUp;
    }

    private void updateTurretAim(Pose currentPose) {
        if (autoAimMode) {
            turret.aimTurret(
                    currentPose,
                    estimatedFieldVelocityX,
                    estimatedFieldVelocityY
            );
        } else {
            turret.setForward();
        }
    }

    private void DetermineGoal(Pose currentPose) {
        if (currentPose.x() > 72) {
            TELEOP_GOAL_X = 100;
        } else {
            TELEOP_GOAL_X = 50;
        }
    }

    private void limitIntakeSpeeds() {

        RobotConstants.FRONT_INTAKE_SPEED = clip(
                RobotConstants.FRONT_INTAKE_SPEED,
                -1.0,
                1.0
        );

        RobotConstants.BACK_INTAKE_SPEED = clip(
                RobotConstants.BACK_INTAKE_SPEED,
                -1.0,
                1.0
        );
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

    @Override
    public void stop() {

        if (outtake != null) {
            outtake.stopOuttake();
        }

        if (intake != null) {
            intake.stopIntake();
        }

        if (frontLeftMotor != null) {
            frontLeftMotor.setPower(0.0);
        }

        if (frontRightMotor != null) {
            frontRightMotor.setPower(0.0);
        }

        if (backLeftMotor != null) {
            backLeftMotor.setPower(0.0);
        }

        if (backRightMotor != null) {
            backRightMotor.setPower(0.0);
        }
    }
}