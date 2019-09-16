package com.kdl.coversation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 聊天界面的输入框及面板的控制
 */
public class ChatInputManager implements KeyboardWatcher.OnKeyboardToggleListener {
    private static final String KEY_BOARD_HEIGHT = "keyboard_height";
    private Activity mActivity;
    private InputMethodManager mInputMethodManager;
    private SharedPreferences mSp;
    private RecyclerView mMsgList;
    private View mInputArea;
    private ImageView mIvInputSwitch;
    private ImageView mIvMoreFunc;
    private EditText mEtInput;
    private FrameLayout mFlFuncPanel;
    private NoAnimNoScrollVP mVpFunc;
    private int mTempLineCount = 1;
    private TextView mTvSend;


    public ChatInputManager(Activity activity, RecyclerView msgList, View inputArea) {
        mActivity = activity;
        mMsgList = msgList;
        mInputArea = inputArea;
        mInputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        findViews();
        bindViews();
    }

    private void findViews() {
        mIvInputSwitch = mInputArea.findViewById(R.id.iv_input_switch);
        mIvMoreFunc = mInputArea.findViewById(R.id.iv_more_func);
        mEtInput = mInputArea.findViewById(R.id.et_input);
        mFlFuncPanel = mInputArea.findViewById(R.id.fl_func_panel);
        mVpFunc = mInputArea.findViewById(R.id.vp_panel);
        mTvSend = mInputArea.findViewById(R.id.tv_send);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindViews() {
        bindEditText();
        bindQuickReplyButton();
        mTvSend.setOnClickListener(view -> {
            if (mActivity instanceof MainActivity) {
                ((MainActivity) mActivity).onSendClick(mEtInput.getText());
                mEtInput.getText().clear();
            }
        });
        mMsgList.setOnTouchListener((view, motionEvent) -> {
            closeFuncPanel(false);
            closeKeyboard();
            return false;
        });
        KeyboardWatcher watcher = new KeyboardWatcher(mActivity);
        /* 为什么用匿名内部类来实现会导致 KeyboardWatcher 类中的 listener 引用为空，而直接在本类实现接口却不会？*/
        watcher.setListener(this);
    }

    private void bindQuickReplyButton() {
        mIvInputSwitch.setOnClickListener(view -> {
            // 点击快速回复按钮后，设置 ViewPager 的显示条目以展示相应界面
            mEtInput.clearFocus();
            if (mFlFuncPanel.isShown()) {
                lockContentHeight();
                closeFuncPanel(true);
                unlockContentHeightDelayed();

            } else {
                if (isSoftInputShown()) {
                    lockContentHeight();
                    showFuncPanel();
                    unlockContentHeightDelayed();
                } else {
                    showFuncPanel();
                    pullListToEnd();
                }
            }
        });
    }

    private void unlockContentHeightDelayed() {
//        mEtInput.postDelayed(() -> {
//            ((LinearLayout.LayoutParams) mMsgList.getLayoutParams()).weight = 1;
//        }, 200L);
    }

    /**
     * 固定住 recyclerView 的高度，防止因为底部面板消失导致列表高度变化，而引起界面闪动
     */
    private void lockContentHeight() {
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMsgList.getLayoutParams();
//        params.height = mMsgList.getHeight();
//        params.weight = 0;
    }

    private void showFuncPanel() {
        // TODO: 2019-09-11
        int keyboardHeight = getKeyboardHeight();
        if (keyboardHeight == 0) {
            keyboardHeight = (int) SPUtils.get(mActivity, KEY_BOARD_HEIGHT, 600);
        }
        closeKeyboard();
        mFlFuncPanel.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 172, mActivity.getResources().getDisplayMetrics());
        mFlFuncPanel.setVisibility(View.VISIBLE);
    }

    public void closeKeyboard() {
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
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    mIvMoreFunc.setVisibility(View.GONE);
                    mTvSend.setVisibility(View.VISIBLE);
                } else {
                    mIvMoreFunc.setVisibility(View.VISIBLE);
                    mTvSend.setVisibility(View.GONE);
                }
                int lineCount = mEtInput.getLineCount();
                if (lineCount != mTempLineCount) {
                    onInputLineChange();
                    mTempLineCount = lineCount;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtInput.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && mFlFuncPanel.isShown()) {
                lockContentHeight();
                closeFuncPanel(true);
                unlockContentHeightDelayed();
            }
            return false;
        });
    }

    private void onInputLineChange() {
        pullListToEnd();
    }

    private void pullListToEnd() {
        RecyclerView.Adapter adapter = mMsgList.getAdapter();
        if (adapter != null) {
            mMsgList.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    public void closeFuncPanel(boolean showKeyboard) {
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

    @Override
    public void onKeyboardShown(int keyboardSize) {
        pullListToEnd();
    }

    @Override
    public void onKeyboardClosed() {

    }
}
