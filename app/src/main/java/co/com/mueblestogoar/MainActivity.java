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
import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by ojmalagon on 9/04/2017.
 */
public class MainActivity extends AppCompatActivity {

    GridView gw;
    private SensorManager mSensorManager;
    private float azimuth;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load Data
        Data data = new Data();
        data.LoadData();

        gw = (GridView) findViewById(R.id.grPpal);
        //getIntent().get

        this.index = -1;
        if(getIntent().getIntExtra("index",-1)!=-1){
            //
            this.index = getIntent().getIntExtra("index",0);
        }
        gw.setAdapter( new GridAdapter(data,this.index));

        gw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if( index==-1){
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtra("index",position);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(getApplicationContext(),ViewAR.class);
                    String p = "";//Data.ITEM_NAMES[position].toString();
                    float orientation[] = new float[3];
                    float R[] = new float[9];
                    SensorManager.getOrientation(R, orientation);
                    azimuth = (orientation[0] < 0) ? 2 * (float)Math.PI + orientation[0] : orientation[0];
                    i.putExtra("model",p);
                    i.putExtra("azimuth", azimuth);
                    startActivity(i);
                }

            }
        });
    }


    class GridAdapter extends BaseAdapter {

        private Data DataGrid;
        private int index;


         public GridAdapter(Data data, int index){
             DataGrid = data;
             this.index = index;
         }

        @Override
        public int getCount() {

            //return Data.IMAGE_IDS.length;
            if(this.index==-1){
                return DataGrid.getCatalog().size();
            }
            else{
                return DataGrid.getCatalog().get(index).getItems().size();
            }

        }

        @Override
        public Object getItem(int position) {

            //return Data.IMAGE_IDS[position];
            if(this.index==-1){
                return DataGrid.getCatalog().get(position);
            }
            else{
                return DataGrid.getCatalog().get(index).getItems().get(position);
            }
        }

        @Override
        public long getItemId(int position) {

            if(this.index==-1){
                return DataGrid.getCatalog().get(position).getId();
            }
            else{
                return DataGrid.getCatalog().get(index).getItems().get(position).getId();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.single_grid, parent,false);
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);
            TextView tv = (TextView) convertView.findViewById(R.id.textView);

            if(this.index==-1){
                //return DataGrid.getCatalog().get(position).getId();
                iv.setImageResource(DataGrid.getCatalog().get(position).getImage());
                tv.setText(DataGrid.getCatalog().get(position).getName());
            }
            else{
                //return DataGrid.getCatalog().get(index).getItems().get(position).getId();
                iv.setImageResource(DataGrid.getCatalog().get(index).getItems().get(position).getImage());
                tv.setText(DataGrid.getCatalog().get(index).getItems().get(position).getName());
            }

            return convertView;
        }
    }




}
