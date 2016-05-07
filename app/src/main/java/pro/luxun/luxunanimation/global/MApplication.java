package pro.luxun.luxunanimation.global;

import android.app.Application;

import pro.luxun.luxunanimation.utils.SerializeUtils;

/**
 * Created by wufeiyang on 16/5/7.
 */
public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SerializeUtils.init(this);
    }
}
