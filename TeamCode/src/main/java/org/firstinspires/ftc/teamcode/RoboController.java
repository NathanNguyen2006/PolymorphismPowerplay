package org.firstinspires.ftc.teamcode;




import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorController;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;

public class RoboController {
    public static boolean almostEqual(double a, double b, double eps){
        return Math.abs(a-b)<eps;
    }
    private ElapsedTime runtime = new ElapsedTime();

    //Hardware

    //Wheels
    public DcMotorEx FRW;
    public DcMotorEx FLW;
    public DcMotorEx BRW;
    public DcMotorEx BLW;

    //Arm
    private DcMotor ArmBase;
    private DcMotor ArmBase2;
    private DcMotor ArmTop;
    private Servo Hand;
    public Servo ClawR;
    //private Servo ClawR;
    private Pid ArmTopPid;
    private Pid ArmBasePid;
    private boolean handClosed = false;
    private boolean previousRightBumper = false;

    //directions
    private enum Direction {
        North,
        South,
        East,
        West
    }
    public Direction direction = Direction.North;

    public RoboController(HardwareMap hardwareMap){
        //Wheels
        FRW = hardwareMap.get(DcMotorEx.class, "FRW");
        FLW = hardwareMap.get(DcMotorEx.class, "FLW");
        BRW = hardwareMap.get(DcMotorEx.class, "BRW");
        BLW = hardwareMap.get(DcMotorEx.class, "BLW");

        // Arm = hardwareMap.get(CRServo.class, "Arm");
        // Set direction of motors
        FLW.setDirection(DcMotor.Direction.REVERSE);
        FRW.setDirection(DcMotor.Direction.FORWARD);
        BLW.setDirection(DcMotor.Direction.REVERSE);
        BRW.setDirection(DcMotor.Direction.FORWARD);

        ArmTop = hardwareMap.get(DcMotor.class,"ArmTop");
        ArmBase = hardwareMap.get(DcMotor.class,"ArmBase");
        ArmBase2 = hardwareMap.get(DcMotor.class,"ArmBase2");
        ClawR = hardwareMap.get(Servo.class,"ClawR");
        //ClawR = hardwareMap.get(Servo.class,"ClawR");
        Hand = hardwareMap.get(Servo.class,"Hand");
        ArmBase.setMode(DcMotor.RunMode.RESET_ENCODERS);
        ArmTop.setMode(DcMotor.RunMode.RESET_ENCODERS);
        ArmBase2.setMode(DcMotor.RunMode.RESET_ENCODERS);
        ArmBase.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        ArmTop.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        ArmBase2.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        ArmTopPid = new Pid( 0.3, 0.3, 0.3, 1, -1, 1);
        ArmBasePid = new Pid(0.1, 0.1, 0.15, 1, -1, 1);


    }


    public double strafePower = 0;
    public double drivePower = 0;
    public double turnPower = 0;
    //arm stuff
    private enum Position {
        Pickup,
        Carrying,
        High,
        Middle,
        Low
    }

    public Position position = Position.Middle;

    public double armBasePower;
    public double armTopPower;

    public void interpretMovepad(Gamepad movepad){

        FLW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        FRW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        BLW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        BRW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);

        if(Math.abs(movepad.right_stick_x) > .2){
            turnPower = movepad.right_stick_x*0.35;
        }
        else{
            turnPower = 0;
        }

        if(movepad.left_stick_x > 0.3){
            direction = Direction.East;
        }
        else if(movepad.left_stick_x < -0.3){
            direction = Direction.West;
        }
        else if(movepad.left_stick_y < -0.3){
            direction = Direction.North;
        }
        else if(movepad.left_stick_y > 0.3){
            direction = Direction.South;
        }



        //Direction
        if (movepad.dpad_up) drivePower = 0.5;
        else if (movepad.dpad_down) drivePower = -0.5;
        else{ drivePower = 0;}

        if (movepad.dpad_right) strafePower = 0.9;
        else if (movepad.dpad_left) strafePower = -0.9;
        else {
            strafePower = 0;
        }


        double tempStrafe = strafePower;
        switch (direction) {
            case North: break;
            case South:
                drivePower = -drivePower;
                strafePower = -strafePower;
                break;
            case East:
                tempStrafe = strafePower;
                strafePower = -drivePower;
                drivePower = tempStrafe;
                break;
            case West:
                tempStrafe = strafePower;
                strafePower = drivePower;
                drivePower = -tempStrafe;
                break;
        }

