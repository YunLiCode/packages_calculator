/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calculator2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.LinearLayout;

public class Calculator extends Activity implements PanelSwitcher.Listener,
        Logic.Listener, OnClickListener,
        /* M:#No Physical Key# OnClickListener, */OnMenuItemClickListener {
    EventListener mListener = new EventListener();
    private CalculatorDisplay mDisplay;
    private Persist mPersist;
    private History mHistory;
    private Logic mLogic;
    private ViewPager mPager;
    private View mClearButton;
    private View mBackspaceButton;
    private View mOverflowMenuButton;

    // private TextView mDisplayView;
    private Button mSwitchAdvanceButton;
    private Button mGoBackButton;
    private ColorButton mHistoryButton;

    private View mSimplePage;
    private View mAdvancedPage;

    static final int BASIC_PANEL = 0;
    static final int ADVANCED_PANEL = 1;

    private static final String LOG_TAG = "Calculator";
    private static final boolean DEBUG = false;
    private static final boolean LOG_ENABLED = false;
    private static final String STATE_CURRENT_VIEW = "state-current-view";

    private int mOrientation;

    // /M: @{
    private static Context sContext;

    // /@}

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // /M: save context @{
        sContext = getApplication();
        // /@}

        // Disable IME for this application
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        mOrientation = getResources().getConfiguration().orientation;

        setContentView(R.layout.main);

        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            initPortView();
        } else {
            initLandView();
        }

        mHistoryButton = (ColorButton) findViewById(R.id.show_history_bt);
        mHistoryButton.setOnClickListener(this);
        if (mClearButton == null) {
            mClearButton = findViewById(R.id.clear);
            mClearButton.setOnClickListener(mListener);
            mClearButton.setOnLongClickListener(mListener);
        }
        if (mBackspaceButton == null) {
            mBackspaceButton = findViewById(R.id.del);
            mBackspaceButton.setOnClickListener(mListener);
            mBackspaceButton.setOnLongClickListener(mListener);
        }

        //add by shenqi for no navigator hard key layout

        if(mOrientation == Configuration.ORIENTATION_PORTRAIT) {
             adjustPortView();
        }
        //add by shenqi end

        mPersist = new Persist(this);
        mPersist.load();

        mHistory = mPersist.history;

        // mDisplayView = (TextView)findViewById(R.id.display1);
        mDisplay = (CalculatorDisplay) findViewById(R.id.display);

        mLogic = new Logic(this, mHistory, mDisplay);
        // TODO
        mLogic.setListener(this);

        mLogic.setDeleteMode(mPersist.getDeleteMode());
        mLogic.setLineLength(mDisplay.getMaxDigits());

        HistoryAdapter historyAdapter = new HistoryAdapter(this, mHistory,
                mLogic);
        mHistory.setObserver(historyAdapter);

        if (mPager != null) {
            mPager.setCurrentItem(state == null ? 0 : state.getInt(
                    STATE_CURRENT_VIEW, 0));
        }

        mListener.setHandler(mLogic, mPager);
        mDisplay.setOnKeyListener(mListener);
        // TODO

        /*
         * ///M: #No Physical Key# no need to create fake menu in case
         * sdk-version is 10 if
         * (!ViewConfiguration.get(this).hasPermanentMenuKey()) {
         * createFakeMenu(); }
         */

        mLogic.resumeWithHistory();
        updateDeleteMode();
        
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        registerReceiver(screenBcr, intentFilter);
    }
    
    @Override
    protected void onStart() {
//        shouldClear = true;
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void adjustPortView() {
        Log.d(LOG_TAG,"hasPermanentMenuKey = " + ViewConfiguration.get(this).hasPermanentMenuKey());
        if (!ViewConfiguration.get(this).hasPermanentMenuKey()) {

            View mTempView;
            int leftMarginOffset = getResources().
                    getDimensionPixelSize(R.dimen.no_permanent_menu_key_offset);
            int advancedleftMarginOffset = getResources().
                    getDimensionPixelSize(R.dimen.no_permanent_menu_largekey_offset);
            
            
            LinearLayout.LayoutParams lp =
                    (LinearLayout.LayoutParams) mBackspaceButton.getLayoutParams();
            lp.setMargins(lp.leftMargin + dip2px(leftMarginOffset), lp.topMargin, lp.rightMargin, lp.bottomMargin);
            mBackspaceButton.setLayoutParams(lp);

            lp = (LinearLayout.LayoutParams) mClearButton.getLayoutParams();
            lp.setMargins(lp.leftMargin + dip2px(leftMarginOffset), lp.topMargin, lp.rightMargin, lp.bottomMargin);
            mClearButton.setLayoutParams(lp);

            
            lp = (LinearLayout.LayoutParams) mSwitchAdvanceButton.getLayoutParams();
            lp.setMargins(lp.leftMargin + dip2px(leftMarginOffset), lp.topMargin, lp.rightMargin, lp.bottomMargin);
            mSwitchAdvanceButton.setLayoutParams(lp);

            lp = (LinearLayout.LayoutParams) mGoBackButton.getLayoutParams();
            lp.setMargins(lp.leftMargin + dip2px(leftMarginOffset), lp.topMargin, lp.rightMargin, lp.bottomMargin);
            mGoBackButton.setLayoutParams(lp);

            lp = (LinearLayout.LayoutParams) mHistoryButton.getLayoutParams();
            lp.setMargins(lp.leftMargin + dip2px(leftMarginOffset), lp.topMargin, lp.rightMargin, lp.bottomMargin);
            mHistoryButton.setLayoutParams(lp);


            final Resources res = getResources();
            final TypedArray simpleButtons = res
                    .obtainTypedArray(R.array.simple_buttons);
      
            for (int i = 0; i < simpleButtons.length(); i++) {
                mTempView = mSimplePage.findViewById(simpleButtons.getResourceId(i, 0)); 
                lp = (LinearLayout.LayoutParams) mTempView.getLayoutParams();
                lp.setMargins(lp.leftMargin + dip2px(leftMarginOffset), lp.topMargin, lp.rightMargin, lp.bottomMargin);
                mTempView.setLayoutParams(lp);
            }

            final TypedArray advancedButtons = res
                    .obtainTypedArray(R.array.advanced_buttons);
      
            for (int i = 0; i < advancedButtons.length(); i++) {
                mTempView = mAdvancedPage.findViewById(advancedButtons.getResourceId(i, 0)); 
                lp = (LinearLayout.LayoutParams) mTempView.getLayoutParams();
                lp.setMargins(lp.leftMargin + dip2px(advancedleftMarginOffset), lp.topMargin, lp.rightMargin, lp.bottomMargin);
                mTempView.setLayoutParams(lp);
            }
            
            
        }
    }

    private int dip2px(float dpValue) {  
        final float scale = getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
    private void initPortView() {
        mPager = (ViewPager) findViewById(R.id.panelswitch);
        if (mPager != null) {
            mPager.setAdapter(new PageAdapter(mPager));
        } else {
            // Single page UI
            final TypedArray buttons = getResources().obtainTypedArray(
                    R.array.buttons);
            for (int i = 0; i < buttons.length(); i++) {
                setOnClickListener(null, buttons.getResourceId(i, 0));
            }
            buttons.recycle();
        }

        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (arg0 == 2) {
                    if (!getBasicVisibility()) {
                        mGoBackButton.setVisibility(View.VISIBLE);
                        mSwitchAdvanceButton.setVisibility(View.GONE);
                    }
                    if (!getAdvancedVisibility()) {
                        mGoBackButton.setVisibility(View.GONE);
                        mSwitchAdvanceButton.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        mSwitchAdvanceButton = (Button) findViewById(R.id.switch_advance_bt);
        mSwitchAdvanceButton.setOnClickListener(this);
        mGoBackButton = (Button) findViewById(R.id.goback_bt);
        mGoBackButton.setOnClickListener(this);
    }

    private void initLandView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation != mOrientation) {
//            shouldClear = false;
            mOrientation = newConfig.orientation;

            setContentView(R.layout.main);

            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                initPortView();
            } else {
                initLandView();
            }

//            shouldClear = true;
        }

        super.onConfigurationChanged(newConfig);
    }

    private void updateDeleteMode() {
        if (mLogic.getDeleteMode() == Logic.DELETE_MODE_BACKSPACE) {
            // mClearButton.setVisibility(View.GONE);
            mBackspaceButton.setVisibility(View.VISIBLE);
        } else {
            mClearButton.setVisibility(View.VISIBLE);
            // mBackspaceButton.setVisibility(View.GONE);
        }
    }

    void setOnClickListener(View root, int id) {
        final View target = root != null ? root.findViewById(id)
                : findViewById(id);
        target.setOnClickListener(mListener);
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // super.onCreateOptionsMenu(menu);
    // getMenuInflater().inflate(R.menu.menu, menu);
    // return true;
    // }

    // @Override
    // public boolean onPrepareOptionsMenu(Menu menu) {
    // super.onPrepareOptionsMenu(menu);
    //
    // if (!sContext.getResources().getBoolean(R.bool.isTablet)) {
    // menu.findItem(R.id.basic).setVisible(!getBasicVisibility());
    // menu.findItem(R.id.advanced).setVisible(!getAdvancedVisibility());
    // if (!getBasicVisibility()) {
    // mGoBackButton.setVisibility(View.VISIBLE);
    // mSwitchAdvanceButton.setVisibility(View.GONE);
    // }
    // if (!getAdvancedVisibility()) {
    // mGoBackButton.setVisibility(View.GONE);
    // mSwitchAdvanceButton.setVisibility(View.VISIBLE);
    // }
    //
    // } else {
    // menu.findItem(R.id.basic).setVisible(false);
    // menu.findItem(R.id.advanced).setVisible(false);
    // }
    // return true;
    // }

    /*
     * ///M: #No Physical Key# no need to create fake menu in case sdk version
     * is 10
     * 
     * private void createFakeMenu() { mOverflowMenuButton =
     * findViewById(R.id.overflow_menu); if (mOverflowMenuButton != null) {
     * mOverflowMenuButton.setVisibility(View.VISIBLE);
     * mOverflowMenuButton.setOnClickListener(this); } }
     * 
     * @Override public void onClick(View v) { switch (v.getId()) { case
     * R.id.overflow_menu: PopupMenu menu = constructPopupMenu(); if (menu !=
     * null) { menu.show(); } break; } }
     */

    private PopupMenu constructPopupMenu() {
        final PopupMenu popupMenu = new PopupMenu(this, mOverflowMenuButton);
        final Menu menu = popupMenu.getMenu();
        popupMenu.inflate(R.menu.menu);
        popupMenu.setOnMenuItemClickListener(this);
        onPrepareOptionsMenu(menu);
        return popupMenu;
    }

     @Override
     public boolean onMenuItemClick(MenuItem item) {
     return onOptionsItemSelected(item);
     }

    private boolean getBasicVisibility() {
        return mPager != null && mPager.getCurrentItem() == BASIC_PANEL;
    }

    private boolean getAdvancedVisibility() {
        return mPager != null && mPager.getCurrentItem() == ADVANCED_PANEL;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (mPager != null) {
            state.putInt(STATE_CURRENT_VIEW, mPager.getCurrentItem());
        }
    }

    @Override
    public void onPause() {
        mLogic.updateHistory();
        mPersist.setDeleteMode(mLogic.getDeleteMode());
        mPersist.save();
        super.onPause();
    }
    
//    private boolean shouldClear = true;
//    @Override
//    protected void onStop() {
//        
//        if(shouldClear){
////            mLogic.onClear();
//            Log.d("Calculator", "clear");
//        }
//        super.onStop();
//    }
//    
//    private BroadcastReceiver screenBcr = new BroadcastReceiver() {
//        
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(intent.ACTION_SCREEN_OFF)){
//                shouldClear = false;
//                Log.d("Calculator", "onreceive set false");
//            }
//        }
//    };
    
//    protected void onDestroy() {
//        unregisterReceiver(screenBcr);
//        super.onDestroy();
//    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT
                && keyCode == KeyEvent.KEYCODE_BACK && getAdvancedVisibility()) {
            mPager.setCurrentItem(BASIC_PANEL);
            return true;
        } else {
            return super.onKeyDown(keyCode, keyEvent);
        }
    }

    static void log(String message) {
        if (LOG_ENABLED) {
            Log.v(LOG_TAG, message);
        }
    }

    @Override
    public void onChange() {
        invalidateOptionsMenu();
    }

    @Override
    public void onDeleteModeChange() {
        updateDeleteMode();
    }

    // /M:vibrate@{
    public static void vibrate() {
        Vibrator vibrator = (Vibrator) sContext
                .getSystemService(sContext.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[] { 100, 100 }, -1);
        } else {
            log("Device not have vibrator");
        }
    }

    // /@}

    class PageAdapter extends PagerAdapter {


        public PageAdapter(ViewPager parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent
                    .getContext());
            final View simplePage = inflater.inflate(R.layout.simple_pad,
                    parent, false);
            final View advancedPage = inflater.inflate(R.layout.advanced_pad,
                    parent, false);
            mSimplePage = simplePage;
            mAdvancedPage = advancedPage;

            final Resources res = getResources();
            final TypedArray simpleButtons = res
                    .obtainTypedArray(R.array.simple_buttons);
            for (int i = 0; i < simpleButtons.length(); i++) {
                setOnClickListener(simplePage,
                        simpleButtons.getResourceId(i, 0));
            }
            simpleButtons.recycle();

            final TypedArray advancedButtons = res
                    .obtainTypedArray(R.array.advanced_buttons);
            for (int i = 0; i < advancedButtons.length(); i++) {
                setOnClickListener(advancedPage,
                        advancedButtons.getResourceId(i, 0));
            }
            advancedButtons.recycle();

            final View clearButton = simplePage.findViewById(R.id.clear);
            if (clearButton != null) {
                mClearButton = clearButton;
            }

            final View backspaceButton = simplePage.findViewById(R.id.del);
            if (backspaceButton != null) {
                mBackspaceButton = backspaceButton;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void startUpdate(View container) {
        }

        @Override
        public Object instantiateItem(View container, int position) {
            final View page = position == 0 ? mSimplePage : mAdvancedPage;
            ((ViewGroup) container).addView(page);
            return page;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewGroup) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.switch_advance_bt:
            if (!getAdvancedVisibility()) {
                mPager.setCurrentItem(ADVANCED_PANEL);

                mGoBackButton.setVisibility(View.VISIBLE);
                mSwitchAdvanceButton.setVisibility(View.GONE);
            }
            break;
        case R.id.goback_bt:

            if (!getBasicVisibility()) {
                mPager.setCurrentItem(BASIC_PANEL);
                mGoBackButton.setVisibility(View.GONE);
                mSwitchAdvanceButton.setVisibility(View.VISIBLE);
            }
            break;
        case R.id.show_history_bt:
            showHistory();
            break;
        default:
            break;
        }
    }

    private void showHistory() {
        startActivity(new Intent(Calculator.this, HistoryActivity.class));
    }
}
