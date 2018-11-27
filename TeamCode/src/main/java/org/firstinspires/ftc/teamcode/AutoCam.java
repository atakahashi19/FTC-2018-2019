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
@Autonomous(name = "AutoTest", group = "Concept")

public class AutoCam extends LinearOpMode {
    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;
    private DcMotor Grabber;
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

    @Override
    public void runOpMode() throws InterruptedException {
        FL = hardwareMap.dcMotor.get("frontLeft");
        BL = hardwareMap.dcMotor.get("backLeft");
        FR = hardwareMap.dcMotor.get("frontRight");
        BR = hardwareMap.dcMotor.get("backRight");
        Grabber = hardwareMap.dcMotor.get("Grabber");
        Grabber.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            while(doLoop) {
                if (tfod != null) {
                    tfod.activate();
                }
                if (tfod != null) {
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 3) {
                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            int silverMineral2X = -1;
                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = (int) recognition.getLeft();

                                } else {
                                    silverMineral2X = (int) recognition.getLeft();
                                }
                            }
                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Left");
                                    left = true;
                                    doMove = true;
                                    doLoop = false;
                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                    telemetry.addData("Gold Mineral Position", "Right");
                                    right = true;
                                    doMove = true;
                                    doLoop = false;
                                } else {
                                    telemetry.addData("Gold Mineral Position", "Center");
                                    center = true;
                                    doMove = true;
                                    doLoop = false;
                                }
                            }
                            telemetry.addData("Gold x", goldMineralX);
                        }
                        telemetry.update();
                    }
                }
            }
            Grabber.setPower(-0.4);
            Thread.sleep(4000);
            Grabber.setPower(0);
            FL.setPower(-0.4);
            BL.setPower(-0.4);
            FR.setPower(0.4);
            BR.setPower(0.4);
            Thread.sleep(900);
            stopAllMotors();
            Grabber.setPower(0.4);
            Thread.sleep(4000);
            Grabber.setPower(0);
            FL.setPower(0.4);
            BL.setPower(0.4);
            FR.setPower(-0.4);
            BR.setPower(-0.4);
            Thread.sleep(900);
            stopAllMotors();

            while (opModeIsActive()) {




                if(doMove){
                    if(left){
                       turnRight(200);
                       stopAllMotors();
                       forward(1500);
                       stopAllMotors();
                    }else if(right){
                        turnLeft(200);
                        stopAllMotors();
                        forward(1500);
                        stopAllMotors();
                    }else if(center){
                        forward(1500);
                        stopAllMotors();
                    }
                    doMove = false;
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

    private void stopAllMotors(){
        FL.setPower(0);
        BL.setPower(0);
        FR.setPower(0);
        BR.setPower(0);
    }
}
