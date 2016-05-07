package pro.luxun.luxunanimation.presenter.adapter;

import pro.luxun.luxunanimation.bean.MainJson;
import pro.luxun.luxunanimation.model.IMainActivityModel;
import pro.luxun.luxunanimation.model.MainActivityModel;
import pro.luxun.luxunanimation.utils.RxUtils;
import pro.luxun.luxunanimation.view.activity.IMainActivity;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by wufeiyang on 16/5/7.
 */
public class MainActivityPresenter {

    private IMainActivity mainActivity;
    private IMainActivityModel mainActivityModel;

    public MainActivityPresenter(IMainActivity mainActivity) {
        this.mainActivity = mainActivity;
        mainActivityModel = new MainActivityModel();
    }

    public void getMainJsonNet(){
        mainActivityModel.getMainJsonNet().compose(RxUtils.<MainJson>applySchedulers())
            .subscribe(new Subscriber<MainJson>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mainActivity.onGetMainJsonErrorNet();
                }

                @Override
                public void onNext(MainJson mainJson) {
                    mainActivity.onGetMainJsonSuccessNet();
                }

                @Override
                public void onStart() {
                    mainActivity.onStartGetMainJsonNet();
                }
            });
    }

    public void getMainJsonCache(){
        mainActivityModel.getMainJsonCache().compose(RxUtils.<MainJson>applySchedulers())
                .subscribe(new Action1<MainJson>() {
                    @Override
                    public void call(MainJson mainJson) {
                        mainActivity.onGetMainJsonCacheSuccess(mainJson);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mainActivity.onGetMainJsonCacheFailed();
                    }
                });
    }
}
