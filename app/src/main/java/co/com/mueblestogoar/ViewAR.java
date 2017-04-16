package co.com.mueblestogoar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.camera.CameraSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;


/**
 * Created by ojmalagon on 9/04/2017.
 */

public class ViewAR  extends AbstractArchitectCamActivity implements SensorEventListener {

    ImageView iv2;
    private ArchitectView architectView;
    private WebView webView;
    private static final int REQUEST_CAMERA_RESULT = 1;
    private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
    private Intent intent;
    private String jsonString;
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float azimuth;
    float[] mGravity;
    float[] mGeomagnetic;
    ServerSocket serverSocket;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }
        super.onCreate(savedInstanceState);
        intent = getIntent();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        Thread socketServerThread = new Thread(new SocketServerThread(this));
        socketServerThread.start();
    }

    @Override
    protected CameraSettings.CameraPosition getCameraPosition() {
        return CameraSettings.CameraPosition.DEFAULT;
    }

    @Override
    protected boolean hasGeo() {
        return true;
    }

    @Override
    protected boolean hasIR() {
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String[] filename = {"\"" + intent.getStringExtra("model") + "\""};
        callJavaScript("World.init", filename);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getActivityTitle() {
        return "Vista";
    }

    @Override
    public String getARchitectWorldPath() {
        //return "file:////android_asset/render/index.html";
        return "file:////android_asset/render/index.html";
    }

    @Override
    public ArchitectView.ArchitectUrlListener getUrlListener() {
        return new ArchitectView.ArchitectUrlListener() {

            @Override
            public boolean urlWasInvoked(String uriString) {
                Uri invokedUri = Uri.parse(uriString);

                if ("markerselected".equalsIgnoreCase(invokedUri.getHost())) {
                    return false;
                }

                // pressed snapshot button. check if host is button to fetch e.g. 'architectsdk://button?action=captureScreen', you may add more checks if more buttons are used inside AR scene
                else if ("button".equalsIgnoreCase(invokedUri.getHost())) {
                    ViewAR.this.architectView.captureScreen(ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW, new ArchitectView.CaptureScreenCallback() {

                        @Override
                        public void onScreenCaptured(final Bitmap screenCapture) {
                            final File screenCaptureFile = new File(Environment.getExternalStorageDirectory().toString(), "screenCapture_" + System.currentTimeMillis() + ".jpg");

                            try {
                                final FileOutputStream out = new FileOutputStream(screenCaptureFile);
                                screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();

                                final Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("image/jpg");
                                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));
                                final String chooserTitle = "Share Snaphot";
                                ViewAR.this.startActivity(Intent.createChooser(share, chooserTitle));

                            } catch (final Exception e) {
                                ViewAR.this.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(ViewAR.this, "Unexpected error, " + e, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
                return true;
            }
        };
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_view_ar;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        //return "0z/8Fwhl7tOjD92txxhdpofO2BsLowXtjo/AeS8Lp7VSVMBvTwPnRjNY0iZAEwpjN63AuXGIr7GyzlfBRd4dcut47aylf5PrVQds/t3AEszAcYPqpHjt2bAPhMZBWv87F8gLH+67p1GdxOzsIxdAQbOMPskg5oCyUibqd3AOAp1TYWx0ZWRfXxthfZ8BBFZf51jzGmPm/as0jG5ygU0Uw0P8ASobBoJgG1w8NjjO3AkTCzVyoeb7zftf2RZlLRhvEeMlsDENRvbSTakH/zJN1dysXJ/sbn0th6yi/GUYjDLSjLI8xlzb8Fj0OSZ+pqgPKT6Jbkoc+nhKcmDZrGFJXalrpXoOpO+mFyyz64akFdax8MwpNgFnvPmK+fJmwRYFHThIB81YVNvRN+pzk0/r3d8NjT6Xy40NeVqFfEOmznRc7EyrHWrHUrdJ+Ng1/GoasLPBokN3RBkxF0VblZ5j6dFBMowfHZr62J0/QDjQz4nB+E392SEDaxVEu76EQdbf1MFkRUixTWIpngNGUF1goWddEM0rGyb2SEqZAjhhWZlvuHVwlcRm/ZaRQsjnX12yKACSViXqopVtRnFkCZMgy0d4E8LDzdI2fitk2EReY4cPyK8q3SXwdPpUvl9s0c41DNehGO54oQoY53VDWHzUgMHSSo7I/aBDkfa3cmQwT4s=";
        return "DSTFpFW4YWjPRJA1Rz/2BnNEawePW1XR3kU0hG1pcSHGVvjmht5tECzauqgJWYVDa6vBkV0ZH8m24eswHnrN4F9fEAV1sdZKZMXIoDtcIyoZUkabVeX2A24r3BEviVHOfzP8twfCdaCwrVAboYLMpI0VxnfIxVj5VBlYx8FdJqlTYWx0ZWRfX7pSUJYB/ytSPrW4QpeJyI5VJeY6a7Yd38ehFEBX5Id1NsZZFvHYTEZgo+s4WEVgLr9fX/qs5/rh2Dnhib3+Jvu17D0fqMfGeIcILKmmg/oWtlQZavnZwXK2Z/JPs3gXxqWwsvQyE+90K/r72KMMAVFKovXVj9DCn/WiRMtRs5fCE5DeLe7JXnA4lACRadlrUjdRFEixuQeYRLu+v9pwEwbzjn39ssR/bpxLEBuK60Ejda7ivBn43zSO5XuKIqB+jyG+iwOWXJ4wnVjzR7gvdLh202NitQ1KpAMNqtiZqYv9k6xoS/smxfJUR4Gql11cfTO8zcAy4p3XQ+jgUgHeQsk3o13OAxCBzIiRog1hB7U30g/9F8t+LAniUHbqhkzPyi6EjtgRZnyJmUC6EyZuGCs/TfIIRD6ewUh5LSnya8eLrh20Iq1iDB3+zFLG9IX53gBq8NC6+tU/upIWA4i2vWdm/NcwTu1Xj+3zODgbeY4gfda/vsh5t90=";
    }

    @Override
    public int getArchitectViewId() {
        return R.id.architectView ;
    }

    @Override
    public ILocationProvider getLocationProvider(LocationListener locationListener) {
        return new LocationProvider(this, locationListener);
    }

    @Override
    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return new ArchitectView.SensorAccuracyChangeListener() {
            @Override
            public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && ViewAR.this != null && !ViewAR.this.isFinishing() && System.currentTimeMillis() - ViewAR.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                       Toast.makeText(ViewAR.this,R.string.compass_accuracy_low,Toast.LENGTH_LONG).show();
                       lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
                }
            }
        };
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public float getInitialCullingDistanceMeters() {
        return 0;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = sensorEvent.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float newAzimuth = (orientation[0] < 0) ? 2 * (float)Math.PI + orientation[0] : orientation[0];
                float diffAzimuth = newAzimuth - intent.getExtras().getFloat("azimuth");
                azimuth = diffAzimuth;
                String[] callJSArg = {Float.toString(azimuth)};
                //callJavaScript("World.setBearingExternally", callJSArg);
                try {
                    writeBearingToJSON(azimuth);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public String getJSON() {
        return jsonString;
    }

    private void writeBearingToJSON(Float azimuth) throws IOException {
        File file = new File(getExternalCacheDir(), "bearing.json");
        FileOutputStream stream = new FileOutputStream(file);
        jsonString = "{\"bearing\": " + azimuth + ",\n" +
                "\"selection\": \"" + intent.getStringExtra("model")+ "\"}";
        try {
            stream.write(jsonString.getBytes());
        } finally {
            stream.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case  REQUEST_CAMERA_RESULT:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Cannot run application because camera service permission have not been granted", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
