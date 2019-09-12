package com.kdl.coversation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class MainActivity extends AppCompatActivity implements KeyboardWatcher.OnKeyboardToggleListener {
    MultiTypeAdapter mAdapter;
    Items mItems;
    RecyclerView mRvMsgList;
    private LinearLayout mLlInputArea;
    private KeyboardWatcher mKeyboardWatcher;
    private ChatInputManager mChatInputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRvMsgList = findViewById(R.id.rv_msgs);
        mLlInputArea = findViewById(R.id.ll_input_area);
        initRecyclerView();
        initInputArea();
    }

    private void initInputArea() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ChatHandyWordsFragment.newInstance(0, 0));
        fragments.add(new ChatFunctionsFragment());

        mChatInputManager = new ChatInputManager(this, mRvMsgList, mLlInputArea);
        mKeyboardWatcher = new KeyboardWatcher(this);
        mKeyboardWatcher.setListener(this);
    }

    private void initRecyclerView() {
        mRvMsgList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MultiTypeAdapter();
        mItems = new Items();
        mAdapter.register(Message.class)
                .to(new TextMessageItemViewBinder(), new ImageMessageItemViewBinder())
                .withClassLinker((position, message) -> {
                    if (message.getMessageType() == Message.Type.TXT) {
                        return TextMessageItemViewBinder.class;
                    } else if (message.getMessageType() == Message.Type.IMAGE) {
                        return ImageMessageItemViewBinder.class;
                    } else {
                        return TextMessageItemViewBinder.class;
                    }
                });

        mItems.add(new Message("领导我们事业的核心力量是中国共产党。指导我们思想的理论基础是马克思列宁主义。", Message.Type.TXT, Message.Direct.SEND));
        mItems.add(new Message("既要革命，就要有一个革命的党。没有一个革命的党，没有一个按照马克思列宁主义的革命理论和革命风格建立起来的革命党，就不可能领导工人阶级和广大人民群众战胜帝国主义及其走狗。", Message.Type.TXT, Message.Direct.RECEIVE));
        mItems.add(new Message("没有中国共产党的努力，没有中国共产党人做中国人民的中流砥柱，中国的独立和解放是不可能的，中国的工业化和农业近代化也是不可能的。", Message.Type.TXT, Message.Direct.RECEIVE));
        mItems.add(new Message("中国共产党是全中国人民的领导核心。没有这样的一个核心，社会主义事业就不能胜利。", Message.Type.TXT, Message.Direct.SEND));
        mItems.add(new Message("", Message.Type.IMAGE, Message.Direct.RECEIVE));
        mItems.add(new Message("", Message.Type.IMAGE, Message.Direct.RECEIVE));
        mItems.add(new Message("", Message.Type.IMAGE, Message.Direct.SEND));
        mItems.add(new Message("", Message.Type.IMAGE, Message.Direct.RECEIVE));
        mItems.add(new Message("领导我们事业的核心力量是中国共产党。指导我们思想的理论基础是马克思列宁主义。", Message.Type.TXT, Message.Direct.SEND));
        mItems.add(new Message("既要革命，就要有一个革命的党。没有一个革命的党，没有一个按照马克思列宁主义的革命理论和革命风格建立起来的革命党，就不可能领导工人阶级和广大人民群众战胜帝国主义及其走狗。", Message.Type.TXT, Message.Direct.RECEIVE));
        mItems.add(new Message("没有中国共产党的努力，没有中国共产党人做中国人民的中流砥柱，中国的独立和解放是不可能的，中国的工业化和农业近代化也是不可能的。", Message.Type.TXT, Message.Direct.RECEIVE));
        mItems.add(new Message("中国共产党是全中国人民的领导核心。没有这样的一个核心，社会主义事业就不能胜利。", Message.Type.TXT, Message.Direct.SEND));
        mItems.add(new Message("领导我们事业的核心力量是中国共产党。指导我们思想的理论基础是马克思列宁主义。", Message.Type.TXT, Message.Direct.SEND));
        mItems.add(new Message("既要革命，就要有一个革命的党。没有一个革命的党，没有一个按照马克思列宁主义的革命理论和革命风格建立起来的革命党，就不可能领导工人阶级和广大人民群众战胜帝国主义及其走狗。", Message.Type.TXT, Message.Direct.RECEIVE));
        mItems.add(new Message("没有中国共产党的努力，没有中国共产党人做中国人民的中流砥柱，中国的独立和解放是不可能的，中国的工业化和农业近代化也是不可能的。", Message.Type.TXT, Message.Direct.RECEIVE));
        mItems.add(new Message("中国共产党是全中国人民的领导核心。没有这样的一个核心，社会主义事业就不能胜利。", Message.Type.TXT, Message.Direct.SEND));

        mAdapter.setItems(mItems);
        mRvMsgList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onKeyboardShown(int keyboardSize) {
        mRvMsgList.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onKeyboardClosed() {

    }

    @Override
    protected void onDestroy() {
        if (mKeyboardWatcher != null) {
            mKeyboardWatcher.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mChatInputManager.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    public void onSendClick(Editable text) {
        mItems.add(new Message(text.toString(), Message.Type.TXT, Message.Direct.SEND));
        mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        mRvMsgList.scrollToPosition(mAdapter.getItemCount()-1);
    }

    public void onNewMessage(Message message) {

    }
}
