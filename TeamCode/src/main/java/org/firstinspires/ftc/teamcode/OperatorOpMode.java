package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class OperatorOpMode extends LinearOpMode{
    //Personal Class
    private RoboController roboController;

    //private Gyroscope imu;
    //private DcMotor motorTest;
    //private DigitalChannel digitalTouch;
    // private DistanceSensor sensorColorRange;

    //private Servo servoTest;

    private Gamepad movePad;
    private Gamepad armPad;

    @Override
    public void runOpMode() {
        //imu = hardwareMap.get(Gyroscope.class, "imu");
        //digitalTouch = hardwareMap.get(DigitalChannel.class, "digitalTouch");
        //sensorColorRange = hardwareMap.get(DistanceSensor.class, "sensorColorRange");

        //Robo Controller
        roboController = new RoboController(this);

        //Choose which gamepad is which
        movePad = gamepad1;
        armPad = gamepad2;


        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()){
            //telemetry.addData("Status", "Playing");
            //Transferring movement inputs
            roboController.interpretMovepad(movePad);
            roboController.interpretArmpad(armPad);
            //roboController.interpretArmpad(armPad);
            // roboController.testWheels(movePad);
            telemetry.addData("movepad.right_stick_x:", gamepad2.right_stick_y);
            telemetry.addData("movepad.left_stick_y", gamepad2.left_stick_y);
            telemetry.addData("Status", "Running");
            telemetry.addData("Direction", roboController.direction);
            telemetry.addData("Drive", roboController.drivePower);
            telemetry.addData("Strafe", roboController.strafePower);
            telemetry.addData("Turn", roboController.turnPower);
            //telemetry.addData("ArmBase1", gamepad2.ArmBase.getCurrentPosition());
            //telemetry.addData("ArmBase2", gamepad2.ArmBase2.getCurrentPosition());
            //telemetry.addData("ArmTop", gamepad2.ArmTop.getCurrentPosition());
            //telemetry.addData("Drive", direction);
            telemetry.addData("Position", roboController.getPosition());
            telemetry.addData("Armbase2", roboController.getThePosition());
            telemetry.addData("Armtoppos", roboController.getThePosition2());
            telemetry.addData("Armtoppower", roboController.getArmtTopPower());
            telemetry.addData("armbase2power", roboController.getArmBase2Power());
            telemetry.addData("handpos", roboController.getHandPos());
            telemetry.addData("jlksdasdjlskl", roboController.xyz);
            telemetry.addData("jlksdadjlskl", roboController.getClawPos());
            telemetry.update();
        }
    }
}
