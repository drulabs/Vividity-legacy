package org.drulabs.vividvidhi.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.widget.Button;

import org.drulabs.vividvidhi.R;

/**
 * Custom Button widget for application. This must be used where ever android's button widget
 * is required. All button specific customization goes here
 */
public class DruButton extends Button {
    /**
     * An <code>LruCache</code> for previously loaded typefaces.
     */
    private static LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(12);

    @SuppressLint("NewApi")
    public DruButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/RobotoCondensed-BoldItalic.ttf");
        setTypeface(tf);
        // Note: This flag is required for proper typeface rendering
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);


//        ShapeDrawable sd1 = new ShapeDrawable(new RectShape());
        GradientDrawable d1 = new GradientDrawable();
        d1.setShape(GradientDrawable.RECTANGLE);


        d1.setColor(ContextCompat.getColor(context, R.color.colorAccent));

        d1.setCornerRadius(10.0f);

        GradientDrawable d2 = new GradientDrawable();
        d2.setShape(GradientDrawable.RECTANGLE);
        d2.setColor(Color.WHITE);
        d2.setCornerRadius(10.0f);

        StateListDrawable btnBackgroundStates = new StateListDrawable();
        btnBackgroundStates.addState(new int[]{android.R.attr.state_pressed},
                d2);
        btnBackgroundStates.addState(new int[]{android.R.attr.state_focused},
                d1);
        btnBackgroundStates.addState(new int[]{android.R.attr.state_enabled},
                d1);
        btnBackgroundStates.addState(new int[]{android.R.attr.state_activated},
                d1);


        setBackground(btnBackgroundStates);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed}, // enabled
                new int[]{android.R.attr.state_activated}, // activated
                new int[]{android.R.attr.state_focused}, // focused
                new int[]{android.R.attr.state_enabled}  // pressed
        };

        int[] colors = new int[]{
                ContextCompat.getColor(getContext(), R.color.colorAccent),
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
        };

        ColorStateList txtBtnStates = new ColorStateList(states, colors);

        setTextColor(txtBtnStates);
    }
}