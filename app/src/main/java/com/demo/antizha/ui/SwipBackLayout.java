package com.demo.antizha.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.viewpager.widget.ViewPager;

import com.demo.antizha.R;

import java.util.LinkedList;
import java.util.List;

/* loaded from: classes3.dex */
public class SwipBackLayout extends FrameLayout {
    private View mContentView;
    private final int O;
    private int P;
    private int b0;
    private int c0;
    private final Scroller d0;
    private int width;
    private boolean f0;
    private boolean g0;
    private boolean mEnable;
    private final Activity mActivity;
    private final List<ViewPager> j0;
    private final Drawable mShadowLeft;

    public SwipBackLayout(Context context) {
        this(context, null);
    }

    public static SwipBackLayout create(Activity activity) {
        return new SwipBackLayout(activity);
    }

    private void c() {
        int scrollX = this.mContentView.getScrollX();
        this.d0.startScroll(this.mContentView.getScrollX(), 0, -scrollX, 0, Math.abs(scrollX));
        postInvalidate();
    }

    private void d() {
        int scrollX = this.width + this.mContentView.getScrollX();
        this.d0.startScroll(this.mContentView.getScrollX(), 0, (-scrollX) + 1, 0, Math.abs(scrollX));
        postInvalidate();
    }

    private void setContentView(View view) {
        this.mContentView = (View) view.getParent();
    }

    public boolean enable() {
        return this.mEnable;
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.d0.computeScrollOffset()) {
            this.mContentView.scrollTo(this.d0.getCurrX(), this.d0.getCurrY());
            postInvalidate();
            if (this.d0.isFinished() && this.g0) {
                this.mActivity.finish();
            }
        }
    }

    @Override // android.view.View, android.view.ViewGroup
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mShadowLeft != null && this.mContentView != null) {
            int left = this.mContentView.getLeft() - this.mShadowLeft.getIntrinsicWidth();
            this.mShadowLeft.setBounds(left, this.mContentView.getTop(), this.mContentView.getLeft(), this.mContentView.getBottom());
            this.mShadowLeft.draw(canvas);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mEnable) {
            return false;
        }
        ViewPager a2 = a(this.j0, motionEvent);
        if (a2 != null && a2.getCurrentItem() != 0) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            int rawX = (int) motionEvent.getRawX();
            this.c0 = rawX;
            this.P = rawX;
            this.b0 = (int) motionEvent.getRawY();
        } else if (action == 2 && ((int) motionEvent.getRawX()) - this.P > this.O && Math.abs(((int) motionEvent.getRawY()) - this.b0) < this.O) {
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.widget.FrameLayout, android.view.View, android.view.ViewGroup
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.width = getWidth();
            a(this.j0, this);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mEnable) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 1) {
            this.f0 = false;
            if (this.mContentView.getScrollX() <= (-this.width) / 2) {
                this.g0 = true;
                d();
            } else {
                c();
                this.g0 = false;
            }
        } else if (action == 2) {
            int rawX = (int) motionEvent.getRawX();
            int i2 = this.c0 - rawX;
            this.c0 = rawX;
            if (rawX - this.P > this.O && Math.abs(((int) motionEvent.getRawY()) - this.b0) < this.O) {
                this.f0 = true;
            }
            if (rawX - this.P >= 0 && this.f0) {
                this.mContentView.scrollBy(i2, 0);
            }
        }
        return true;
    }

    public void setInterEvent(boolean enable) {
        this.mEnable = enable;
    }

    public SwipBackLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public void init() {
        ViewGroup decor = (ViewGroup) this.mActivity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }

    public SwipBackLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.mEnable = false;
        this.j0 = new LinkedList();
        this.mActivity = (Activity) context;
        this.O = ViewConfiguration.get(context).getScaledTouchSlop();
        this.d0 = new Scroller(context);
        this.mShadowLeft = getResources().getDrawable(R.drawable.swip_left_shadow, null);
    }

    private void a(List<ViewPager> list, ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewPager) {
                list.add((ViewPager) childAt);
            } else if (childAt instanceof ViewGroup) {
                a(list, (ViewGroup) childAt);
            }
        }
    }

    private ViewPager a(List<ViewPager> list, MotionEvent motionEvent) {
        if (!(list == null || list.size() == 0)) {
            Rect rect = new Rect();
            for (ViewPager viewPager : list) {
                viewPager.getHitRect(rect);
                if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return viewPager;
                }
            }
        }
        return null;
    }
}
