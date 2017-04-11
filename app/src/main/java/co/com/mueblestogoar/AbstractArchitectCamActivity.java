package co.com.mueblestogoar;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.common.camera.CameraSettings;
import com.wikitude.common.camera.CameraSettings.CameraPosition;
//import com.wikitude.architect.ArchitectStartupConfiguration.Features.*;

/**
 * Created by ojmalagon on 11/04/2017.
 */

public abstract class AbstractArchitectCamActivity extends Activity implements ArchitectViewHolderInterface {

    protected ArchitectView					architectView;

    protected SensorAccuracyChangeListener	sensorAccuracyListener;

    protected Location 						lastKnownLocaton;

    protected ILocationProvider				locationProvider;

    protected LocationListener 				locationListener;

    protected ArchitectUrlListener 			urlListener;

    protected JSONArray poiData;

    protected boolean isLoading = false;

    @SuppressLint("NewApi")
    @Override
    public void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        this.setVolumeControlStream( AudioManager.STREAM_MUSIC );

        this.setContentView( this.getContentViewId() );

        this.setTitle( this.getActivityTitle() );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) ) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        this.architectView = (ArchitectView)this.findViewById( this.getArchitectViewId()  );

        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration( );
        config.setLicenseKey(this.getWikitudeSDKLicenseKey());
        config.setFeatures(this.getFeatures());
        config.setCameraPosition(this.getCameraPosition());

        try {
            this.architectView.onCreate( config );
        } catch (RuntimeException rex) {
            this.architectView = null;
            Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT).show();
            Log.e(this.getClass().getName(), "Exception in ArchitectView.onCreate()", rex);
        }

        this.sensorAccuracyListener = this.getSensorAccuracyListener();

        this.urlListener = this.getUrlListener();

        if (this.urlListener != null && this.architectView != null) {
            this.architectView.registerUrlListener( this.getUrlListener() );
        }

        if (hasGeo()) {
            this.locationListener = new LocationListener() {

                @Override
                public void onStatusChanged( String provider, int status, Bundle extras ) {
                }

                @Override
                public void onProviderEnabled( String provider ) {
                }

                @Override
                public void onProviderDisabled( String provider ) {
                }

                @Override
                public void onLocationChanged( final Location location ) {
                    if (location!=null) {
                        AbstractArchitectCamActivity.this.lastKnownLocaton = location;
                        if ( AbstractArchitectCamActivity.this.architectView != null ) {
                            if ( location.hasAltitude() && location.hasAccuracy() && location.getAccuracy()<7) {
                                AbstractArchitectCamActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy() );
                            } else {
                                AbstractArchitectCamActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.hasAccuracy() ? location.getAccuracy() : 1000 );
                            }
                        }
                    }
                }
            };

            this.locationProvider = getLocationProvider( this.locationListener );
        } else {
            this.locationProvider = null;
            this.locationListener = null;
        }
    }

    protected abstract CameraPosition getCameraPosition();

    private int getFeatures() {
        int features = (hasGeo() ? ArchitectStartupConfiguration.Features.Geo : 0) | (hasIR() ? ArchitectStartupConfiguration.Features.Tracking2D : 0);
        return features;
    }

    protected abstract boolean hasGeo();
    protected abstract boolean hasIR();

    @Override
    protected void onPostCreate( final Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );

        if ( this.architectView != null ) {

            this.architectView.onPostCreate();

            try {
                this.architectView.load( this.getARchitectWorldPath() );

                if (this.getInitialCullingDistanceMeters() != ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS) {
                    this.architectView.setCullingDistance( this.getInitialCullingDistanceMeters() );
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ( this.architectView != null ) {
            this.architectView.onResume();

            if (this.sensorAccuracyListener!=null) {
                this.architectView.registerSensorAccuracyChangeListener( this.sensorAccuracyListener );
            }
        }

        if ( this.locationProvider != null ) {
            this.locationProvider.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if ( this.architectView != null ) {
            this.architectView.onPause();

            if ( this.sensorAccuracyListener != null ) {
                this.architectView.unregisterSensorAccuracyChangeListener( this.sensorAccuracyListener );
            }
        }

        if ( this.locationProvider != null ) {
            this.locationProvider.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if ( this.architectView != null ) {
            this.architectView.clearAppCache();
            this.architectView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if ( this.architectView != null ) {
            this.architectView.onLowMemory();
        }
    }

    public abstract String getActivityTitle();

    @Override
    public abstract String getARchitectWorldPath();

    @Override
    public abstract ArchitectUrlListener getUrlListener();

    @Override
    public abstract int getContentViewId();

    @Override
    public abstract String getWikitudeSDKLicenseKey();

    @Override
    public abstract int getArchitectViewId();

    @Override
    public abstract ILocationProvider getLocationProvider(final LocationListener locationListener);

    @Override
    public abstract ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener();

    public static final boolean isVideoDrawablesSupported() {
        String extensions = GLES20.glGetString( GLES20.GL_EXTENSIONS );
        return extensions != null && extensions.contains( "GL_OES_EGL_image_external" );
    }

    protected void injectData() {
        if (!isLoading) {
            final Thread t = new Thread(new Runnable() {

                @Override
                public void run() {

                    isLoading = true;

                    final int WAIT_FOR_LOCATION_STEP_MS = 2000;

                    while (lastKnownLocaton==null && !isFinishing()) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(AbstractArchitectCamActivity.this, R.string.location_fetching, Toast.LENGTH_SHORT).show();
                            }
                        });

                        try {
                            Thread.sleep(WAIT_FOR_LOCATION_STEP_MS);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }

                    if (lastKnownLocaton!=null && !isFinishing()) {
                        poiData = getPoiInformation(lastKnownLocaton, 20);
                        callJavaScript("World.loadPoisFromJsonData", new String[] { poiData.toString() });
                    }

                    isLoading = false;
                }
            });
            t.start();
        }
    }

    public void callJavaScript(final String methodName, final String[] arguments) {
        final StringBuilder argumentsString = new StringBuilder("");
        for (int i= 0; i<arguments.length; i++) {
            argumentsString.append(arguments[i]);
            if (i<arguments.length-1) {
                argumentsString.append(", ");
            }
        }

        if (this.architectView!=null) {
            final String js = ( methodName + "( " + argumentsString.toString() + " );" );
            this.architectView.callJavascript(js);
        }
    }

    public static JSONArray getPoiInformation(final Location userLocation, final int numberOfPlaces) {

        if (userLocation==null) {
            return null;
        }

        final JSONArray pois = new JSONArray();

        final String ATTR_ID = "id";
        final String ATTR_NAME = "name";
        final String ATTR_DESCRIPTION = "description";
        final String ATTR_LATITUDE = "latitude";
        final String ATTR_LONGITUDE = "longitude";
        final String ATTR_ALTITUDE = "altitude";

        for (int i=1;i <= numberOfPlaces; i++) {
            final HashMap<String, String> poiInformation = new HashMap<String, String>();
            poiInformation.put(ATTR_ID, String.valueOf(i));
            poiInformation.put(ATTR_NAME, "POI#" + i);
            poiInformation.put(ATTR_DESCRIPTION, "This is the description of POI#" + i);
            double[] poiLocationLatLon = getRandomLatLonNearby(userLocation.getLatitude(), userLocation.getLongitude());
            poiInformation.put(ATTR_LATITUDE, String.valueOf(poiLocationLatLon[0]));
            poiInformation.put(ATTR_LONGITUDE, String.valueOf(poiLocationLatLon[1]));
            final float UNKNOWN_ALTITUDE = -32768f;  // equals "AR.CONST.UNKNOWN_ALTITUDE" in JavaScript (compare AR.GeoLocation specification)
            poiInformation.put(ATTR_ALTITUDE, String.valueOf(UNKNOWN_ALTITUDE));
            pois.put(new JSONObject(poiInformation));
        }

        return pois;
    }

    private static double[] getRandomLatLonNearby(final double lat, final double lon) {
        return new double[] { lat + Math.random()/5-0.1 , lon + Math.random()/5-0.1};
    }

}