        //Turn overwrites strafe
        if(drivePower != 0){
            if(turnPower != 0){
                //Drive and Turn
                FRW.setPower(drivePower);
                FLW.setPower(turnPower);
                BRW.setPower(drivePower);
                BLW.setPower(turnPower);

            }else if(strafePower  != 0){
                //Drive and Strafe
                FRW.setPower(-strafePower);
                FLW.setPower(drivePower);
                BRW.setPower(drivePower);
                BLW.setPower(-strafePower);

            }else{
                //Just Drive
                FRW.setPower(drivePower);
                FLW.setPower(drivePower);
                BRW.setPower(drivePower);
                BLW.setPower(drivePower);
            }
        }else if(turnPower != 0){
            //Just Turn
            FRW.setPower(-turnPower);
            FLW.setPower(turnPower);
            BRW.setPower(-turnPower);
            BLW.setPower(turnPower);

        }else if(strafePower != 0){
            //Just Strafe
            FRW.setPower(-strafePower); //-strafe
            FLW.setPower(strafePower);
            BRW.setPower(strafePower);
            BLW.setPower(-strafePower); //-strafe
        }
        else{
            FRW.setPower(-0);
            FLW.setPower(0);
            BRW.setPower(0);
            BLW.setPower(-0);
        }
    }
    public int xyz = 0;
    //Not Implemented
    public void interpretArmpad(Gamepad armpad){
        long dt = runtime.nanoseconds();
        runtime.reset();

        //ArmBase.setDirection(DcMotor.Direction.REVERSE);

        //armpower


        double baseConstFactor = -.99;
        if (Math.abs(ArmBase2.getCurrentPosition())> 5300  || Math.abs(ArmBase.getCurrentPosition())> 5300) {
            ArmBase.setPower(-0.3);
            ArmBase2.setPower(-0.3);
        }
        else if (ArmBase2.getCurrentPosition()< -75  || ArmBase.getCurrentPosition()< -75){
            ArmBase.setPower(0.3);
            ArmBase2.setPower(0.3);
        }
        else if(Math.abs(armpad.left_stick_y) > 0.3){
            ArmBase2.setPower(armpad.left_stick_y*baseConstFactor);
            ArmBase.setPower(armpad.left_stick_y*baseConstFactor);
        }
        else {
            ArmBase.setPower(0);
            ArmBase2.setPower(0);
        }
        // else if (ArmBase.getPower() != 0.001 && ArmBase2.getPower() != 0.001) {
        //     ArmBase.setPower(0.001);
        //     ArmBase2.setPower(0.001);
        // }
        double topConstFactor = .26;
        if(Math.abs(armpad.right_stick_y) > 0.3){
            ArmTop.setPower(armpad.right_stick_y*topConstFactor);
        }
        else {
            ArmTop.setPower(0);
        }
        // else if (ArmBase.getPower() != 0.001) {
        //     ArmTop.setPower(0.001);
        // }


         //Hand
        if (!previousRightBumper && armpad.right_bumper) toggleHand();
        previousRightBumper = armpad.right_bumper;
        //ClawR.setPosition(1);
        Hand.setPosition(0.52+ (ArmBase.getCurrentPosition()/640.0/5.5 * 0.22));
        //Hand.setPosition(1);

        //if (armpad.y ){
        //    position = Position.High;
        //}
        //else if (armpad.a){ position = Position.Low;}
        // else if (armpad.dpad_down){ position = Position.Pickup;}
        //else if (armpad.dpad_up) {position = Position.Carrying;}
        //else if(armpad.x){position = Position.Middle;}

        //645.6 ticks max
        //30/48

        /*
            Arm is 0- 550
            Gripper is 1-1/3
        */

        switch (position) {
            case Pickup:
                /*
                ArmTop.setTargetPosition(1);
                ArmTop.setPower(1);
               ArmTop.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                while(ArmTop.isBusy()){
                }
                ArmTop.setPower(0);

                ArmBase.setTargetPosition(1);
                ArmBase.setPower(1);
                ArmBase.setMode(DcMotor.RunMode.RUN_TO_POSITION);
               while(ArmBase.isBusy()){
                }
               ArmBase.setPower(0);
               */

                break;

            case Carrying:
                ArmTop.setTargetPosition((int)(646.0/4 * 30/48));
                ArmTop.setPower(1);
                ArmTop.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                while(ArmTop.isBusy()){
                }
                ArmTop.setPower(0);
                ArmBase.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);

                ArmBase.setTargetPosition(200);
                ArmBase.setPower(1);
                //ArmBase2.setPower(1);
                ArmBase.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                while(ArmBase.isBusy()){
                }
                //ArmBase2.setPower(0);
                ArmBase.setPower(0);


                ArmTop.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
                //ArmBase2.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);

                break;

            case Low:
                ArmTop.setTargetPosition((int)(646.0/4 * 3/2));
                ArmTop.setPower(1);
                ArmTop.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                while(ArmTop.isBusy()){
                }
                ArmTop.setPower(0);
                ArmTop.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);

                // ArmBase2.setTargetPosition((int)(646.0/4 * 3/2));
                // ArmBase2.setPower(1);
                //ArmBase2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                //while(ArmBase2.isBusy()){
                ArmBase.setPower(-1);
                //}
                ArmBase.setPower(0);
                //ArmBase2.setPower(0);
                //ArmBase2.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);

                break;
            case High:
                if (!almostEqual(ArmTop.getCurrentPosition()+1, 646.0*30/48/2, 10)) {
                    ArmTop.setPower(ArmTop.getPower() + ArmTopPid.Update(dt, ArmTop.getCurrentPosition(), 646.0*30/48/2));
                } else ArmTop.setPower(0);

                break;

            case Middle:
                break;

        }
    }

    public Position getPosition(){
        return position;
    }
    public int getThePosition(){
        return ArmBase.getCurrentPosition();
    }
    public int getThePosition2(){
        return ArmTop.getCurrentPosition();
    }
    public double getArmBase2Power(){
        return ArmBase.getPower();
    }
    public double getArmtTopPower(){
        return ArmTop.getPower();
    }
    public double getHandPos(){
        return Hand.getPosition();
    }
    public double getClawPos(){
        return ClawR.getPosition();
    }


    public void setHand(boolean closed) {
        handClosed = closed;
        if (handClosed) {
            ClawR.setPosition(0.15);
        } else {
            ClawR.setPosition(0.4);
        }
    }

    public void toggleHand() {
        setHand(!handClosed);
    }
}