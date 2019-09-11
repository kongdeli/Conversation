package com.kdl.coversation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <pre>
 *     author : linzheng
 *     e-mail : 1007687534@qq.com
 *     time   : 2017/08/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TextMessageItemViewBinder extends BaseMessageItemViewBinder<TextMessageItemViewBinder.TextMessageViewHolder> {


    @Override
    protected void onBindContentViewHolder(TextMessageViewHolder holder, Message item) {
        holder.itemView.setOnClickListener(view -> Toast.makeText(holder.itemView.getContext(), item.getContent(), Toast.LENGTH_SHORT).show());
        TextView tvMsg = holder.itemView.findViewById(R.id.tv_msg);
        tvMsg.setText(item.getContent());
    }

    @Override
    protected ContentViewHolder onCreateContentViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new TextMessageViewHolder(inflater.inflate(R.layout.item_text_message, parent, false));
    }

    public static class TextMessageViewHolder extends BaseMessageItemViewBinder.ContentViewHolder {

        public TextMessageViewHolder(View itemView) {
            super(itemView);
        }
    }


}
