package com.yfw.kchartext.drawing;

import android.view.animation.Interpolator;

import androidx.annotation.Nullable;

import com.yfw.kchartcore.canvas.RendererCanvas;
import com.yfw.kchartcore.drawing.AbstractAnimDrawing;
import com.yfw.kchartcore.impl.IDataProvider;
import com.yfw.kchartcore.index.IEntity;
import com.yfw.kchartcore.layout.IParentPortLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @日期 : 2020/7/10
 * @描述 : 并行主动触发动画的绘制，允许多个动画同时计算
 */
public abstract class ParallelTriggerAnimDrawing<T extends IEntity> extends AbstractAnimDrawing<T> {

    private Interpolator mInterpolator;

    private final List<ChildAnim> mAnims = new ArrayList<>();
    private ChildAnim mStartAnim;
    private ChildAnim mEndAnim;

    public ParallelTriggerAnimDrawing() {
    }

    public ParallelTriggerAnimDrawing(RendererCanvas.DrawingLayoutParams params) {
        super(params);
    }

    @Override
    public void attachedParentPortLayout(IParentPortLayout portLayout, IDataProvider<T> dataProvider) {
        super.attachedParentPortLayout(portLayout, dataProvider);
        if (inAnimTime() && !inAnimationManager()) {
            mDataProvider.getChartAnimation().addAnim(this);
        }
    }

    @Override
    public void detachedParentPortLayout() {
        if (inAnimationManager()) {
            mDataProvider.getChartAnimation().removeAnim(this);
        }
        super.detachedParentPortLayout();
    }

    @Override
    public final boolean isAutoAnim() {
        return false;
    }

    @Override
    public final long getAnimStartTime() {
        return mStartAnim == null ? 0 : mStartAnim.mAnimStartTime;
    }

    /**
     * 指定id获取动画开始时间
     */
    public final long getAnimStartTime(int tagId) {
        ChildAnim childAnim = getChildAnimByTagId(tagId);
        return childAnim == null ? 0 : childAnim.mAnimStartTime;
    }

    @Override
    public final long getAnimEndTime() {
        return mEndAnim == null ? 0 : mEndAnim.mAnimEndTime;
    }

    /**
     * 指定id获取动画结束时间
     */
    public final long getAnimEndTime(int tagId) {
        ChildAnim childAnim = getChildAnimByTagId(tagId);
        return childAnim == null ? 0 : childAnim.mAnimEndTime;
    }

    @Override
    public final boolean inAnimTime() {
        return mEndAnim != null && mEndAnim.mAnimEndTime > System.currentTimeMillis();
    }

    /**
     * 指定id判断是否在动画时间内
     */
    public final boolean inAnimTime(int tagId) {
        ChildAnim childAnim = getChildAnimByTagId(tagId);
        return inAnimTime(childAnim);
    }

    private boolean inAnimTime(@Nullable ChildAnim childAnim) {
        return childAnim != null && childAnim.mAnimEndTime > System.currentTimeMillis();
    }

    /**
     * 设置插值器，如果{@link ChildAnim#mInterpolator}等于空或者等于旧的{@link #mInterpolator}
     * 则将新值赋值到{@link ChildAnim#mInterpolator}
     */
    @Override
    public void setInterpolator(Interpolator interpolator) {
        ChildAnim childAnim;
        for (int i = 0; i < mAnims.size(); i++) {
            childAnim = mAnims.get(i);
            if (childAnim.mInterpolator == null || childAnim.mInterpolator == mInterpolator) {
                childAnim.mInterpolator = interpolator;
            }
        }
        mInterpolator = interpolator;
    }

    /**
     * 指定id设置插值器
     */
    public void setInterpolator(int tagId, Interpolator i) {
        ChildAnim childAnim = getChildAnimByTagId(tagId);
        if (childAnim != null) {
            childAnim.mInterpolator = i;
        }
    }

    @Override
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * 指定id获取插值器
     */
    @Nullable
    public Interpolator getInterpolator(int tagId) {
        ChildAnim childAnim = getChildAnimByTagId(tagId);
        return childAnim == null ? null : childAnim.mInterpolator;
    }

