package pilot.obss.com.android.ioio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import ioio.lib.util.android.IOIOAndroidApplicationHelper;

public abstract class BaseIOIOActivity extends Activity implements
        IOIOLooperProvider {
    private final IOIOAndroidApplicationHelper helper_ = new IOIOAndroidApplicationHelper(
            this, this);

    /**
     * Subclasses should call this method from their own onCreate() if
     * overloaded. It takes care of connecting with the IOIO.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper_.create();
    }

    /**
     * Subclasses should call this method from their own onDestroy() if
     * overloaded. It takes care of connecting with the IOIO.
     */
    @Override
    protected void onDestroy() {
        helper_.stop();
        helper_.destroy();
        super.onDestroy();
    }

    /**
     * Subclasses should call this method from their own onStart() if
     * overloaded. It takes care of connecting with the IOIO.
     */
    @Override
    protected void onStart() {
        super.onStart();
        helper_.start();
    }

    /**
     * Subclasses should call this method from their own onStop() if overloaded.
     * It takes care of disconnecting from the IOIO.
     */
    @Override
    protected void onStop() {
        helper_.stop();
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0) {
            helper_.restart();
        }
    }

    protected IOIOLooper createIOIOLooper() {
        throw new RuntimeException(
                "Client must override one of the createIOIOLooper overloads!");
    }

    @Override
    public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
        return createIOIOLooper();
    }

}