package com.kdl.coversation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 聊天界面的输入框及面板的控制
 */
public class ChatInputManager {
    private static final String KEY_BOARD_HEIGHT = "keyboard_height";
    private Activity mActivity;
    private InputMethodManager mInputMethodManager;
    private SharedPreferences mSp;
    private RecyclerView mMsgList;
    private View mInputArea;
    private TextView mTvQuickReply;
    private TextView mTvMoreFunc;
    private EditText mEtInput;
    private FrameLayout mFlFuncPanel;
    private NoAnimNoScrollVP mVpFunc;


    public ChatInputManager(Activity activity, RecyclerView msgList, View inputArea) {
        mActivity = activity;
        mMsgList = msgList;
        mInputArea = inputArea;
        mInputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        findViews();
        bindViews();
    }

    private void findViews() {
        mTvQuickReply = mInputArea.findViewById(R.id.tv_quick_reply);
        mTvMoreFunc = mInputArea.findViewById(R.id.tv_more_func);
        mEtInput = mInputArea.findViewById(R.id.et_input);
        mFlFuncPanel = mInputArea.findViewById(R.id.fl_func_panel);
        mVpFunc = mInputArea.findViewById(R.id.vp_panel);
    }

    private void bindViews() {
        bindEditText();
        bindQuickReplyButton();
    }

    private void bindQuickReplyButton() {
        mTvQuickReply.setOnClickListener(view -> {
            // 点击快速回复按钮后，设置 ViewPager 的显示条目以展示相应界面
            if (mFlFuncPanel.isShown()) {
                Toast.makeText(mActivity, "panel shown", Toast.LENGTH_SHORT).show();
            } else {
                if (isSoftInputShown()) {
                    lockListHeight();
                    showFuncPanel();
                    unlockContentHeightDelayed();
                } else {
                    showFuncPanel();
                }
            }
        });
    }

    private void unlockContentHeightDelayed() {
        mEtInput.postDelayed(() -> {
            ((LinearLayout.LayoutParams) mMsgList.getLayoutParams()).weight = 1;
        }, 200L);
    }

    private void lockListHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMsgList.getLayoutParams();
        params.height = mMsgList.getHeight();
        params.weight = 0;
    }

    private void showFuncPanel() {
        // TODO: 2019-09-11
        int keyboardHeight = getKeyboardHeight();
        if (keyboardHeight == 0) {
            keyboardHeight = (int) SPUtils.get(mActivity, KEY_BOARD_HEIGHT, 600);
        }
        closeKeyboard();
        mFlFuncPanel.getLayoutParams().height = keyboardHeight;
        mFlFuncPanel.setVisibility(View.VISIBLE);
    }

    private void closeKeyboard() {
        mInputMethodManager.hideSoftInputFromWindow(mEtInput.getWindowToken(), 0);
    }

    private boolean isSoftInputShown() {
        return getKeyboardHeight() != 0;
    }

    private int getKeyboardHeight() {
        Rect rect = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int keyboardHeight = screenHeight - rect.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            keyboardHeight = keyboardHeight - getNavBarHeight();
        }
        if (keyboardHeight > 0) {
            SPUtils.put(mActivity, KEY_BOARD_HEIGHT, keyboardHeight);
        }
        return keyboardHeight;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getNavBarHeight() {
        Display defaultDisplay = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        defaultDisplay.getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindEditText() {
        if (mEtInput == null) return;
        mEtInput.requestFocus();
        mEtInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mFlFuncPanel.isShown()) {
                    lockListHeight();
                    closeFuncPanel(true);
                    unlockContentHeightDelayed();
                }
                return false;
            }
        });
    }

    private void closeFuncPanel(boolean showKeyboard) {
        if (mFlFuncPanel.isShown()) {
            mFlFuncPanel.setVisibility(View.GONE);
            if (showKeyboard) {
                showKeyboard();
            }
        }
    }

    private void showKeyboard() {
        mEtInput.requestFocus();
        mEtInput.post(() -> mInputMethodManager.showSoftInput(mEtInput, 0));
    }

    public boolean interceptBackPress() {
        if (mFlFuncPanel.isShown()) {
            closeFuncPanel(false);
            return true;
        }
        return false;
    }
}