    /**
     * 默认返回{@link #mEndAnim}的计算值,如需指定动画进度，使用{@link #getAnimProcess(int)}获取
     */
    @Override
    public float getAnimProcess() {
        if (!inAnimTime()) return 1.0f;
        float fraction = (mAnimProcessTime - mEndAnim.mAnimStartTime) / (float) mEndAnim.mDuration;
        if (mEndAnim.mInterpolator == null) {
            return Math.max(Math.min(fraction, 1.0f), 0.0f);
        }
        return mEndAnim.mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));
    }

    /**
     * 指定id获取动画进度
     */
    public float getAnimProcess(int tagId) {
        ChildAnim childAnim = getChildAnimByTagId(tagId);
        if (!inAnimTime(childAnim)) return 1.0f;

        float fraction = (mAnimProcessTime - childAnim.mAnimStartTime) / (float) childAnim.mDuration;
        if (childAnim.mInterpolator == null) {
            return Math.max(Math.min(fraction, 1.0f), 0.0f);
        }
        return childAnim.mInterpolator.getInterpolation(Math.max(Math.min(fraction, 1.0f), 0.0f));

    }

    /**
     * 开始执行动画
     * 如果指定的动画没有结束的情况下调用该函数将以新的动画周期开始
     *
     * @param tagId 动画标记
     */
    public void startAnim(int tagId, long duration) {
        if (duration < 0) {
            return;
        }
        ChildAnim childAnim = getChildAnimByTagId(tagId);
        if (childAnim == null) {
            childAnim = new ChildAnim();
            childAnim.mTagId = tagId;
            mAnims.add(childAnim);
        }
        childAnim.mAnimStartTime = System.currentTimeMillis();
        childAnim.mDuration = duration;
        childAnim.mAnimEndTime = childAnim.mAnimStartTime + duration;
        if (childAnim.mInterpolator == null && mInterpolator != null) {
            childAnim.mInterpolator = mInterpolator;
        }
        updateFinalDuration();
        if (isAttachedParentPortLayout() && !inAnimationManager()) {
            mDataProvider.getChartAnimation().addAnim(this);
        }
    }

    /**
     * 停止指定id的动画
     */
    public void stopAnim(int tagId) {
        ChildAnim childAnim = getChildAnimByTagId(tagId);
        if (inAnimTime(childAnim)) {
            childAnim.mAnimEndTime = System.currentTimeMillis();
            childAnim.mDuration = childAnim.mAnimEndTime - childAnim.mAnimStartTime;
            updateFinalDuration();
            if (!inAnimTime() && inAnimationManager()) {
                mDataProvider.getChartAnimation().removeAnim(this);
            }
        }
    }

    /**
     * 停止所有动画
     */
    public void stopAllAnim() {
        long currTime = System.currentTimeMillis();
        ChildAnim childAnim;
        for (int i = 0; i < mAnims.size(); i++) {
            childAnim = mAnims.get(i);
            if (inAnimTime(childAnim)) {
                childAnim.mAnimEndTime = currTime;
                childAnim.mDuration = childAnim.mAnimEndTime - childAnim.mAnimStartTime;
            }
        }
        updateFinalDuration();
        if (inAnimationManager()) {
            mDataProvider.getChartAnimation().removeAnim(this);
        }
    }

    /**
     * 释放不在动画范围内的资源
     */
    public void releaseNotInAnim() {
        long currTime = System.currentTimeMillis();
        ChildAnim childAnim;
        for (int i = 0; i < mAnims.size(); i++) {
            childAnim = mAnims.get(i);
            if (childAnim.mAnimEndTime < currTime) {
                mAnims.remove(i);
                i--;
            }
        }
    }

    /**
     * 更新最终时长
     */
    private void updateFinalDuration() {
        ChildAnim childAnim;
        for (int i = 0; i < mAnims.size(); i++) {
            childAnim = mAnims.get(i);
            if (mStartAnim == null) {
                mStartAnim = childAnim;
            } else if (mStartAnim.mAnimStartTime > childAnim.mAnimStartTime) {
                mStartAnim = childAnim;
            }
            if (mEndAnim == null) {
                mEndAnim = childAnim;
            } else {
                if (mEndAnim.mAnimEndTime < childAnim.mAnimEndTime) {
                    mEndAnim = childAnim;
                }
            }
        }
    }

    @Nullable
    private ChildAnim getChildAnimByTagId(int tagId) {
        for (int i = 0; i < mAnims.size(); i++) {
            if (mAnims.get(i).mTagId == tagId) {
                return mAnims.get(i);
            }
        }
        return null;
    }

    private static class ChildAnim {
        int mTagId;
        long mAnimStartTime;
        long mDuration;
        long mAnimEndTime;
        Interpolator mInterpolator;
    }
}