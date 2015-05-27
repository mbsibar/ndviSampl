package pilot.obss.com.android.ioio;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import pilot.obss.com.autopilot.util.Converter;
import pilot.obss.com.autopilot.util.SingletonCollection;
import pilot.obss.com.autopilot.util.types.CraftInformation;

/**
 * Created by burak on 19.4.2015.
 */
public class TrainerPlaneLooper extends BaseIOIOLooper {
    private PwmOutput servoAileron;
    //private PwmOutput servoElevator;
    private PulseInput modeSwitch;
    private PulseInput aileronStick;
    //private PulseInput elevatorStick;
    private PulseInput gainP;
    private PulseInput gainI;
    private int hertz = 50;
    private float centerValue = 1500;

    @Override
    public void setup() throws ConnectionLostException {
        SingletonCollection.getUserInterface().writeToTextView("0");
        servoAileron = ioio_.openPwmOutput(6, hertz);
        SingletonCollection.getUserInterface().writeToTextView("1");
       // servoElevator = ioio_.openPwmOutput(7, hertz);
        modeSwitch = ioio_.openPulseInput(new DigitalInput.Spec(4),
                PulseInput.ClockRate.RATE_16MHz, PulseInput.PulseMode.POSITIVE, true);
        SingletonCollection.getUserInterface().writeToTextView("2");
        aileronStick = ioio_.openPulseInput(new DigitalInput.Spec(3),
                PulseInput.ClockRate.RATE_16MHz, PulseInput.PulseMode.POSITIVE, true);
        SingletonCollection.getUserInterface().writeToTextView("3");
        /*elevatorStick = ioio_.openPulseInput(new DigitalInput.Spec(2),
                PulseInput.ClockRate.RATE_16MHz, PulseInput.PulseMode.POSITIVE, true);*/
        gainP = ioio_.openPulseInput(new DigitalInput.Spec(9),PulseInput.ClockRate.RATE_250KHz,PulseInput.PulseMode.POSITIVE,false);
        SingletonCollection.getUserInterface().writeToTextView("4");
        gainI = ioio_.openPulseInput(new DigitalInput.Spec(10),PulseInput.ClockRate.RATE_250KHz,PulseInput.PulseMode.POSITIVE,false);
        SingletonCollection.getUserInterface().writeToTextView("5");
        servoAileron.setPulseWidth(centerValue);
        //servoElevator.setPulseWidth(centerValue);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void loop() throws ConnectionLostException, InterruptedException {
        try {
            float modeValue = Converter.pwmToDegreeConverter(modeSwitch.getDuration() * 1000000, 1200, 1800, 0, 180);
            if (SingletonCollection.getStarted() && modeValue < 90) {
                float left = CraftInformation.getInstance().getAileron();
                float aileron = Converter.pwmToDegreeConverter(aileronStick.getDuration() * 1000000, 1200, 1800, 0, 180);
                left = new Float(Converter.degreeToPwmConverter(left + aileron, 0, 180, 1100, 1900));
                if (!Float.isNaN(left)) {
                    servoAileron.setPulseWidth(left);
                }
            } else {
                servoAileron.setPulseWidth(aileronStick.getDuration() * 1000000);
            }
            Float pGain = 0.01f + Converter.pwmToDegreeConverter(gainP.getDuration() * 1000000, 1000, 2000, -0.01f, 0.2f);
            Float iGain = 0.01f + Converter.pwmToDegreeConverter(gainI.getDuration() * 1000000, 1000, 2000, -0.01f, 0.05f);

            SingletonCollection.getPIDObject().setP(pGain);
            SingletonCollection.getPIDObject().setI(iGain);
            SingletonCollection.getUserInterface().writeToTextViewP(pGain + " " + iGain);
        } catch (Exception e) {
            SingletonCollection.getUserInterface().writeToTextView(e.getMessage());
        }
    }


    @Override
    public void disconnected() {
    }


    /*

    float left = 90 - CraftInformation.getInstance().getAileron();
                float right = 90 + left;
                float pitch = 90 - CraftInformation.getInstance().getElevator();
                left = 90 + left + pitch;
                right = 90 + right + pitch;
                left = new Float(Converter.degreeToPwmConverter(left, 0, 180, 1000, 2000));
                right = new Float(Converter.degreeToPwmConverter(right, 0, 180, 1000, 2000));
                if (!Float.isNaN(left) && !Float.isNaN(right)) {
                    servoAileron.setPulseWidth(left);
                    servoElevator.setPulseWidth(right);
                    Settings.craftType.getAutoPilot().actionProcess.setRollDegree(Converter.pwmToDegreeConverter(aileronStick.getDuration() * 1000000, 1000, 2000, 0, 180));
                    Settings.craftType.getAutoPilot().actionProcess.setPitchDegree(Converter.pwmToDegreeConverter(elevatorStick.getDuration() * 1000000, 1000, 2000, 0, 180));
                    SingletonCollection.getUserInterface().writeToTextView(Settings.craftType.getAutoPilot().actionProcess.getRollDegree().toString());
                    SingletonCollection.getUserInterface().writeToTextViewP(Settings.craftType.getAutoPilot().actionProcess.getPitchDegree().toString());
                    SingletonCollection.getUserInterface().writeToTextViewI(new Float(CraftInformation.getInstance().getElevator()).toString());
                    SingletonCollection.getUserInterface().writeToTextViewD(new Float(left).toString());

}


     */


}
