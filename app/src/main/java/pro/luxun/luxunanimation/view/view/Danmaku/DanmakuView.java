package pro.luxun.luxunanimation.view.view.Danmaku;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import pro.luxun.luxunanimation.bean.Danmaku;
import pro.luxun.luxunanimation.utils.Utils;
import ykooze.ayaseruri.codesslib.ui.LocalDisplay;

/**
 * Created by wufeiyang on 16/5/23.
 */
public class DanmakuView extends master.flame.danmaku.ui.widget.DanmakuView{

    private DanmakuContext mDanmakuContext;

    public DanmakuView(Context context) {
        super(context);
        initDanmuConfig();
    }

    public DanmakuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDanmuConfig();
    }

    private void initDanmuConfig() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 4); // 滚动弹幕最大显示4行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)//越大速度越慢
                .setScaleTextSize(1.2f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });

        prepare(new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);

        enableDanmakuDrawingCache(true);
    }

    public void refreshDanmaku(List<Danmaku> danmakus){
        for (int i = 0; i < danmakus.size(); i++){
            addDanmaku(danmakus.get(i), i, false);
        }
    }

    public void addDanmaku(Danmaku danmaku){
        addDanmaku(danmaku, 0, true);
    }

    public void addDanmaku(Danmaku danmaku, int i, boolean isLocal){
        int type;
        switch (danmaku.getType()){
            case DanmakuConstant.TYPE_RL:
                type = BaseDanmaku.TYPE_SCROLL_RL;
                break;
            case DanmakuConstant.TYPE_TOP:
                type = BaseDanmaku.TYPE_FIX_TOP;
                break;
            case DanmakuConstant.TYPE_BOTTOM:
                type = BaseDanmaku.TYPE_FIX_BOTTOM;
                break;
            default:
                type = BaseDanmaku.TYPE_SCROLL_LR;
                break;
        }

        BaseDanmaku baseDanmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(type
                , LocalDisplay.SCREEN_HEIGHT_PIXELS
                , LocalDisplay.SCREEN_WIDTH_PIXELS, 1.0f, mDanmakuContext.scrollSpeedFactor);
        baseDanmaku.text = danmaku.getText();
        baseDanmaku.textColor = Color.parseColor(danmaku.getColor());
        baseDanmaku.textShadowColor = Utils.isColorDark(baseDanmaku.textColor) ? Color.WHITE : Color.BLACK;
        baseDanmaku.index = i;
        baseDanmaku.priority = isLocal ? (byte) 1 : (byte) 0;
        baseDanmaku.isLive = false;
        baseDanmaku.textSize = LocalDisplay.dp2px(16);
        if(isLocal){
            baseDanmaku.borderColor = Color.WHITE;
        }
        addDanmaku(baseDanmaku);
    }
}
