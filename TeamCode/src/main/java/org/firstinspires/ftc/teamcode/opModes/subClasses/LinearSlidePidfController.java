package org.firstinspires.ftc.teamcode.opModes.subClasses;

import com.bylazar.configurables.annotations.Configurable;

@Configurable
public class LinearSlidePidfController {
    private final double kP;
    private final double kI;
    private final double kD;
    private final double kS;
    private final double kG;
    private final double kV;
    private final double velocityDeadband;
    private double integralSum = 0.0;
    private double lastError = 0.0;
    private long lastTimeNanos = 0;
    public LinearSlidePidfController(
            double kP,
            double kI,
            double kD,
            double kS,
            double kG,
            double kV,
            double velocityDeadband
    ) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kS = kS;
        this.kG = kG;
        this.kV = kV;
        this.velocityDeadband = velocityDeadband;
    }
    public double calculate(
            double targetPosition,
            double targetVelocity,
            double currentPosition,
            double currentVelocity
    ) {
        long now = System.nanoTime();
        double dt = 0.02;
        if (lastTimeNanos != 0) {
            dt = (now - lastTimeNanos) / 1_000_000_000.0;
        }
        lastTimeNanos = now;
        double positionError = targetPosition - currentPosition;
        double velocityError = targetVelocity - currentVelocity;
        integralSum += positionError * dt;
        integralSum = clamp(integralSum, -5000.0, 5000.0);
        double derivativeFromVelocity = velocityError;
        double pidOutput = kP * positionError
                + kI * integralSum
                + kD * derivativeFromVelocity;
        double feedforward = kG;
        if (Math.abs(targetVelocity) > velocityDeadband) {
            feedforward += kS * Math.signum(targetVelocity);
        }
        feedforward += kV * targetVelocity;
        lastError = positionError;
        return clamp(pidOutput + feedforward, -1.0, 1.0);
    }
    public void reset() {
        integralSum = 0.0;
        lastError = 0.0;
        lastTimeNanos = 0;
    }
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
