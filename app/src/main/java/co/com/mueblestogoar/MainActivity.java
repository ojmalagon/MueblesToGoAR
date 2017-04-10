package co.com.mueblestogoar;

import android.content.Intent;
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

import java.io.File;
import java.util.ArrayList;
/**
 * Created by ojmalagon on 9/04/2017.
 */
public class MainActivity extends AppCompatActivity {

    GridView gw;
    ArrayList<File> lstFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstFile = imageReader(Environment.getExternalStorageDirectory());

        gw = (GridView) findViewById(R.id.grPpal);
        gw.setAdapter( new GridAdapter());

        gw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(),ViewAR.class);
                String p = lstFile.get(position).getAbsoluteFile().toString();
                i.putExtra("img",p);
                startActivity(i);
            }
        });
    }

    ArrayList<File> imageReader(File root){

        ArrayList<File> a = new ArrayList<File>();
        File[] files = root.listFiles();

        for(int i =0; i<files.length;i++){
            if(files[i].isDirectory()){
                a.addAll( imageReader( files[i]));
            }
            else{

                if(files[i].getName().endsWith(".jpg")){
                    a.add(files[i]);
                }
            }

        }
        return a;

    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lstFile.size();
        }

        @Override
        public Object getItem(int position) {
            return lstFile.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.single_grid, parent,false);
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);
            iv.setImageURI(Uri.parse(getItem(position).toString()));
            return convertView;
        }
    }

}
