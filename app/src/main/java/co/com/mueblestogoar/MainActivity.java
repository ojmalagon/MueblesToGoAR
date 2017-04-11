package co.com.mueblestogoar;

import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
/**
 * Created by ojmalagon on 9/04/2017.
 */
public class MainActivity extends AppCompatActivity {

    GridView gw;
    private SensorManager mSensorManager;
    private float azimuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gw = (GridView) findViewById(R.id.grPpal);
        gw.setAdapter( new GridAdapter());

        gw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(),ViewAR.class);
                String p = Data.ITEM_NAMES[position].toString();
                float orientation[] = new float[3];
                float R[] = new float[9];
                SensorManager.getOrientation(R, orientation);
                azimuth = (orientation[0] < 0) ? 2 * (float)Math.PI + orientation[0] : orientation[0];
                i.putExtra("model",p);
                i.putExtra("azimuth", azimuth);
                startActivity(i);
            }
        });
    }


    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Data.IMAGE_IDS.length;
        }

        @Override
        public Object getItem(int position) {
            return Data.IMAGE_IDS[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.single_grid, parent,false);
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);
            TextView tv = (TextView) convertView.findViewById(R.id.textView);
            iv.setImageResource(Data.IMAGE_IDS[position]);
            tv.setText(Data.ITEM_NAMES[position]);
            return convertView;
        }
    }




}
