package com.example.flowledger.ui.navigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.TypedValue;
import androidx.core.graphics.drawable.DrawableCompat;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.dynamicanimation.animation.FloatPropertyCompat;

import com.example.flowledger.R;

public class LiquidNavBar extends View {

    private Paint backgroundPaint;
    private Paint blobPaint;
    private Path blobPath;

    private float blobX = 0f;
    private float targetBlobX = 0f;

    private SpringAnimation springAnimation;
    
    private Drawable[] icons;
    private int totalTabs = 5;
    private OnTabSelectedListener listener;

    public interface OnTabSelectedListener {
        void onTabSelected(int index);
    }

    public LiquidNavBar(Context context) {
        super(context);
        init();
    }

    public LiquidNavBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }

    private void init() {
        TypedValue typedValue = new TypedValue();
        Context context = getContext();

        // Get glass surface color
        int bgColor = Color.parseColor("#252525");
        if (context.getTheme().resolveAttribute(R.attr.glassSurfaceColor, typedValue, true)) {
            bgColor = typedValue.data;
        }

        // Get primary color for blob
        int primaryColor = Color.parseColor("#4F378B");
        if (context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)) {
            primaryColor = typedValue.data;
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(bgColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        blobPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blobPaint.setColor(primaryColor);
        blobPaint.setStyle(Paint.Style.FILL);

        blobPath = new Path();

        icons = new Drawable[]{
                ContextCompat.getDrawable(getContext(), R.drawable.ic_home),
                ContextCompat.getDrawable(getContext(), R.drawable.ic_search),
                ContextCompat.getDrawable(getContext(), R.drawable.ic_add),
                ContextCompat.getDrawable(getContext(), R.drawable.ic_insights),
                ContextCompat.getDrawable(getContext(), R.drawable.ic_profile)
        };

        FloatPropertyCompat<LiquidNavBar> BLOB_X_PROPERTY = new FloatPropertyCompat<LiquidNavBar>("blobX") {
            @Override
            public float getValue(LiquidNavBar object) {
                return object.blobX;
            }

            @Override
            public void setValue(LiquidNavBar object, float value) {
                object.blobX = value;
                object.invalidate();
            }
        };

        springAnimation = new SpringAnimation(this, BLOB_X_PROPERTY);
        SpringForce springForce = new SpringForce();
        springForce.setStiffness(SpringForce.STIFFNESS_LOW);
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        springAnimation.setSpring(springForce);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (blobX == 0f) {
            float tabWidth = w / (float) totalTabs;
            blobX = tabWidth / 2f;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        // Draw pill background
        canvas.drawRoundRect(0, 0, w, h, h / 2f, h / 2f, backgroundPaint);

        // Draw blob
        blobPath.reset();
        float radius = h / 2f - 16f; // padding
        blobPath.addCircle(blobX, h / 2f, radius, Path.Direction.CW);
        canvas.drawPath(blobPath, blobPaint);

        // Draw icons
        float tabWidth = w / (float) totalTabs;
        int iconSize = (int) (h * 0.4f);
        
        TypedValue textValue = new TypedValue();
        int iconTintColor = Color.WHITE;
        if (getContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, textValue, true)) {
            iconTintColor = textValue.data;
        }

        for (int i = 0; i < totalTabs; i++) {
            float centerX = (i * tabWidth) + (tabWidth / 2f);
            float centerY = h / 2f;
            
            Drawable icon = icons[i];
            if (icon != null) {
                int left = (int) (centerX - iconSize / 2f);
                int top = (int) (centerY - iconSize / 2f);
                icon.setBounds(left, top, left + iconSize, top + iconSize);
                
                // Tint icon based on proximity to blob or theme
                Drawable wrapIcon = DrawableCompat.wrap(icon.mutate());
                if (Math.abs(centerX - blobX) < (tabWidth / 2f)) {
                    DrawableCompat.setTint(wrapIcon, Color.WHITE); // High contrast on blob
                } else {
                    DrawableCompat.setTint(wrapIcon, iconTintColor);
                }
                wrapIcon.draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float tabWidth = getWidth() / (float) totalTabs;
            int clickedTab = (int) (event.getX() / tabWidth);
            if (clickedTab >= 0 && clickedTab < totalTabs) {
                if (listener != null) {
                    listener.onTabSelected(clickedTab);
                }
                return true;
            }
        }
        return true;
    }

    public void animateToTab(int tabIndex, int totalTabs) {
        if (getWidth() == 0) {
            post(() -> animateToTab(tabIndex, totalTabs));
            return;
        }
        float tabWidth = getWidth() / (float) totalTabs;
        targetBlobX = (tabIndex * tabWidth) + (tabWidth / 2f);
        
        springAnimation.animateToFinalPosition(targetBlobX);
    }
}
