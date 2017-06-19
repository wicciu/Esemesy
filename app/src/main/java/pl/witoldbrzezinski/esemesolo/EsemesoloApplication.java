package pl.witoldbrzezinski.esemesolo;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class EsemesoloApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
    }
}
