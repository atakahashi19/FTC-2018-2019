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
    private DcMotor Grabber;
    private DcMotor Arm;
    private Servo right;
    private Servo left;

    @Override
    public void runOpMode() throws InterruptedException {
        FL = hardwareMap.dcMotor.get("frontLeft");
        BL = hardwareMap.dcMotor.get("backLeft");
        FR = hardwareMap.dcMotor.get("frontRight");
        BR = hardwareMap.dcMotor.get("backRight");
        Grabber = hardwareMap.dcMotor.get("Grabber");
        Arm = hardwareMap.dcMotor.get("Arm");
        right = hardwareMap.servo.get("right");
        left = hardwareMap.servo.get("left");
        FR.setDirection(DcMotor.Direction.REVERSE);
        Grabber.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);
        left.setDirection(Servo.Direction.REVERSE);

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
           Arm.setPower(gamepad2.left_stick_y/3);
           Grabber.setPower(gamepad2.right_stick_y);
           if(gamepad2.x){
               right.setPosition(0);
               left.setPosition(0);
           }else{
               right.setPosition(1.0);
               left.setPosition(1.0);
           }

        }


    }
}


