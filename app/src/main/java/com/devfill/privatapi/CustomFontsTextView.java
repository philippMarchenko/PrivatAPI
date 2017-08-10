package com.devfill.privatapi;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontsTextView extends TextView {
    public CustomFontsTextView(Context context) {
        super(context);
    }

    public CustomFontsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFonts(attrs,context);
    }

    public CustomFontsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFonts(attrs,context);
    }

    public CustomFontsTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFonts(attrs,context);
    }


    private void setFonts(AttributeSet attributeSet, Context context){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.CustomFontsTextView,
                0, 0);
        a.recycle();
        int fonts = a.getInt(R.styleable.CustomFontsTextView_fonts,-1);
        SingletonFonts singltonFonts = SingletonFonts.getInstance(context);
        switch (fonts){
            case (0):
                setTypeface(singltonFonts.getFont1());
                break;
            case (1):
                setTypeface(singltonFonts.getFont2());
                break;
            case (2):
                setTypeface(singltonFonts.getFont3());
                break;
        }
    }
}