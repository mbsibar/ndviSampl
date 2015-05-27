package pilot.obss.com.android.ioio;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import pilot.obss.com.android.util.CraftTypeData;
import pilot.obss.com.autopilot.util.Converter;
import pilot.obss.com.autopilot.util.SingletonCollection;
import pilot.obss.com.autopilot.util.constants.Settings;
import pilot.obss.com.autopilot.util.types.CraftInformation;

/**
 * Created by burak on 19.4.2015.
 */
public class DeltaWingLooper extends BaseIOIOLooper {
    private PwmOutput servoLeft;
    private PwmOutput servoRight;
    private PulseInput modeSwitch;
    private PulseInput aileronStick;
    private PulseInput elevatorStick;
    private PulseInput gainP;
    private PulseInput gainI;
    private int hertz = 50;
    private float centerValue = 1500;

    @Override
    public void setup() throws ConnectionLostException {
        modeSwitch = ioio_.openPulseInput(new DigitalInput.Spec(4),
                PulseInput.ClockRate.RATE_16MHz, PulseInput.PulseMode.POSITIVE, true);
        aileronStick = ioio_.openPulseInput(new DigitalInput.Spec(3),
                PulseInput.ClockRate.RATE_16MHz, PulseInput.PulseMode.POSITIVE, true);
        elevatorStick = ioio_.openPulseInput(new DigitalInput.Spec(2),
                PulseInput.ClockRate.RATE_16MHz, PulseInput.PulseMode.POSITIVE, true);
        gainP = ioio_.openPulseInput(new DigitalInput.Spec(9),PulseInput.ClockRate.RATE_250KHz,PulseInput.PulseMode.POSITIVE,false);
        gainI = ioio_.openPulseInput(new DigitalInput.Spec(10),PulseInput.ClockRate.RATE_250KHz,PulseInput.PulseMode.POSITIVE,false);

        servoLeft = ioio_.openPwmOutput(6, hertz);
        servoRight = ioio_.openPwmOutput(7, hertz);
        servoLeft.setPulseWidth(centerValue);
        servoRight.setPulseWidth(centerValue);
    }

    @Override
    public void loop() throws ConnectionLostException, InterruptedException {
        try {
            float modeValue = Converter.pwmToDegreeConverter(modeSwitch.getDuration() * 1000000, 1200, 1800, 0, 180);
            float left = 90;
            float right = -left;
            float pitch = 0;
            if (SingletonCollection.getStarted() && modeValue < 90) {
                left = (0-CraftInformation.getInstance().getAileron()) + Converter.pwmToDegreeConverter(aileronStick.getDuration() * 1000000, 1000, 2000, 0, 180) -90;
                right = -left;
                pitch = 90 + CraftInformation.getInstance().getPitch() - Converter.pwmToDegreeConverter(elevatorStick.getDuration() * 1000000, 1000, 2000, 0, 180);
                CraftTypeData.getCraftType(Settings.craftType).getAutopilot().actionProcess.setRollDegree(Converter.pwmToDegreeConverter(aileronStick.getDuration() * 1000000, 1000, 2000, 0, 180));
                CraftTypeData.getCraftType(Settings.craftType).getAutopilot().actionProcess.setPitchDegree(Converter.pwmToDegreeConverter(elevatorStick.getDuration() * 1000000, 1000, 2000, 0, 180));


                /*left = (280-CraftInformation.getInstance().getAileron()) + Converter.pwmToDegreeConverter(aileronStick.getDuration() * 1000000, 1000, 2000, 0, 180) -90;
                right = -left;
                pitch = 90 + CraftInformation.getInstance().getPitch() - Converter.pwmToDegreeConverter(elevatorStick.getDuration() * 1000000, 1000, 2000, 0, 180);
                SingletonCollection.getUserInterface().writeToTextViewI(new Float(CraftInformation.getInstance().getAileron()).toString());
                SingletonCollection.getUserInterface().writeToTextViewD(new Float(280-CraftInformation.getInstance().getAileron()).toString());
                Settings.craftType.getAutoPilot().actionProcess.setRollDegree(Converter.pwmToDegreeConverter(aileronStick.getDuration() * 1000000, 1000, 2000, 0, 180));
                Settings.craftType.getAutoPilot().actionProcess.setPitchDegree(Converter.pwmToDegreeConverter(elevatorStick.getDuration() * 1000000, 1000, 2000, 0, 180));*/
            } else {
                left = 90 - Converter.pwmToDegreeConverter(aileronStick.getDuration() * 1000000, 1000, 2000, 0, 180);
                right = -left;
                pitch = 90 - Converter.pwmToDegreeConverter(elevatorStick.getDuration() * 1000000, 1000, 2000, 0, 180);
            }
            left = 90 - left - pitch;
            right = 90 + right + pitch;
            left = new Float(Converter.degreeToPwmConverter(left, 0, 180, 1000, 2000));
            right = new Float(Converter.degreeToPwmConverter(right, 0, 180, 1000, 2000));
            servoLeft.setPulseWidth(left);
            servoRight.setPulseWidth(right);


            Float pGain = 0.01f + Converter.pwmToDegreeConverter(gainP.getDuration() * 1000000, 1000, 2000, -0.01f, 0.2f);
            Float iGain = 0.01f + Converter.pwmToDegreeConverter(gainI.getDuration() * 1000000, 1000, 2000, -0.01f, 0.05f);

            SingletonCollection.getPIDObject().setP(pGain);
            SingletonCollection.getPIDObject().setI(iGain);
            SingletonCollection.getUserInterface().writeToTextViewP(pGain + " " + iGain);
        } catch (Exception e) {
            SingletonCollection.getUserInterface().writeToTextViewD(e.getMessage());
        }
    }


    @Override
    public void disconnected() {

    }

}
