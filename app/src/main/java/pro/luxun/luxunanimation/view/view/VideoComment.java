package pro.luxun.luxunanimation.view.view;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import com.pnikosis.materialishprogress.ProgressWheel;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pro.luxun.luxunanimation.R;
import pro.luxun.luxunanimation.bean.Comment;
import pro.luxun.luxunanimation.bean.SubComment;
import pro.luxun.luxunanimation.global.MApplication;
import pro.luxun.luxunanimation.net.ApiService;
import pro.luxun.luxunanimation.net.RetrofitClient;
import pro.luxun.luxunanimation.presenter.adapter.BaseRecyclerAdapter;
import pro.luxun.luxunanimation.utils.StartUtils;
import pro.luxun.luxunanimation.utils.UserInfoHelper;
import pro.luxun.luxunanimation.utils.Utils;
import ykooze.ayaseruri.codesslib.others.ToastUtils;
import ykooze.ayaseruri.codesslib.rx.RxUtils;

/**
 * Created by wufeiyang on 16/5/12.
 */
@EViewGroup(R.layout.view_video_comment)
public class VideoComment extends RelativeLayout{

    @ViewById(R.id.rating_bar)
    RatingBar mStarBar;
    @ViewById(R.id.comment_et)
    EditText mCommentET;
    @ViewById(R.id.rating_bar)
    RatingBar mRatingBar;
    @ViewById(R.id.progress_submit)
    ProgressWheel mProgressSubmit;
    @ViewById(R.id.progress_list)
    ProgressWheel mProgressList;
    @ViewById(R.id.recycler)
    RecyclerView mRecyclerView;
    @App
    MApplication mMApplication;

    private ApiService mApiService;
    private String mCommentUrl;
    private int mCur = 0;
    private BaseRecyclerAdapter<Comment, CommentItem> mAdapter;
    private ArrayList<Integer> mLikedCommentsId;

    public VideoComment(Context context) {
        super(context);
    }

    public VideoComment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void init(){
        mProgressSubmit.setVisibility(GONE);

        mApiService = RetrofitClient.getApiService();

        mAdapter = new BaseRecyclerAdapter<Comment, CommentItem>(){

            @Override
            protected CommentItem onCreateItemView(ViewGroup parent, int viewType) {
                return CommentItem_.build(parent.getContext());
            }

            @Override
            protected void onBindView(CommentItem commentItem, Comment comment) {
                commentItem.bind(comment, mLikedCommentsId.contains(Integer.valueOf(comment.getCid())));
            }
        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mLikedCommentsId = new ArrayList<>();
    }

    @Click(R.id.submit_btn)
    void onSubmit(){
        if(!UserInfoHelper.isLogin(getContext())){
            Snackbar.make(getRootView(), "登录之后才能评论…", Snackbar.LENGTH_LONG).setAction("登录", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartUtils.startMainActivity(getContext(), 0);
                }
            }).show();
            return;
        }

        if(TextUtils.isEmpty(mCommentUrl) || -1 ==mCur){
            throw new IllegalArgumentException("需要调用initComment来初始化评论url参数");
        }

        String comment = mCommentET.getText().toString();
        if(TextUtils.isEmpty(comment)){
            Snackbar.make(this, "番评不能为空", Snackbar.LENGTH_LONG).show();
            return;
        }

        mApiService.submitComment(mCommentUrl, (int) mRatingBar.getRating()
                , mCur, Utils.str2RequestBody("0.00"), Utils.str2RequestBody(comment))
                .compose(RxUtils.<SubComment>applySchedulers())
                .subscribe(new Observer<SubComment>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mProgressSubmit.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onNext(SubComment subComment) {
                        ToastUtils.showTost(getContext(), ToastUtils.TOAST_CONFIRM, "评论成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(VideoComment.this, "评论出错", Snackbar.LENGTH_LONG).show();
                        mProgressSubmit.setVisibility(GONE);
                    }

                    @Override
                    public void onComplete() {
                        mCommentET.setText("");
                        mProgressSubmit.setVisibility(GONE);
                        initCommentList();
                    }
                });
    }


    public void initComment(String name){
        mCommentUrl = RetrofitClient.URL_COMMENT + Utils.encodeURIComponent(name);
        initCommentList();
    }

    public void initCommentList(){
        mApiService.getlikeCommentIds(RetrofitClient.URL_LIKE)
                .compose(RxUtils.<List<Integer>>applySchedulers())
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        mLikedCommentsId.clear();
                        mLikedCommentsId.addAll(integers);
                    }
                });

        mApiService.getCommentList(mCommentUrl).compose(RxUtils.<List<Comment>>applySchedulers())
                .subscribe(new Observer<List<Comment>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mProgressList.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mAdapter.refresh(comments);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(VideoComment.this, "评论刷新失败", Snackbar.LENGTH_LONG).show();
                        mProgressList.setVisibility(INVISIBLE);
                    }

                    @Override
                    public void onComplete() {
                        mProgressList.setVisibility(INVISIBLE);
                    }
                });
    }
}
