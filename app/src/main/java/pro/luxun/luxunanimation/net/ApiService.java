package pro.luxun.luxunanimation.net;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import pro.luxun.luxunanimation.bean.Auth;
import pro.luxun.luxunanimation.bean.Comment;
import pro.luxun.luxunanimation.bean.Danmaku;
import pro.luxun.luxunanimation.bean.GetToken;
import pro.luxun.luxunanimation.bean.LikeBangumi;
import pro.luxun.luxunanimation.bean.MainJson;
import pro.luxun.luxunanimation.bean.PostLikeComment;
import pro.luxun.luxunanimation.bean.SubComment;
import pro.luxun.luxunanimation.bean.TopicJson;
import pro.luxun.luxunanimation.bean.Update;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by wufeiyang on 16/5/7.
 */
public interface ApiService {
    @GET
    Observable<MainJson> getMainJson(@Url String url);

    @GET
    Observable<ArrayList<TopicJson>> getTopicJson(@Url String url);

    @Multipart
    @POST
    Observable<GetToken> getToken(@Url String url, @Part("text") String text);

    @GET
    Observable<Auth> auth(@Url String url);

    @GET
    Observable<List<Comment>> getCommentList(@Url String url);

    @Multipart
    @POST
    Observable<SubComment> submitComment(@Url String url, @Part("rate") int rate
            , @Part("cur") int cur
            , @Part("time") RequestBody time
            , @Part("text") RequestBody comment);

    @GET
    Observable<ArrayList<String>> getBangumis(@Url String url);

    @Multipart
    @POST
    Observable<LikeBangumi> subscribe(@Url String url, @Part("type") RequestBody type);

    @GET
    Observable<List<Integer>> getlikeCommentIds(@Url String url);

    @GET
    Observable<List<Comment>> getlikeComment(@Url String url);

    @Multipart
    @POST
    Observable<PostLikeComment> likeComment(@Url String url, @Part("type") RequestBody cid, @Part("theid") RequestBody theid);

    @GET
    Observable<List<List>> getDm(@Url String url);

    @Multipart
    @POST
    Observable<Danmaku> submitDm(@Url String url, @Part("start") RequestBody time
            , @Part("text") RequestBody text, @Part("color") RequestBody color, @Part("y") int y
            , @Part("type") RequestBody type);

    @GET
    Observable<Update> checkUpdate(@Url String url);

    @Multipart
    @POST
    Observable<Auth.UserEntity> refreshAuth(@Url String url, @Part("sss") RequestBody sss);
}
