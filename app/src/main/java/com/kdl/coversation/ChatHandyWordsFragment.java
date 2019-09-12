package com.kdl.coversation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class ChatHandyWordsFragment extends Fragment {
    public static ChatHandyWordsFragment newInstance(Object p1, Object p2) {
        ChatHandyWordsFragment fragment = new ChatHandyWordsFragment();
        Bundle bundle = new Bundle();
        // TODO: 2019-09-12 put params
        fragment.setArguments(bundle);
        return fragment;
    }


}
