package com.sagisu.vaultLibrary.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import com.sagisu.vaultLibrary.models.Coins;
import com.sagisu.vaultLibrary.network.VaultApiInterface;
import com.sagisu.vaultLibrary.network.VaultServerResponse;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CryptoTokensPagingSource extends RxPagingSource<Integer, Coins> {
    @NonNull
    private VaultApiInterface mBackend;
    private String mCursor;

    public CryptoTokensPagingSource(@NonNull VaultApiInterface backend,
                                    String cursor) {
        mBackend = backend;
        mCursor = cursor;
        //Globals.getAppComponent().inject(this);
    }

    @NotNull
    @Override
    public Single<LoadResult<Integer, Coins>> loadSingle(
            @NotNull LoadParams<Integer> params) {
        // Start refresh at page 1 if undefined.
        Integer nextPageNumber = params.getKey();
        if (nextPageNumber == null) {
            nextPageNumber = 1;
            mCursor = null;
        } else {
            //mCursor = new SharedPref(context).getTransactionCursor();
        }

        return mBackend.getCryptoTokens(nextPageNumber/*, mCursor*/)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new)
                .doOnError(err -> {
                    Log.e("error", err.getMessage());
                });
    }

    private LoadResult<Integer, Coins> toLoadResult(
            @NonNull VaultServerResponse<List<Coins>> response) {

       // new SharedPref(context).setTransactionCursor(response.getCursor());

        return new LoadResult.Page<>(
                response.getData(),
                null, // Only paging forward.
                response.getNextPageNumber(),
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, Coins> state) {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<Integer, Coins> anchorPage = state.closestPageToPosition(anchorPosition);
        if (anchorPage == null) {
            return null;
        }

        Integer prevKey = anchorPage.getPrevKey();
        if (prevKey != null) {
            return prevKey + 1;
        }

        Integer nextKey = anchorPage.getNextKey();
        if (nextKey != null) {
            return nextKey - 1;
        }

        return null;
    }
}
