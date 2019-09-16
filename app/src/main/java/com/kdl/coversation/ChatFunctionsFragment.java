package com.kdl.coversation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

class ChatFunctionsFragment extends androidx.fragment.app.Fragment implements View.OnClickListener {

    private ImageView mIvGallery;

    public static Fragment newInstance(int p) {
        ChatFunctionsFragment fragment = new ChatFunctionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("a", p);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_functions, container, false);
        mIvGallery = view.findViewById(R.id.iv_gallery);
        mIvGallery.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_gallery:
                Toast.makeText(getContext(), "pick from gallery", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
