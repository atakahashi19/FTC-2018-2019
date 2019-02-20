/* Copyright (c) 2018 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

/**
 * This 2018-2019 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the gold and silver minerals.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous(name = "AutoC", group = "Concept")

public class AutoCam3 extends LinearOpMode {
    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;
    private DcMotor Grabber;
    private DcMotor Grabber2;
    private Servo r;
    private Servo l;

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";


    private static final String VUFORIA_KEY = "AVwxC87/////AAABmV7IvMsh60iBrcr7sOP++yZBOE5wwvoSlCFqs41q6Jzm/R8YCByQcCTOh0LSBp/C+qni+5mKYYGAkWYCFSVnHPVEzlPSx+yDWktfdRKtuAGot5T2HChnVsuDAEN5UA1RgHoda7WEtiVY+UFc/08j1NjZ44xjTAUdJbWzj8Lnz5VH4oo0Vx//7bHTjJQW+Afc7mw/nddEvnpBIsff20+qOSImvMbFgnxJ3rEff4ATGaY3SI/lSZgjPRI3vgBjpHOaUx7aDsSDl08+zF16wNiDlQrvG6eDYGv69iM4SQwFVoVb34jNnni4aEioZij6FBBjjw1IOmY0pCuGnix4FogzvGRlCvYVRPa+LenEFooNRAK1";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private boolean center = false;
    private boolean left = false;
    private boolean right = false;
    private boolean doMove = false;
    private boolean doLoop = true;
    private int seekGold = -1;


    @Override
    public void runOpMode() throws InterruptedException {
        FL = hardwareMap.dcMotor.get("frontLeft");
        BL = hardwareMap.dcMotor.get("backLeft");
        FR = hardwareMap.dcMotor.get("frontRight");
        BR = hardwareMap.dcMotor.get("backRight");
        Grabber = hardwareMap.dcMotor.get("Grabber");
        Grabber2 = hardwareMap.dcMotor.get("Grabber2");
        Grabber.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);
        r = hardwareMap.servo.get("right");
        l = hardwareMap.servo.get("left");
        l.setDirection(Servo.Direction.REVERSE);

        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        r.setPosition(0);
        l.setPosition(0);
        waitForStart();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            doMove = true;
            left = true;
            Grabber.setPower(-0.8);
            Grabber2.setPower(-0.8 * 0.8);
            Thread.sleep(7000);
            Grabber.setPower(0);
            Grabber2.setPower(0);
            paralellLeft(0.3);
            Thread.sleep(1000);

            stopAllMotors();
            Grabber.setPower(1.0);
            Grabber2.setPower(1.0 * 0.8);
            Thread.sleep(2000);
            Grabber.setPower(0);
            Grabber2.setPower(0);

            paralellRight(0.3);
            Thread.sleep(1000);

//            FL.setPower(0.4);
//            BL.setPower(0.4);
//            FR.setPower(-0.4);
//            BR.setPower(-0.4);
//            Thread.sleep(900);
            stopAllMotors();
            forward(0.3);
            Thread.sleep(700);
            stopAllMotors();
            paralellRight(0.3);
            Thread.sleep(2000);

            while (opModeIsActive()) {
                if (tfod != null) {
                    tfod.activate();
                }
                if (tfod != null) {
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

                    if (updatedRecognitions != null) {


                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                seekGold = (int) recognition.getLeft();
                            }
                        }

                    }

                    if (doMove) {
                        if (left) {
                            stopAllMotors();

                            while (seekGold < 100) {
                                paralellLeft(0.3);
                                if (tfod != null) {
                                    tfod.activate();
                                }
                                if (tfod != null) {
                                    List<Recognition> recog = tfod.getUpdatedRecognitions();

                                    if (recog != null) {


                                        for (Recognition recognition : recog) {
                                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                                seekGold = (int) recognition.getLeft();
                                            }
                                        }

                                    }
                                }
                            }
                            stopAllMotors();
                            forward(2000);
stopAllMotors();
                            r.setPosition(1.0);
                            l.setPosition(1.0);
                            back(0.3);
                            Thread.sleep(1000);
                            stopAllMotors();
                        }
                        doMove = false;
                    }
                    telemetry.update();

                }
            }
        }

        if (tfod != null) {
            tfod.shutdown();
        }


    }


    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    private void forward(long time) throws InterruptedException {
        FL.setPower(-1.0);
        BL.setPower(-1.0);
        FR.setPower(-1.0);
        BR.setPower(-1.0);
        Thread.sleep(time);
    }
    private void forward(double power) {
        FL.setPower(-power);
        BL.setPower(-power);
        FR.setPower(-power);
        BR.setPower(-power);
    }
    private void back(long time) throws InterruptedException {
        FL.setPower(1.0);
        BL.setPower(1.0);
        FR.setPower(1.0);
        BR.setPower(1.0);
        Thread.sleep(time);
    }
    private void back(double power) {
        FL.setPower(power);
        BL.setPower(power);
        FR.setPower(power);
        BR.setPower(power);
    }
    private void turnRight(long time) throws InterruptedException {
        FL.setPower(1.0);
        BL.setPower(1.0);
        FR.setPower(-1.0);
        BR.setPower(-1.0);
        Thread.sleep(time);
    }
    private void turnLeft(long time) throws InterruptedException {
        FL.setPower(-1.0);
        BL.setPower(-1.0);
        FR.setPower(1.0);
        BR.setPower(1.0);
        Thread.sleep(time);
    }
    private void turnRight(double power) throws InterruptedException {
        FL.setPower(power);
        BL.setPower(power);
        FR.setPower(-power);
        BR.setPower(-power);

    }
    private void turnLeft(double power) throws InterruptedException {
        FL.setPower(-power);
        BL.setPower(-power);
        FR.setPower(power);
        BR.setPower(power);

    }

    private void stopAllMotors(){
        FL.setPower(0);
        BL.setPower(0);
        FR.setPower(0);
        BR.setPower(0);
    }

    private void paralellRight(double power) throws InterruptedException{
        FL.setPower(-power);
        BL.setPower(power);
        FR.setPower(power);
        BR.setPower(-power);
    }
    private void paralellLeft(double power) throws InterruptedException{
        FL.setPower(power);
        BL.setPower(-power);
        FR.setPower(-power);
        BR.setPower(power);
    }
}
