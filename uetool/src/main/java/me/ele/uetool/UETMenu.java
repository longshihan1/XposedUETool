package me.ele.uetool;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.jakewharton.scalpel.ScalpelFrameLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static me.ele.uetool.MenuHelper.Type.TYPE_LAYOUT_LEVEL;
import static me.ele.uetool.MenuHelper.Type.TYPE_UNKNOWN;

public class UETMenu extends LinearLayout {

    private View vMenu;
    private ViewGroup vSubMenuContainer;
    private ValueAnimator animator;
    private Interpolator defaultInterpolator = new AccelerateDecelerateInterpolator();
    private List<UETSubMenu.SubMenu> subMenus = new ArrayList<>();

    private WindowManager windowManager;
    private WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    private int touchSlop;
    private int y;
    public static final String ACTION="com.longshihan.uetooltaichi.xposed";

    public UETMenu(final Context context, int y) {
        super(context);
        inflate(context, R.layout.uet_menu_layout, this);
        setGravity(Gravity.CENTER_VERTICAL);

        this.y = y;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        vMenu = findViewById(R.id.menu);
        vSubMenuContainer = findViewById(R.id.sub_menu_container);
        Resources resources = context.getResources();
        subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_catch_view), R.drawable.uet_edit_attr, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION);
                intent.putExtra("type",MenuHelper.Type.TYPE_EDIT_ATTR);
                context.sendBroadcast(intent);
            }
        }));
        subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_relative_location), R.drawable.uet_relative_position,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ACTION);
                        intent.putExtra("type", MenuHelper.Type.TYPE_RELATIVE_POSITION);
                        context.sendBroadcast(intent);
                    }
                }));
        subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_grid), R.drawable.uet_show_gridding,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ACTION);
                        intent.putExtra("type", MenuHelper.Type.TYPE_SHOW_GRIDDING);
                        context.sendBroadcast(intent);
                    }
                }));

        subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_scalpel), R.drawable.uet_scalpel, new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ACTION);
                intent.putExtra("type", TYPE_LAYOUT_LEVEL);
                context.sendBroadcast(intent);
            }
        }));

        for (UETSubMenu.SubMenu subMenu : subMenus) {
            UETSubMenu uetSubMenu = new UETSubMenu(getContext());
            uetSubMenu.update(subMenu);
            vSubMenuContainer.addView(uetSubMenu);
        }

        vMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnim();
            }
        });

        vMenu.setOnTouchListener(new View.OnTouchListener() {
            private float downX, downY;
            private float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        lastY = downY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.y += event.getRawY() - lastY;
                        params.y = Math.max(0, params.y);
                        windowManager.updateViewLayout(UETMenu.this, params);
                        lastY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(event.getRawX() - downX) < touchSlop && Math.abs(event.getRawY() - downY) < touchSlop) {
                            try {
                                Field field = View.class.getDeclaredField("mListenerInfo");
                                field.setAccessible(true);
                                Object object = field.get(vMenu);
                                field = object.getClass().getDeclaredField("mOnClickListener");
                                field.setAccessible(true);
                                object = field.get(object);
                                if (object != null && object instanceof View.OnClickListener) {
                                    ((View.OnClickListener) object).onClick(vMenu);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void startAnim() {
        ensureAnim();
        final boolean isOpen = vSubMenuContainer.getTranslationX() <= -vSubMenuContainer.getWidth();
        animator.setInterpolator(isOpen ? defaultInterpolator : new ReverseInterpolator(defaultInterpolator));
        animator.removeAllListeners();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                vSubMenuContainer.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isOpen) {
                    vSubMenuContainer.setVisibility(GONE);
                }
            }
        });
        animator.start();
    }

    private void ensureAnim() {
        if (animator == null) {
            animator = ValueAnimator.ofInt(-vSubMenuContainer.getWidth(), 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    vSubMenuContainer.setTranslationX((int) animation.getAnimatedValue());
                }
            });
            animator.setDuration(400);
        }
    }

    private void open() {
        open(TYPE_UNKNOWN);
    }

    public static void open(int type) {
        if (type==TYPE_LAYOUT_LEVEL){
            showLayoutLevel();
            return;
        }
        Activity currentTopActivity = UETool.getInstance().getTargetActivity();
        Log.d("UETMenu", "currentTopActivity: " + (currentTopActivity == null));
        if (currentTopActivity == null) {
            Log.d("UETMenu","currentTopActivity 为空");
            return;
        }else if (UETMenu.dismiss(currentTopActivity)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(MenuHelper.EXTRA_TYPE, type);
        MenuHelper.show(currentTopActivity, bundle);
    }

    private static void showLayoutLevel() {
        try {
            Activity activity=Util.getCurrentActivity();
            if (activity==null){
                Log.d("TTTTTTT","activity 为空");
                return;
            }
            Log.d("TTTTTTT",activity.getLocalClassName());
            Window window=activity.getWindow();
            if (window==null){
                Log.d("TTTTTTT","window 为空");
                return;
            }
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            ViewGroup content = decorView.findViewById(android.R.id.content);
            View contentChild = content.getChildAt(0);
            if (contentChild != null) {
                if (contentChild instanceof ScalpelFrameLayout) {
                    content.removeAllViews();
                    View originContent = ((ScalpelFrameLayout) contentChild).getChildAt(0);
                    ((ScalpelFrameLayout) contentChild).removeAllViews();
                    content.addView(originContent);
                } else {
                    content.removeAllViews();
                    ScalpelFrameLayout frameLayout = new ScalpelFrameLayout(activity);
                    frameLayout.setLayerInteractionEnabled(true);
                    frameLayout.setDrawIds(true);
                    frameLayout.addView(contentChild);
                    content.addView(frameLayout);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void show() {
        try {
            windowManager.addView(this, getWindowLayoutParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int dismiss() {
        try {
            windowManager.removeView(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params.y;
    }

    public static boolean dismiss(Activity currentTopActivity) {
      return MenuHelper.dismiss(currentTopActivity);
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 10;
        params.y = y;
        return params;
    }

    private static class ReverseInterpolator implements TimeInterpolator {

        private TimeInterpolator mWrappedInterpolator;

        ReverseInterpolator(TimeInterpolator interpolator) {
            mWrappedInterpolator = interpolator;
        }

        @Override
        public float getInterpolation(float input) {
            return mWrappedInterpolator.getInterpolation(Math.abs(input - 1f));
        }
    }
}
