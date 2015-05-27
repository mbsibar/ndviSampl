package pilot.obss.com.android.ioio;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import pilot.obss.com.autopilot.util.SingletonCollection;
import pilot.obss.com.autopilot.util.types.CraftInformation;

/**
 * Created by burak on 19.4.2015.
 */
public class QuadCopterLooper extends BaseIOIOLooper {
    private PwmOutput motor1;
    private PwmOutput motor4;
    private PwmOutput motor3;
    private PwmOutput motor2;
    private PulseInput modeSwitch;
    private int nominalValue = 1000;
    private DigitalOutput led_;
    private int hertz = 350;

    private float motor1Value = 1000;
    private float motor2Value = 1200;
    private float motor3Value = 1200;
    private float motor4Value = 1200;

    @Override
    public void setup() throws ConnectionLostException {
        motor1 = ioio_.openPwmOutput(6, hertz);
        motor2 = ioio_.openPwmOutput(7, hertz);
        motor3 = ioio_.openPwmOutput(3, hertz);
        motor4 = ioio_.openPwmOutput(2, hertz);
        modeSwitch = ioio_.openPulseInput(new DigitalInput.Spec(4),
                PulseInput.ClockRate.RATE_16MHz, PulseInput.PulseMode.POSITIVE, true);
        motor1.setPulseWidth(nominalValue);
        motor4.setPulseWidth(nominalValue);
        motor3.setPulseWidth(nominalValue);
        motor2.setPulseWidth(nominalValue);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        led_ = ioio_.openDigitalOutput(0, true);
    }

    @Override
    public void loop() throws ConnectionLostException, InterruptedException {
        try {
            if(SingletonCollection.getStarted()) {
                motor1.setPulseWidth(CraftInformation.getInstance().getMotor1());
                motor4.setPulseWidth(CraftInformation.getInstance().getMotor4());
                motor3.setPulseWidth(CraftInformation.getInstance().getMotor3());
                motor2.setPulseWidth(CraftInformation.getInstance().getMotor2());

                // float duration = modeSwitch.getDuration();
           /*  SingletonCollection.getUserInterface().writeToTextView(new Float(CraftInformation.getInstance().getMotor1()).toString());*/
                SingletonCollection.getUserInterface().writeToTextViewP(new Float(CraftInformation.getInstance().getMotor2()).toString());

                SingletonCollection.getUserInterface().writeToTextViewI(new Float(CraftInformation.getInstance().getMotor3()).toString());
                SingletonCollection.getUserInterface().writeToTextViewD(new Float(CraftInformation.getInstance().getMotor4()).toString());
            }
        } catch (Exception e) {
            SingletonCollection.getUserInterface().writeToTextView(e.getMessage());
        }
    }


    @Override
    public void disconnected() {
    }


}
