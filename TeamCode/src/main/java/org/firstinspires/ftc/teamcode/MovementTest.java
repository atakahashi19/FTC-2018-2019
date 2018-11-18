package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.usb.RobotArmingStateNotifier;


import java.net.NoRouteToHostException;

@TeleOp (name="Teleop_2", group = "Teleop")
public class MovementTest extends LinearOpMode

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
        BR.setDirection(DcMotor.Direction.REVERSE);
        Grabber.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad2.right_stick_x == 0) {
                FL.setPower(gamepad2.right_stick_y);
                FR.setPower(gamepad2.right_stick_y);
                BL.setPower(gamepad2.right_stick_y);
                BR.setPower(gamepad2.right_stick_y);
            }else {
                if (gamepad2.right_stick_x < 0) {
                    FL.setPower(-1.0);
                    FR.setPower(1.0);
                    BL.setPower(4.0/6.0);
                    BR.setPower(-4.0/6.0);
                } else {
                    FL.setPower(1.0);
                    FR.setPower(-1.0);
                    BL.setPower(-4.0/6.0);
                    BR.setPower(4.0/6.0);
                }
            }
             if(gamepad2.left_stick_x>0){
                FL.setPower(1.0);
                FR.setPower(-1.0);
                BL.setPower(4.0/6.0);
                BR.setPower(-4.0/6.0);
            }
             if(gamepad2.left_stick_x<0){
                FL.setPower(-1.0);
                FR.setPower(1.0);
                BL.setPower(-4.0/6.0);
                BR.setPower(4.0/6.0);
            }

            if(!gamepad2.right_stick_button&&!gamepad2.left_stick_button&&gamepad2.right_stick_x == 0&&gamepad2.right_stick_y == 0){
                FL.setPower(0);
                FR.setPower(0);

                BR.setPower(0);
                BL.setPower(0);
            }
            Arm.setPower(gamepad2.left_stick_y);
            Grabber.setPower(gamepad2.right_stick_y);
            if(gamepad2.x){
                right.setPosition(0.4);
                left.setPosition(0.4);
            }else{
                right.setPosition(1.0);
                left.setPosition(1.0);
            }

        }


    }
}


