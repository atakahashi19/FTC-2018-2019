package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.usb.RobotArmingStateNotifier;
import com.qualcomm.robotcore.util.Range;


import java.net.NoRouteToHostException;

@TeleOp (name="Teleop_1", group = "Teleop")
public class Movement2 extends LinearOpMode

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
           float LF = gamepad1.left_stick_y-gamepad1.left_stick_x;
           float LB = gamepad1.left_stick_y+gamepad1.left_stick_x;
           float RF = gamepad1.right_stick_y+gamepad1.left_stick_x;
           float RB = gamepad1.right_stick_y-gamepad1.left_stick_x;

           LF = Range.clip(LF, -1, 1);
           RF = Range.clip(RF, -1, 1);
           LB = Range.clip(LB, -1, 1);
           RB = Range.clip(RB, -1, 1);

           FL.setPower(LF);
           BL.setPower(LB);
           FR.setPower(RF);
           BR.setPower(RB);


        }


    }
}


