package android.support.v7.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class ActionBarDrawerToggle implements DrawerListener {
    private final Delegate mActivityImpl;
    private final int mCloseDrawerContentDescRes;
    private boolean mDrawerIndicatorEnabled;
    private final DrawerLayout mDrawerLayout;
    private boolean mHasCustomUpIndicator;
    private Drawable mHomeAsUpIndicator;
    private final int mOpenDrawerContentDescRes;
    private DrawerToggle mSlider;
    private OnClickListener mToolbarNavigationClickListener;
    private boolean mWarnedForDisplayHomeAsUp;

    /* renamed from: android.support.v7.app.ActionBarDrawerToggle.1 */
    class C00921 implements OnClickListener {
        C00921() {
        }

        public void onClick(View v) {
            if (ActionBarDrawerToggle.this.mDrawerIndicatorEnabled) {
                ActionBarDrawerToggle.this.toggle();
            } else if (ActionBarDrawerToggle.this.mToolbarNavigationClickListener != null) {
                ActionBarDrawerToggle.this.mToolbarNavigationClickListener.onClick(v);
            }
        }
    }

    public interface Delegate {
        Context getActionBarThemedContext();

        Drawable getThemeUpIndicator();

        boolean isNavigationVisible();

        void setActionBarDescription(int i);

        void setActionBarUpIndicator(Drawable drawable, int i);
    }

    public interface DelegateProvider {
        @Nullable
        Delegate getDrawerToggleDelegate();
    }

    interface DrawerToggle {
        float getPosition();

        void setPosition(float f);
    }

    interface TmpDelegateProvider {
        @Nullable
        Delegate getV7DrawerToggleDelegate();
    }

    static class DrawerArrowDrawableToggle extends DrawerArrowDrawable implements DrawerToggle {
        private final Activity mActivity;

        public DrawerArrowDrawableToggle(Activity activity, Context themedContext) {
            super(themedContext);
            this.mActivity = activity;
        }

        public void setPosition(float position) {
            if (position == 1.0f) {
                setVerticalMirror(true);
            } else if (position == 0.0f) {
                setVerticalMirror(false);
            }
            super.setProgress(position);
        }

        boolean isLayoutRtl() {
            return ViewCompat.getLayoutDirection(this.mActivity.getWindow().getDecorView()) == 1;
        }

        public float getPosition() {
            return super.getProgress();
        }
    }

    static class DummyDelegate implements Delegate {
        final Activity mActivity;

        DummyDelegate(Activity activity) {
            this.mActivity = activity;
        }

        public void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes) {
        }

        public void setActionBarDescription(int contentDescRes) {
        }

        public Drawable getThemeUpIndicator() {
            return null;
        }

        public Context getActionBarThemedContext() {
            return this.mActivity;
        }

        public boolean isNavigationVisible() {
            return true;
        }
    }

    private static class HoneycombDelegate implements Delegate {
        final Activity mActivity;
        SetIndicatorInfo mSetIndicatorInfo;

        private HoneycombDelegate(Activity activity) {
            this.mActivity = activity;
        }

        public Drawable getThemeUpIndicator() {
            return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(this.mActivity);
        }

        public Context getActionBarThemedContext() {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                return actionBar.getThemedContext();
            }
            return this.mActivity;
        }

        public boolean isNavigationVisible() {
            ActionBar actionBar = this.mActivity.getActionBar();
            return (actionBar == null || (actionBar.getDisplayOptions() & 4) == 0) ? false : true;
        }

        public void setActionBarUpIndicator(Drawable themeImage, int contentDescRes) {
            this.mActivity.getActionBar().setDisplayShowHomeEnabled(true);
            this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(this.mSetIndicatorInfo, this.mActivity, themeImage, contentDescRes);
            this.mActivity.getActionBar().setDisplayShowHomeEnabled(false);
        }

        public void setActionBarDescription(int contentDescRes) {
            this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarDescription(this.mSetIndicatorInfo, this.mActivity, contentDescRes);
        }
    }

    private static class JellybeanMr2Delegate implements Delegate {
        final Activity mActivity;

        private JellybeanMr2Delegate(Activity activity) {
            this.mActivity = activity;
        }

        public Drawable getThemeUpIndicator() {
            TypedArray a = getActionBarThemedContext().obtainStyledAttributes(null, new int[]{16843531}, 16843470, 0);
            Drawable result = a.getDrawable(0);
            a.recycle();
            return result;
        }

        public Context getActionBarThemedContext() {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                return actionBar.getThemedContext();
            }
            return this.mActivity;
        }

        public boolean isNavigationVisible() {
            ActionBar actionBar = this.mActivity.getActionBar();
            return (actionBar == null || (actionBar.getDisplayOptions() & 4) == 0) ? false : true;
        }

        public void setActionBarUpIndicator(Drawable drawable, int contentDescRes) {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(drawable);
                actionBar.setHomeActionContentDescription(contentDescRes);
            }
        }

        public void setActionBarDescription(int contentDescRes) {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeActionContentDescription(contentDescRes);
            }
        }
    }

    static class ToolbarCompatDelegate implements Delegate {
        final CharSequence mDefaultContentDescription;
        final Drawable mDefaultUpIndicator;
        final Toolbar mToolbar;

        ToolbarCompatDelegate(Toolbar toolbar) {
            this.mToolbar = toolbar;
            this.mDefaultUpIndicator = toolbar.getNavigationIcon();
            this.mDefaultContentDescription = toolbar.getNavigationContentDescription();
        }

        public void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes) {
            this.mToolbar.setNavigationIcon(upDrawable);
            setActionBarDescription(contentDescRes);
        }

        public void setActionBarDescription(int contentDescRes) {
            if (contentDescRes == 0) {
                this.mToolbar.setNavigationContentDescription(this.mDefaultContentDescription);
            } else {
                this.mToolbar.setNavigationContentDescription(contentDescRes);
            }
        }

        public Drawable getThemeUpIndicator() {
            return this.mDefaultUpIndicator;
        }

        public Context getActionBarThemedContext() {
            return this.mToolbar.getContext();
        }

        public boolean isNavigationVisible() {
            return true;
        }
    }

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        this(activity, null, drawerLayout, null, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        this(activity, toolbar, drawerLayout, null, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    <T extends Drawable & DrawerToggle> ActionBarDrawerToggle(Activity activity, Toolbar toolbar, DrawerLayout drawerLayout, T slider, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        this.mDrawerIndicatorEnabled = true;
        this.mWarnedForDisplayHomeAsUp = false;
        if (toolbar != null) {
            this.mActivityImpl = new ToolbarCompatDelegate(toolbar);
            toolbar.setNavigationOnClickListener(new C00921());
        } else if (activity instanceof DelegateProvider) {
            this.mActivityImpl = ((DelegateProvider) activity).getDrawerToggleDelegate();
        } else if (activity instanceof TmpDelegateProvider) {
            this.mActivityImpl = ((TmpDelegateProvider) activity).getV7DrawerToggleDelegate();
        } else if (VERSION.SDK_INT >= 18) {
            this.mActivityImpl = new JellybeanMr2Delegate(null);
        } else if (VERSION.SDK_INT >= 11) {
            this.mActivityImpl = new HoneycombDelegate(null);
        } else {
            this.mActivityImpl = new DummyDelegate(activity);
        }
        this.mDrawerLayout = drawerLayout;
        this.mOpenDrawerContentDescRes = openDrawerContentDescRes;
        this.mCloseDrawerContentDescRes = closeDrawerContentDescRes;
        if (slider == null) {
            this.mSlider = new DrawerArrowDrawableToggle(activity, this.mActivityImpl.getActionBarThemedContext());
        } else {
            this.mSlider = (DrawerToggle) slider;
        }
        this.mHomeAsUpIndicator = getThemeUpIndicator();
    }

    public void syncState() {
        if (this.mDrawerLayout.isDrawerOpen((int) GravityCompat.START)) {
            this.mSlider.setPosition(1.0f);
        } else {
            this.mSlider.setPosition(0.0f);
        }
        if (this.mDrawerIndicatorEnabled) {
            setActionBarUpIndicator((Drawable) this.mSlider, this.mDrawerLayout.isDrawerOpen((int) GravityCompat.START) ? this.mCloseDrawerContentDescRes : this.mOpenDrawerContentDescRes);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (!this.mHasCustomUpIndicator) {
            this.mHomeAsUpIndicator = getThemeUpIndicator();
        }
        syncState();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null || item.getItemId() != 16908332 || !this.mDrawerIndicatorEnabled) {
            return false;
        }
        toggle();
        return true;
    }

    private void toggle() {
        if (this.mDrawerLayout.isDrawerVisible((int) GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer((int) GravityCompat.START);
        } else {
            this.mDrawerLayout.openDrawer((int) GravityCompat.START);
        }
    }

    public void setHomeAsUpIndicator(Drawable indicator) {
        if (indicator == null) {
            this.mHomeAsUpIndicator = getThemeUpIndicator();
            this.mHasCustomUpIndicator = false;
        } else {
            this.mHomeAsUpIndicator = indicator;
            this.mHasCustomUpIndicator = true;
        }
        if (!this.mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
        }
    }

    public void setHomeAsUpIndicator(int resId) {
        Drawable indicator = null;
        if (resId != 0) {
            indicator = this.mDrawerLayout.getResources().getDrawable(resId);
        }
        setHomeAsUpIndicator(indicator);
    }

    public boolean isDrawerIndicatorEnabled() {
        return this.mDrawerIndicatorEnabled;
    }

    public void setDrawerIndicatorEnabled(boolean enable) {
        if (enable != this.mDrawerIndicatorEnabled) {
            if (enable) {
                setActionBarUpIndicator((Drawable) this.mSlider, this.mDrawerLayout.isDrawerOpen((int) GravityCompat.START) ? this.mCloseDrawerContentDescRes : this.mOpenDrawerContentDescRes);
            } else {
                setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
            }
            this.mDrawerIndicatorEnabled = enable;
        }
    }

    public void onDrawerSlide(View drawerView, float slideOffset) {
        this.mSlider.setPosition(Math.min(1.0f, Math.max(0.0f, slideOffset)));
    }

    public void onDrawerOpened(View drawerView) {
        this.mSlider.setPosition(1.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mCloseDrawerContentDescRes);
        }
    }

    public void onDrawerClosed(View drawerView) {
        this.mSlider.setPosition(0.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mOpenDrawerContentDescRes);
        }
    }

    public void onDrawerStateChanged(int newState) {
    }

    public OnClickListener getToolbarNavigationClickListener() {
        return this.mToolbarNavigationClickListener;
    }

    public void setToolbarNavigationClickListener(OnClickListener onToolbarNavigationClickListener) {
        this.mToolbarNavigationClickListener = onToolbarNavigationClickListener;
    }

    void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes) {
        if (!(this.mWarnedForDisplayHomeAsUp || this.mActivityImpl.isNavigationVisible())) {
            Log.w("ActionBarDrawerToggle", "DrawerToggle may not show up because NavigationIcon is not visible. You may need to call actionbar.setDisplayHomeAsUpEnabled(true);");
            this.mWarnedForDisplayHomeAsUp = true;
        }
        this.mActivityImpl.setActionBarUpIndicator(upDrawable, contentDescRes);
    }

    void setActionBarDescription(int contentDescRes) {
        this.mActivityImpl.setActionBarDescription(contentDescRes);
    }

    Drawable getThemeUpIndicator() {
        return this.mActivityImpl.getThemeUpIndicator();
    }
}
