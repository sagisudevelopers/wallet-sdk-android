package com.sagisu.vaultLibrary.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sagisu.vaultLibrary.databinding.CoinsListItemBinding;
import com.sagisu.vaultLibrary.models.Coins;

import java.util.List;

public class CoinListAdapter extends RecyclerView.Adapter<CoinListAdapter.MyViewHolder> {
    private Context mContext;
    private List<Coins> mCoins;
    private ICoinSelectionListener listener;

    public interface ICoinSelectionListener {
        void onCoinSelected(Coins coins);
    }

    public CoinListAdapter(Context context, List<Coins> mCoins, ICoinSelectionListener listener) {
        mContext = context;
        this.mCoins = mCoins;
        this.listener = listener;
    }

    @Override
    public CoinListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        CoinsListItemBinding viewProductCategoryBinding = CoinsListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(viewProductCategoryBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Coins coins = mCoins.get(position);

        holder.binding.setModel(coins);
        TooltipCompat.setTooltipText(holder.binding.showDecimalPoints, String.valueOf(coins.getBalance()));
        holder.binding.showDecimalPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.showDecimalPoints.performLongClick();
            }
        });

        /*Picasso.with(mContext).load(popularProductInfo.getProductPhoto())
                .error(R.drawable.icon_pills)
                .into(holder.binding.imgProduct);*/

        holder.binding.coinsItemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onCoinSelected(coins);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mCoins.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CoinsListItemBinding binding;

        public MyViewHolder(CoinsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
