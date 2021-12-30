package com.sagisu.vault.utils;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.sagisu.vault.R;

public class ProgressShimmer {
    public static void shimmerLoading(View shimmerLayout, View mainLayout, String loadingText, boolean visibility) {
        shimmerLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mainLayout.setVisibility(visibility ? View.GONE : View.VISIBLE);
        ShimmerFrameLayout container = (ShimmerFrameLayout) shimmerLayout;
        if (visibility) {
            AppCompatTextView loadingTextView = shimmerLayout.findViewById(R.id.loading_text);
            loadingTextView.setText(loadingText);
            container.startShimmer();
        }
    }

}
