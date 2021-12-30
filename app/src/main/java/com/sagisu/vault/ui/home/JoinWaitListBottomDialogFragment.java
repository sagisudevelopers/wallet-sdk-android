package com.sagisu.vault.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sagisu.vault.R;
import com.sagisu.vault.models.WaitList;
import com.sagisu.vault.network.APIError;
import com.sagisu.vault.network.Result;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.utils.FeatureNameDescriptor;
import com.sagisu.vault.utils.ProgressShimmer;
import com.sagisu.vault.utils.Util;

public class JoinWaitListBottomDialogFragment extends BottomSheetDialogFragment {
    private final String featureName;
    View view;

    public static JoinWaitListBottomDialogFragment newInstance(@FeatureNameDescriptor.FeatureNameTypes String featureName) {
        return new JoinWaitListBottomDialogFragment(featureName);
    }

    public JoinWaitListBottomDialogFragment(@FeatureNameDescriptor.FeatureNameTypes String featureName) {
        this.featureName = featureName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.join_waitlist_sheet, container,
                false);

        view.findViewById(R.id.join_waitlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading("Joining waitlist", true);
                NetworkRepository.getInstance().joinWaitLists(featureName).observe(getViewLifecycleOwner(), new Observer<Result<WaitList>>() {
                    @Override
                    public void onChanged(Result<WaitList> waitListResult) {
                        loading(null, false);
                        if (waitListResult instanceof Result.Success) {
                            waitListJoinSuccess(((Result.Success<WaitList>) waitListResult).getData());
                            dismiss();
                        } else if (waitListResult instanceof Result.Error) {
                            APIError apiError = ((Result.Error) waitListResult).getError();
                            Util.showSnackBar(apiError.message(), getActivity());
                            //Toast.makeText(getActivity(), apiError.message(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;

    }

    public void waitListJoinSuccess(WaitList waitList) {
        SuccessBottomDialogFragment successBottomDialogFragment =
                SuccessBottomDialogFragment.newInstance(waitList);
        successBottomDialogFragment.show(getActivity().getSupportFragmentManager(),
                "success_waitlist");
    }

    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(view.findViewById(R.id.waitlist_loading), view.findViewById(R.id.view_waitlist), text, loading);
    }
}
