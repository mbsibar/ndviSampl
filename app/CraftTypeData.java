package pilot.obss.com.android.util;

import ioio.lib.util.BaseIOIOLooper;
import pilot.obss.com.android.ioio.DeltaWingLooper;
import pilot.obss.com.android.ioio.QuadCopterLooper;
import pilot.obss.com.android.ioio.TrainerPlaneLooper;
import pilot.obss.com.android.sensors.AndroidDeltaWingSensor;
import pilot.obss.com.android.sensors.AndroidPlaneSensor;
import pilot.obss.com.android.sensors.AndroidQuadcopterSensor;
import pilot.obss.com.autopilot.brain.AutoPilot;
import pilot.obss.com.autopilot.sensor.PilotSensor;
import pilot.obss.com.autopilot.util.types.CraftTypes;

/**
 * Created by burak on 26.5.2015.
 */
public enum CraftTypeData {
    PLANE_DATA(CraftTypes.PLANE, new AndroidPlaneSensor(), new TrainerPlaneLooper()),
    DELTAWING_DATA(CraftTypes.DELTAWING, new AndroidDeltaWingSensor(), new DeltaWingLooper()),
    QUADCOPTER_X_DATA(CraftTypes.QUADCOPTER_X, new AndroidQuadcopterSensor(), new QuadCopterLooper()),
    FG_PLANE_DATA(CraftTypes.FG_PLANE, null, null);

    private CraftTypes types;
    private PilotSensor pilotSensor;
    private BaseIOIOLooper looper;

    CraftTypeData(CraftTypes types, PilotSensor pilotSensor, BaseIOIOLooper looper) {
        this.types = types;
        this.pilotSensor = pilotSensor;
        this.looper = looper;
    }

    public static CraftTypeData getCraftType(CraftTypes craftType) {
        if(craftType.equals(CraftTypes.DELTAWING))
            return DELTAWING_DATA;
        else if(craftType.equals(CraftTypes.FG_PLANE))
            return FG_PLANE_DATA;
        else if(craftType.equals(CraftTypes.QUADCOPTER_X))
            return QUADCOPTER_X_DATA;
        else if(craftType.equals(CraftTypes.PLANE))
            return PLANE_DATA;
        return DELTAWING_DATA;
    }

    public AutoPilot getAutopilot(){
        return types.getAutoPilot();
    }

    public CraftTypes getCraftTypes() {
        return types;
    }

    public PilotSensor getPilotSensor() {
        return pilotSensor;
    }

    public BaseIOIOLooper getLooper() {
        return looper;
    }
}
