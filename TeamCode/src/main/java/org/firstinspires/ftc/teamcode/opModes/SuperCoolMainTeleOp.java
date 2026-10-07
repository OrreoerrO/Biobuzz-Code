/*package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.opModes.subClasses.ArduCam;
import org.firstinspires.ftc.teamcode.opModes.subClasses.Intake;
import org.firstinspires.ftc.teamcode.opModes.subClasses.PoseStorage;
import org.firstinspires.ftc.teamcode.opModes.subClasses.Slider;
import org.firstinspires.ftc.teamcode.opModes.subClasses.Turret;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

import java.util.Locale;

@Configurable
@Disabled
@TeleOp(name = "00 - Main TeleOp", group = "Main")
public class SuperCoolMainTeleOp extends OpMode {

    private Follower follower;
    private Slider slider;
    private Intake intake;
    private ArduCam arduCam;
    public Turret turret;

    public DcMotor frontRightMotor;
    public DcMotor frontLeftMotor;
    public DcMotor backRightMotor;
    public DcMotor backLeftMotor;


    public static double DRIVE_SPEED = 1;
    public static int SLIDER_POSITION = 100;
    public static double SLIDER_POWER = 0.5;
    public static double leftIntakeSpeed = 0.5;
    public static double rightIntakeSpeed = 0.5;

    public static double BLUE_GOAL_X = 0;
    public static double BLUE_GOAL_Y = 0;

    public static double RED_GOAL_X = 0;
    public static double RED_GOAL_Y = 0;

    public static double BLUE_OFFSET = 0.0;
    public static double RED_OFFSET = 0.0;

    public static double BLUE_RESET_X = 0;
    public static double BLUE_RESET_Y = 0;
    public static double BLUE_RESET_HEADING_DEG = 0;

    public static double RED_RESET_X = 0;
    public static double RED_RESET_Y = 0;
    public static double RED_RESET_HEADING_DEG = 0;

    public static double TELEOP_GOAL_X = 0;
    public static double TELEOP_GOAL_Y = 0;

    public static double TELEOP_RESET_X = 0;
    public static double TELEOP_RESET_Y = 0;
    public static double TELEOP_RESET_HEADING_DEG = 0;

    public static boolean USE_POSE_DELTA_VELOCITY = false;

    private Pose previousVelocityPose = null;
    private double previousVelocityTime = 0.0;

    private double estimatedFieldVelocityX = 0.0;
    private double estimatedFieldVelocityY = 0.0;
    private double estimatedFieldSpeed = 0.0;
    private String allianceLabel = "UNKNOWN";

    public static boolean SLIDING = true;

    @Override
    public void init() {

        slider = new Slider();
        intake = new Intake();

        slider.init(hardwareMap);
        intake.init(hardwareMap);

        arduCam.init();

        //slider.stopSlider();

        //follower = Constants.createFollower(hardwareMap);
        //follower.setStartingPose(new Pose(0, 0, 0));

        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");
        frontRightMotor = hardwareMap.get(DcMotor.class,"frontRight");
        backRightMotor = hardwareMap.get(DcMotor.class,"backRight");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        follower = Constants.create(hardwareMap);

        if (PoseStorage.currentPose != null) {
            follower.setPose(PoseStorage.currentPose);
        } else {
            follower.setPose(getTeleOpResetPose());
        }
    }

    public void start() {
        follower.manual();
    }

    @Override
    public void loop() {

        driveRobot();

        handleControls();

        telemetry();

        if (SLIDING == true) {
            slider.update();
        } else if (SLIDING == false) {
            slider.stop();
        }

    }

    private void driveRobot() {
        double forward = -gamepad1.left_stick_y * DRIVE_SPEED;
        double strafe = gamepad1.left_stick_x * DRIVE_SPEED;
        double turn = gamepad1.right_stick_x * DRIVE_SPEED;

        double denominator = Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(turn), 1);
        double frontLeftPower = (forward + strafe + turn) / denominator;
        double backLeftPower = (forward - strafe + turn) / denominator;
        double frontRightPower = (forward - strafe - turn) / denominator;
        double backRightPower = (forward + strafe - turn) / denominator;

        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);


        follower.manual(forward, strafe, turn);

        follower.update();
    }

    private void handleControls() {

        if (gamepad1.left_bumper) {
            intake.intake(leftIntakeSpeed, rightIntakeSpeed);
        } else {
            intake.stopIntake();
        }

    }

    private void applyAllianceFromPoseStorage() {
        if (PoseStorage.isRed()) {
            allianceLabel = "RED";

            TELEOP_GOAL_X = RED_GOAL_X;
            TELEOP_GOAL_Y = RED_GOAL_Y;

            TELEOP_RESET_X = RED_RESET_X;
            TELEOP_RESET_Y = RED_RESET_Y;
            TELEOP_RESET_HEADING_DEG = RED_RESET_HEADING_DEG;

            return;
        }

        if (PoseStorage.isBlue()) {
            allianceLabel = "BLUE";

            TELEOP_GOAL_X = BLUE_GOAL_X;
            TELEOP_GOAL_Y = BLUE_GOAL_Y;

            TELEOP_RESET_X = BLUE_RESET_X;
            TELEOP_RESET_Y = BLUE_RESET_Y;
            TELEOP_RESET_HEADING_DEG = BLUE_RESET_HEADING_DEG;

            return;
        }

        allianceLabel = "UNKNOWN - DEFAULT BLUE";

        TELEOP_GOAL_X = BLUE_GOAL_X;
        TELEOP_GOAL_Y = BLUE_GOAL_Y;

        TELEOP_RESET_X = BLUE_RESET_X;
        TELEOP_RESET_Y = BLUE_RESET_Y;
        TELEOP_RESET_HEADING_DEG = BLUE_RESET_HEADING_DEG;
    }


    private void updateEstimatedFieldVelocity(Pose currentPose) {
        if (!USE_POSE_DELTA_VELOCITY || currentPose == null) {
            estimatedFieldVelocityX = 0.0;
            estimatedFieldVelocityY = 0.0;
            estimatedFieldSpeed = 0.0;
            return;
        }

        double currentTime = getRuntime();

        if (previousVelocityPose == null) {
            resetVelocityEstimate(currentPose);
            return;
        }

        double dt = currentTime - previousVelocityTime;

        if (dt <= 0.001) {
            estimatedFieldVelocityX = 0.0;
            estimatedFieldVelocityY = 0.0;
            estimatedFieldSpeed = 0.0;
            return;
        }

        double dx = currentPose.x() - previousVelocityPose.x();
        double dy = currentPose.y() - previousVelocityPose.y();

        estimatedFieldVelocityX = dx / dt;
        estimatedFieldVelocityY = dy / dt;
        estimatedFieldSpeed = Math.hypot(
                estimatedFieldVelocityX,
                estimatedFieldVelocityY
        );

        previousVelocityPose = currentPose;
        previousVelocityTime = currentTime;
    }

    private void resetVelocityEstimate(Pose pose) {
        previousVelocityPose = pose;
        previousVelocityTime = getRuntime();

        estimatedFieldVelocityX = 0.0;
        estimatedFieldVelocityY = 0.0;
        estimatedFieldSpeed = 0.0;
    }

    private Pose getTeleOpGoalPose() {
        return new Pose(
                TELEOP_GOAL_X,
                TELEOP_GOAL_Y,
                0
        );
    }

    private Pose getTeleOpResetPose() {
        return new Pose(
                TELEOP_RESET_X,
                TELEOP_RESET_Y,
                Math.toRadians(TELEOP_RESET_HEADING_DEG)
        );
    }

    private void updateTurretTargetFromTeleOpGoal() {
        turret.setTargetPose(getTeleOpGoalPose());
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

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private String format(String pattern, double value) {
        return String.format(Locale.US, pattern, value);
    }

    private void telemetry() {

        //telemetry.addData("SliderMotorTopVel", slider.SliderMotorTop.getVelocity());
        telemetry.addData("SliderMotorTopVel", slider.SliderMotorTop.getVelocity());
        telemetry.addData("SliderCurrentPosition: ", Slider.currentPosition);
        telemetry.addData("SliderCurrentVelocity: ", Slider.currentVelocity);
        telemetry.addData("SliderTargetVelocity: ", Slider.TARGET_VELOCITY);
        telemetry.addData("SliderTargetPosition: ", Slider.targetPosition);
        telemetry.addData("fx", arduCam.fx);
        telemetry.addData("fy", arduCam.fy);
        telemetry.addData("cx", arduCam.cx);
        telemetry.addData("cy", arduCam.cy);
        telemetry.update();
    }
}*/