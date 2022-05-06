package com.sagisu.vaultLibrary.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.models.PaginationResponse;
import com.sagisu.vaultLibrary.network.VaultApiInterface;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BusinessPagingSource extends RxPagingSource<Integer, Business> {
    @NonNull
    private VaultApiInterface mBackend;
    private String mQuery;

    public BusinessPagingSource(@NonNull VaultApiInterface backend,
                                String query) {
        mBackend = backend;
        mQuery = query;
    }

    @NotNull
    @Override
    public Single<LoadResult<Integer, Business>> loadSingle(
            @NotNull LoadParams<Integer> params) {
        // Start refresh at page 1 if undefined.
        Integer nextPageNumber = params.getKey();
        if (nextPageNumber == null) {
            nextPageNumber = 1;
            //mCursor = null;
        } else {
            //mCursor = new SharedPref(context).getTransactionCursor();
        }

        return mBackend.getBusiness(nextPageNumber,mQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new)
                .doOnError(err -> {
                    Log.e("error", err.getMessage());
                });

    }

    private LoadResult<Integer, Business> toLoadResult(
            @NonNull PaginationResponse response) {

       /* // new SharedPref(context).setTransactionCursor(response.getCursor());
        List<Business> businessList = new ArrayList<>();
        Business business1 = new Business("AAAAAA");
        Business business2 = new Business("BBBBBB");
        Business business3 = new Business("CCCCCC");
        Business business4 = new Business("DDDDDD");
        if (mCursor.isEmpty() || business1.getName().contains(mCursor))
            businessList.add(business1);
        if (mCursor.isEmpty() ||business2.getName().contains(mCursor))
            businessList.add(business2);
        if (mCursor.isEmpty() ||business3.getName().contains(mCursor))
            businessList.add(business3);
        if (mCursor.isEmpty() ||business4.getName().contains(mCursor))
            businessList.add(business4);*/

        return new LoadResult.Page<>(
                response.getData(),
                null, // Only paging forward.
                response.getNextPageNumber(),
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, Business> state) {
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

        LoadResult.Page<Integer, Business> anchorPage = state.closestPageToPosition(anchorPosition);
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
