package com.benben.kchartlib.data;

/**
 * @日期 : 2020/8/24
 * @描述 :
 */
public class PaddingHelper {

    private boolean mLeftLimitPaddingFollowScale = false;
    private int mLeftLimitPadding = 0;
    private boolean mLeftExtPaddingFollowScale = false;
    private int mLeftExtPadding = 0;
    private boolean mLeftOverPaddingFollowScale = false;
    private int mLeftOverPadding = 0;

    private boolean mRightLimitPaddingFollowScale = false;
    private int mRightLimitPadding = 0;
    private boolean mRightExtPaddingFollowScale = false;
    private int mRightExtPadding = 0;
    private boolean mRightOverPaddingFollowScale = false;
    private int mRightOverPadding = 0;

    public boolean isLeftLimitPaddingFollowScale() {
        return mLeftLimitPaddingFollowScale;
    }

    public int getLeftLimitPadding() {
        return mLeftLimitPadding;
    }

    public void setLeftLimitPadding(int leftLimitPadding, boolean leftLimitPaddingFollowScale) {
        mLeftLimitPadding = leftLimitPadding;
        mLeftLimitPaddingFollowScale = leftLimitPaddingFollowScale;
    }

    public boolean isLeftExtPaddingFollowScale() {
        return mLeftExtPaddingFollowScale;
    }

    public int getLeftExtPadding() {
        return mLeftExtPadding;
    }

    public void setLeftExtPadding(int leftExtPadding, boolean leftExtPaddingFollowScale) {
        mLeftExtPadding = leftExtPadding;
        mLeftExtPaddingFollowScale = leftExtPaddingFollowScale;
    }

    public boolean isLeftOverPaddingFollowScale() {
        return mLeftOverPaddingFollowScale;
    }

    public int getLeftOverPadding() {
        return mLeftOverPadding;
    }

    public void setLeftOverPadding(int leftOverPadding, boolean leftOverPaddingFollowScale) {
        mLeftOverPadding = leftOverPadding;
        mLeftOverPaddingFollowScale = leftOverPaddingFollowScale;
    }

    public boolean isRightLimitPaddingFollowScale() {
        return mRightLimitPaddingFollowScale;
    }

    public int getRightLimitPadding() {
        return mRightLimitPadding;
    }

    public void setRightLimitPadding(int rightLimitPadding, boolean rightLimitPaddingFollowScale) {
        mRightLimitPadding = rightLimitPadding;
        mRightLimitPaddingFollowScale = rightLimitPaddingFollowScale;
    }

    public boolean isRightExtPaddingFollowScale() {
        return mRightExtPaddingFollowScale;
    }

    public int getRightExtPadding() {
        return mRightExtPadding;
    }

    public void setRightExtPadding(int rightExtPadding, boolean rightExtPaddingFollowScale) {
        mRightExtPadding = rightExtPadding;
        mRightExtPaddingFollowScale = rightExtPaddingFollowScale;
    }

    public boolean isRightOverPaddingFollowScale() {
        return mRightOverPaddingFollowScale;
    }

    public int getRightOverPadding() {
        return mRightOverPadding;
    }

    public void setRightOverPadding(int rightOverPadding, boolean rightOverPaddingFollowScale) {
        mRightOverPadding = rightOverPadding;
        mRightOverPaddingFollowScale = rightOverPaddingFollowScale;
    }
}
