package com.benben.kchartlib.data;

/**
 * @日期 : 2020/8/24
 * @描述 :
 */
public class PaddingHelper {

    private float mBufferRightExtPaddingScale;
    private int mBufferRightExtPadding = 0;
    private boolean mRightExtPaddingFollowScale = false;    // 右扩展边界是否跟随缩放
    private int mRightExtPadding = 0;                       // 右扩展边界

    private float mBufferLeftExtPaddingScale;
    private int mBufferLeftExtPadding = 0;
    private boolean mLeftExtPaddingFollowScale = false;    // 左扩展边界是否跟随缩放
    private int mLeftExtPadding = 0;                       // 左扩展边界

    public boolean isRightExtPaddingFollowScale() {
        return mRightExtPaddingFollowScale;
    }

    public boolean hasRightExtPadding() {
        return mRightExtPadding != 0;
    }

    public void setRightExtPadding(int rightExtPadding, boolean rightExtPaddingFollowScale) {
        mRightExtPadding = rightExtPadding;
        mRightExtPaddingFollowScale = rightExtPaddingFollowScale;
    }

    public int getRightExtPadding(float scale) {
        if (mRightExtPaddingFollowScale) {
            if (mBufferRightExtPaddingScale == scale) {
                return mBufferRightExtPadding;
            }
            mBufferRightExtPaddingScale = scale;
            return mBufferRightExtPadding = Math.round(mRightExtPadding * scale);
        }
        return mRightExtPadding;
    }

    public boolean isLeftExtPaddingFollowScale() {
        return mLeftExtPaddingFollowScale;
    }

    public boolean hasLeftExtPadding() {
        return mLeftExtPadding != 0;
    }

    public void setLeftExtPadding(int leftExtPadding, boolean leftExtPaddingFollowScale) {
        mLeftExtPadding = leftExtPadding;
        mLeftExtPaddingFollowScale = leftExtPaddingFollowScale;
    }

    public int getLeftExtPadding(float scale) {
        if (mLeftExtPaddingFollowScale) {
            if (mBufferLeftExtPaddingScale == scale) {
                return mBufferLeftExtPadding;
            }
            mBufferLeftExtPaddingScale = scale;
            return mBufferLeftExtPadding = Math.round(mLeftExtPadding * scale);
        }
        return mLeftExtPadding;
    }
}
