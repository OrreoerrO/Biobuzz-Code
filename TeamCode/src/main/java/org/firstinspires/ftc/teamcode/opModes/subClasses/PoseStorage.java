package org.firstinspires.ftc.teamcode.opModes.subClasses;

import com.pedropathing.math.Pose;
import com.pedropathing.api.PoseFactory;

public class PoseStorage {

    private final PoseFactory p = PoseFactory.degrees();

    public enum Alliance {
        BLUE,
        RED,
        UNKNOWN
    }

    public static Pose currentPose = null;

    public static Alliance lastAlliance = Alliance.UNKNOWN;

    public static void setBlue() {
        lastAlliance = Alliance.BLUE;
    }

    public static void setRed() {
        lastAlliance = Alliance.RED;
    }

    public static void setUnknown() {
        lastAlliance = Alliance.UNKNOWN;
    }

    public static void setPose(Pose pose) {
        currentPose = pose;
    }

    public static void clearPose() {
        currentPose = null;
    }

    public static boolean isBlue() {
        return lastAlliance == Alliance.BLUE;
    }

    public static boolean isRed() {
        return lastAlliance == Alliance.RED;
    }

    public static boolean isUnknown() {
        return lastAlliance == Alliance.UNKNOWN;
    }

    public static void clearAll() {
        currentPose = null;
        lastAlliance = Alliance.UNKNOWN;
    }
}