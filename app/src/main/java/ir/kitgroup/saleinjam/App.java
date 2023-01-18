package ir.kitgroup.saleinjam;

import android.app.Application;


import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.model.MapID;
import com.orm.SugarContext;


import dagger.hilt.android.HiltAndroidApp;


@HiltAndroidApp
public class App extends Application {
    @Override
    public void onCreate() {
        SugarContext.init(getApplicationContext());




        CedarMaps.getInstance()
                .setClientID("sportapp-6594917192157661130")
                .setClientSecret("b2uejHNwb3J0YXBw4V7hZnhRDiV3fQ8aqbTay-mjSd1IXllmWRN1EezGsss=")
                .setContext(this)
                .setMapID(MapID.MIX);

        super.onCreate();
    }
}