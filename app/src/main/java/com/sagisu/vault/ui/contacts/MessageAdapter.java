package com.sagisu.vault.ui.contacts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.sagisu.vault.databinding.MsgItemLayoutBinding;
import com.sagisu.vault.models.MessageBean;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<MessageBean> messageBeanList;
    private LayoutInflater inflater;
    private IMessageClickListener listener;

    public interface IMessageClickListener {
        void onMessageClicked(MessageBean messageBean);
    }

    public MessageAdapter(Context context, List<MessageBean> messageBeanList, IMessageClickListener listener) {
        inflater = ((Activity) context).getLayoutInflater();
        this.messageBeanList = messageBeanList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MsgItemLayoutBinding itemBinding = MsgItemLayoutBinding.inflate(layoutInflater, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemBinding, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MessageBean bean = messageBeanList.get(position);
        holder.bindTo(bean);
    }

    @Override
    public int getItemCount() {
        return messageBeanList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private MsgItemLayoutBinding binding;
        private IMessageClickListener listener;

        MyViewHolder(MsgItemLayoutBinding binding, IMessageClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        public void bindTo(MessageBean messageBean) {

            binding.setMessageBean(messageBean);
            binding.messageRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMessageClicked(messageBean);
                }
            });

        }
    }
}
