package co.com.mueblestogoar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import java.io.IOException;


/**
 * Created by ojmalagon on 9/04/2017.
 */

public class ViewAR  extends AppCompatActivity {

    ImageView iv2;
    private ArchitectView architectView;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ar);

        Intent i = getIntent();
        webView = new WebView(this);

        //File f = i.getExtras().getParcelable("img");

        /*String r = i.getStringExtra("img");
        iv2 = (ImageView) findViewById(R.id.imageView2);
        iv2.setImageURI(Uri.parse( r));*/


        this.architectView = (ArchitectView)this.findViewById( R.id.architectView );
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey("0z/8Fwhl7tOjD92txxhdpofO2BsLowXtjo/AeS8Lp7VSVMBvTwPnRjNY0iZAEwpjN63AuXGIr7GyzlfBRd4dcut47aylf5PrVQds/t3AEszAcYPqpHjt2bAPhMZBWv87F8gLH+67p1GdxOzsIxdAQbOMPskg5oCyUibqd3AOAp1TYWx0ZWRfXxthfZ8BBFZf51jzGmPm/as0jG5ygU0Uw0P8ASobBoJgG1w8NjjO3AkTCzVyoeb7zftf2RZlLRhvEeMlsDENRvbSTakH/zJN1dysXJ/sbn0th6yi/GUYjDLSjLI8xlzb8Fj0OSZ+pqgPKT6Jbkoc+nhKcmDZrGFJXalrpXoOpO+mFyyz64akFdax8MwpNgFnvPmK+fJmwRYFHThIB81YVNvRN+pzk0/r3d8NjT6Xy40NeVqFfEOmznRc7EyrHWrHUrdJ+Ng1/GoasLPBokN3RBkxF0VblZ5j6dFBMowfHZr62J0/QDjQz4nB+E392SEDaxVEu76EQdbf1MFkRUixTWIpngNGUF1goWddEM0rGyb2SEqZAjhhWZlvuHVwlcRm/ZaRQsjnX12yKACSViXqopVtRnFkCZMgy0d4E8LDzdI2fitk2EReY4cPyK8q3SXwdPpUvl9s0c41DNehGO54oQoY53VDWHzUgMHSSo7I/aBDkfa3cmQwT4s=");
        this.architectView.onCreate( config );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();
        try {
            webView.getSettings().setJavaScriptEnabled(true);
            architectView.load( "file:////android_asset/demo1/index.html" );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        architectView.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        architectView.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
        architectView.onPause();
    }

}
