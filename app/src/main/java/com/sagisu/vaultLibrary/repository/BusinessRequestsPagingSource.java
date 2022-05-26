package com.sagisu.vaultLibrary.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.sagisu.vaultLibrary.models.BusinessRequest;
import com.sagisu.vaultLibrary.models.PaginationResponse;
import com.sagisu.vaultLibrary.network.VaultApiInterface;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BusinessRequestsPagingSource extends RxPagingSource<Integer, BusinessRequest> {
    @NonNull
    private VaultApiInterface mBackend;
    private String mQuery;
    private String mBusinessId;

    public BusinessRequestsPagingSource(@NonNull VaultApiInterface backend,
                                        String businessId) {
        mBackend = backend;
        mBusinessId = businessId;
    }

    @NotNull
    @Override
    public Single<LoadResult<Integer, BusinessRequest>> loadSingle(
            @NotNull LoadParams<Integer> params) {
        // Start refresh at page 1 if undefined.
        Integer nextPageNumber = params.getKey();
        if (nextPageNumber == null) {
            nextPageNumber = 1;
            //mCursor = null;
        } else {
            //mCursor = new SharedPref(context).getTransactionCursor();
        }

        return mBackend.getAllBusinessRequests(mBusinessId,nextPageNumber,mQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new)
                .doOnError(err -> {
                    Log.e("error", err.getMessage());
                });

    }

    private LoadResult<Integer, BusinessRequest> toLoadResult(
            @NonNull PaginationResponse response) {

        return new LoadResult.Page<>(
                response.getData(),
                null, // Only paging forward.
                response.getNextPageNumber(),
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, BusinessRequest> state) {
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

        LoadResult.Page<Integer, BusinessRequest> anchorPage = state.closestPageToPosition(anchorPosition);
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
