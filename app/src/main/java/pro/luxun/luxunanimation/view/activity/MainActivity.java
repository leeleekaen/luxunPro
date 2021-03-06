package pro.luxun.luxunanimation.view.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;
import pro.luxun.luxunanimation.R;
import pro.luxun.luxunanimation.bean.Auth;
import pro.luxun.luxunanimation.bean.MainJson;
import pro.luxun.luxunanimation.global.IntentConstant;
import pro.luxun.luxunanimation.net.RetrofitClient;
import pro.luxun.luxunanimation.presenter.presenter.MainActivityPresenter;
import pro.luxun.luxunanimation.utils.UserInfoHelper;
import pro.luxun.luxunanimation.utils.Utils;
import pro.luxun.luxunanimation.view.fragment.MainFragment_;
import pro.luxun.luxunanimation.view.fragment.MeFragment_;
import pro.luxun.luxunanimation.view.fragment.TopicFragment_;
import pro.luxun.luxunanimation.view.view.Update;
import ykooze.ayaseruri.codesslib.adapter.ViewPagerFragmentAdapter;
import ykooze.ayaseruri.codesslib.rx.RxActivity;
import ykooze.ayaseruri.codesslib.rx.RxUtils;

@EActivity(R.layout.activity_main)
public class MainActivity extends RxActivity implements INetCacheData<MainJson> {

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;
    @ViewById(R.id.main_tab)
    TabLayout mTabLayout;
    @ViewById(R.id.main_view_pager)
    ViewPager mViewPager;
    @StringRes(R.string.loading)
    String mLoadingStr;
    @StringRes(R.string.net_error)
    String mNetError;
    @StringRes(R.string.retry)
    String mRetry;
    @StringArrayRes(R.array.main_tab_titles)
    String[] mMainTabTitles;
    @Bean
    Update mUpdate;

    private SweetAlertDialog mAlertDialog;
    private MainActivityPresenter mMainActivityPresenter;

    @AfterViews
    void init(){
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mMainActivityPresenter = new MainActivityPresenter(this, this);

        mMainActivityPresenter.getMainJsonCache();
        mMainActivityPresenter.getMainJsonNet();

        mUpdate.checkUpdate(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UserInfoHelper.isLogin(this)){
            RetrofitClient.getApiService().refreshAuth(RetrofitClient.URL_REFRESH_AUTH
                    , Utils.str2RequestBody(UserInfoHelper.getUserInfo(this).getSss()))
                    .compose(RxUtils.<Auth.UserEntity>applySchedulers())
                    .subscribe(new Consumer<Auth.UserEntity>() {
                        @Override
                        public void accept(Auth.UserEntity userEntity) throws Exception {
                            UserInfoHelper.save(MainActivity.this, userEntity);
                        }
                    });

        }
    }

    @Override
    public void onStartGetJsonNet() {

    }

    @Override
    public void onGetJsonSuccessNet(MainJson mainJson) {
        onGetJsonCacheSuccess(mainJson);
    }

    @Override
    public void onGetJsonErrorNet() {
        if(null != mAlertDialog){
            mAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            mAlertDialog.setTitleText(mNetError);
            mAlertDialog.setConfirmText(mRetry);
            mAlertDialog.showCancelButton(true);
            mAlertDialog.setCancelText("退出");
            mAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    mAlertDialog.dismiss();
                    finish();
                }
            });
            mAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    mAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    mAlertDialog.setTitleText(mLoadingStr);
                    mMainActivityPresenter.getMainJsonNet();
                }
            });
        }else {
            finish();
        }
    }

    @Override
    public void onGetJsonCacheStart() {
        mAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mAlertDialog.setTitleText(mLoadingStr);
        mAlertDialog.show();
    }

    @Override
    public void onGetJsonCacheSuccess(MainJson mainJson) {
        if(null != mAlertDialog && mAlertDialog.isShowing()){
            mAlertDialog.dismiss();
        }

        //初始化Fragments
        initMain();
    }

    @Override
    public void onGetJsonCacheFailed() {
        mMainActivityPresenter.getMainJsonNet();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init();
    }

    private void initMain(){
        ViewPagerFragmentAdapter viewPagerAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(MeFragment_.builder().build(), mMainTabTitles[0]);
        viewPagerAdapter.add(MainFragment_.builder().build(), mMainTabTitles[1]);
        viewPagerAdapter.add(TopicFragment_.builder().build(), mMainTabTitles[3]);
        mViewPager.setAdapter(viewPagerAdapter);

        mViewPager.setCurrentItem(getIntent().getIntExtra(IntentConstant.INTENT_MAIN_FRAGMENT_POS, 1));

        mTabLayout.setVisibility(View.VISIBLE);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
