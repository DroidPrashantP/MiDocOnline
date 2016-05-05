package com.midoconline.app.Util.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.midoconline.app.R;


/**
 * Material like progressbar for all devices<br/>
 * REF : <u>https://github.com/castorflex/SmoothProgressBar<u/>
 * @author Prashant
 */
public class CircularProgressView extends View {

  private CircularProgressDrawable mDrawable;

  public CircularProgressView(Context context) {
    this(context, null);
  }

  public CircularProgressView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
      float width = 3; // Add the width you want
      int unit = TypedValue.COMPLEX_UNIT_DIP; // In the units you want it
      // Remember this is the ring's line, not the whole view.
      float result = TypedValue.applyDimension(unit, width, getResources().getDisplayMetrics());
      mDrawable = new CircularProgressDrawable(getResources().getColor(R.color.accent), result);
      mDrawable.setCallback(this);
      mDrawable.start();
  }

  @Override
  protected void onVisibilityChanged(View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
      if (visibility == VISIBLE) {
      mDrawable.start();
    } else {
      mDrawable.stop();
    }
  }



  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mDrawable.setBounds(0, 0, w, h);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    mDrawable.draw(canvas);
  }

  @Override
  protected boolean verifyDrawable(Drawable who) {
    return who == mDrawable || super.verifyDrawable(who);
  }
}