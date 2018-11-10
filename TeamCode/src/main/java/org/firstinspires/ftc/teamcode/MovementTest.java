package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.usb.RobotArmingStateNotifier;


import java.net.NoRouteToHostException;

@TeleOp (name="Teleop_1", group = "Teleop")
public class MovementTest extends LinearOpMode

{
    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;


    @Override
    public void runOpMode() throws InterruptedException {
        FL = hardwareMap.dcMotor.get("frontLeft");
        BL = hardwareMap.dcMotor.get("backLeft");
        FR = hardwareMap.dcMotor.get("frontRight");
        BR = hardwareMap.dcMotor.get("backRight");
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad2.right_stick_x == 0) {
                FL.setPower(gamepad2.right_stick_y);
                FR.setPower(gamepad2.right_stick_y);
                BL.setPower(gamepad2.right_stick_y);
                BR.setPower(gamepad2.right_stick_y);
            } else {
                if (gamepad2.right_stick_x < 0) {
                    FL.setPower(-1.0);
                    FR.setPower(1.0);
                    BL.setPower(1.0);
                    BR.setPower(-1.0);
                } else {
                    FL.setPower(1.0);
                    FR.setPower(-1.0);
                    BL.setPower(-1.0);
                    BR.setPower(1.0);
                }
            }


        }


    }
}


