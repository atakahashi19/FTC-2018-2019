package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.teamcode.AutoCrater.actions.*;

@Autonomous(name = "AUTOFORDEPOTGUESSGOLD", group = "Concept")

public class AutoCrater extends LinearOpMode{
    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;
    private DcMotor Grabber;
    private DcMotor Grabber2;
    private Servo r;
    private Servo l;
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;

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
    ArrayList<Integer> minerals = new ArrayList();

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

        int position = 0;

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        r.setPosition(0);
        l.setPosition(0);
        waitForStart();
        actions acts = LATCH;
        while (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            switch(acts){
                case LATCH:
                    Grabber.setPower(-0.8);
                    Grabber2.setPower(-0.8 * 0.8);
                    Thread.sleep(6000);
                    Grabber.setPower(0);
                    Grabber2.setPower(0);
                    paralellLeft(0.3);
                    Thread.sleep(600);//was 1000

                    stopAllMotors();
                    Grabber.setPower(1.0);
                    Grabber2.setPower(1.0 * 0.8);
                    Thread.sleep(2000);
                    Grabber.setPower(0);
                    Grabber2.setPower(0);

                    paralellRight(0.3);
                    Thread.sleep(600);//was 1000
                    stopAllMotors();
                    forward(0.3);
                    Thread.sleep(700);
                    stopAllMotors();
                    paralellRight(0.3);
                    Thread.sleep(2000);
                    acts = DETECT;
                    break;

                case DETECT:
                    int times = 1;
                    while(minerals.get(minerals.size()-1)!=2&&times<3) {
                        telemetry.addData("Searching", times);
                        telemetry.update();
                        paralellLeft(0.3);//this needs to be changed sometimes.
                        Thread.sleep(1500);
                        stopAllMotors();
                        minerals.add(lookForward());
                        times++;
                    }
                    if(minerals.size()==1){
                        right = true;
                        left = false;
                        center = false;
                        telemetry.addData("Detected", "it is at the right");
                        telemetry.update();
                    }else if(minerals.size()==2){
                        center = true;
                        left = false;
                        right = false;
                        telemetry.addData("Detected", "it is at the center");
                        telemetry.update();
                    }else{
                        left = true;
                        right = false;
                        center = false;
                        telemetry.addData("Detected", "it is at the left");
                        telemetry.update();
                    }
                    acts = KNOCK;
                    break;

                case KNOCK:
                    alignAndKnockOffGold();
                    acts = DRIVE;
                    break;

                case DRIVE:
                    if(center){
                        forward(1000);
                        stopAllMotors();
                        r.setPosition(1.0);
                        l.setPosition(1.0);
                        backP(0.3);
                        Thread.sleep(1000);
                        stopAllMotors();
                        turnRight(0.3);
                        Thread.sleep(500);
                        stopAllMotors();
                        paralellLeft(1.0);
                        Thread.sleep(2000);
                        stopAllMotors();
                        backP(0.8);
                        Thread.sleep(3000);
                        stopAllMotors();
                    }else if(left){
                        turnLeft(0.2);
                        Thread.sleep(800);
                        stopAllMotors();
                        paralellLeft(1.0);
                        Thread.sleep(2000);
                        stopAllMotors();
                        forward(0.8);
                        Thread.sleep(3000);
                        stopAllMotors();
                        backP(0.3);
                        Thread.sleep(800);
                        stopAllMotors();
                        r.setPosition(1.0);
                        l.setPosition(1.0);
                        stopAllMotors();
                        backP(0.3);
                        Thread.sleep(1000);
                        stopAllMotors();
                        backP(0.8);
                        Thread.sleep(3000);
                        stopAllMotors();
                    }else{
                        turnRight(0.2);
                        Thread.sleep(800);
                        stopAllMotors();
                        paralellRight(0.8);
                        Thread.sleep(2000);
                        stopAllMotors();
                        forward(0.8);
                        Thread.sleep(3000);
                        stopAllMotors();
                        backP(0.3);
                        Thread.sleep(800);
                        stopAllMotors();
                        r.setPosition(1.0);
                        l.setPosition(1.0);
                        stopAllMotors();
                        backP(0.3);
                        Thread.sleep(1000);
                        stopAllMotors();
                        backP(0.8);
                        Thread.sleep(3000);
                        stopAllMotors();
                    }
                    acts = STOP;
                    break;

                case STOP:
                    stopAllMotors();
                    break;

            }


        }
    }


    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
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
    private void backP(double power) {
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


    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    public void initVuforiaThing() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CAMERA_CHOICE;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
    }

    public int lookForward(){
        int col = 0;
        initVuforiaThing();

        if (this.tfod != null) {
            tfod.activate();
        }
        if (tfod != null) {
            List<Recognition> recog = tfod.getUpdatedRecognitions();
            if (recog != null) {
                for (Recognition recognition : recog) {
                    if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            col = 2;
                    }
                    else{
                        col = 1;
                    }
                }

            }
        }
          return col;
    }

    public void alignAndKnockOffGold() throws InterruptedException {
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
                stopAllMotors();
            if(seekGold<100) {
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
            }else if(seekGold>100){
                while (seekGold > 100) {
                    paralellRight(0.3);
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
            }
                stopAllMotors();
                forward(0.7);
                Thread.sleep(1500);
                stopAllMotors();
                backP(0.7);
                Thread.sleep(1500);
                stopAllMotors();
        }
    }
    enum actions {

        LATCH, DETECT, KNOCK, DRIVE, STOP

    }

}
