package co.com.mueblestogoar;

import android.location.LocationListener;

import com.wikitude.architect.ArchitectView;

/**
 * Created by ojmalagon on 11/04/2017.
 */

public interface ArchitectViewHolderInterface {

    public static final int CULLING_DISTANCE_DEFAULT_METERS = 50 * 1000;

    public String getARchitectWorldPath();

    public ArchitectView.ArchitectUrlListener getUrlListener();

    public int getContentViewId();

    public String getWikitudeSDKLicenseKey();

    public int getArchitectViewId();

    public ILocationProvider getLocationProvider(final LocationListener locationListener);

    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener();

    public float getInitialCullingDistanceMeters();

    public static interface ILocationProvider {

        public void onResume();

        public void onPause();

    }

}
