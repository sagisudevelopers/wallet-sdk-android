package com.sagisu.vault.ui.kyc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.DvPersonalInfoBinding;
import com.sagisu.vault.utils.DateValidatorDob;

import java.util.Calendar;
import java.util.TimeZone;


public class ReviewPersonalInfoFragment extends Fragment {

    private DocumentVerificationViewModel mViewModel;
    private DvPersonalInfoBinding binding;

    public static ReviewPersonalInfoFragment newInstance() {
        return new ReviewPersonalInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dv_personal_info_fragment, container, false);
        binding = DataBindingUtil.bind(view);
        /*binding.kycDob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });*/
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(DocumentVerificationViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        setDobDatePicker();

        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<KycFormError>() {
            @Override
            public void onChanged(KycFormError kycFirstname) {
                binding.kycFirstname.setError(kycFirstname.getFirstNameError() == null ? null : getString(kycFirstname.getFirstNameError()));
                binding.kycFirstname.setErrorEnabled(kycFirstname.getFirstNameError() != null);

                binding.kycLastname.setError(kycFirstname.getLastNameError() == null ? null : getString(kycFirstname.getLastNameError()));
                binding.kycLastname.setErrorEnabled(kycFirstname.getLastNameError() != null);

                binding.kycDob.setError(kycFirstname.getDobError() == null ? null : getString(kycFirstname.getDobError()));
                binding.kycDob.setErrorEnabled(kycFirstname.getDobError() != null);

                binding.kycEmail.setError(kycFirstname.getEmailError() == null ? null : getString(kycFirstname.getEmailError()));
                binding.kycEmail.setErrorEnabled(kycFirstname.getEmailError() != null);

            }
        });

        mViewModel.getEnableVerificationFields().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("shimmer")) {
                    binding.documentVerification.smoothScrollTo(0, binding.documentVerification.getHeight());
                    //binding.documentVerification.fullScroll(View.FOCUS_DOWN);
                }
            }
        });
    }

    private void setDobDatePicker() {
        // now create instance of the material date picker
        // builder make sure to add the "datePicker" which
        // is normal material date picker which is the first
        // type of the date picker in material design date
        // picker
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();//Enable only previous dates
        //Validate for 18 years mark
        CalendarConstraints.Builder constraintsBuilder =
                new CalendarConstraints.Builder().setValidator(new DateValidatorDob());


        // now define the properties of the
        // materialDateBuilder that is title text as SELECT A DATE
        materialDateBuilder.setTitleText("SELECT A DATE");
        if (mViewModel.getDob() != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(mViewModel.getDob());
            materialDateBuilder.setSelection(calendar);
        }
        materialDateBuilder.setCalendarConstraints(constraintsBuilder.build());

        // now create the instance of the material date
        // picker
        final MaterialDatePicker materialDatePicker = materialDateBuilder
                .build();

        // handle select date button which opens the
        // material design date picker
        binding.kycDob.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getSupportFragmentManager() to
                // interact with the fragments
                // associated with the material design
                // date picker tag is to get any error
                // in logcat
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            }
        });

        // now handle the positive button click from the
        // material design date picker
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Long selection) {

                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        /*Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        calendar.setTimeInMillis(selection);*/
                        binding.kycDob.getEditText().setText(materialDatePicker.getHeaderText());
                        mViewModel.setDob(selection);

                        //mShowSelectedDateText.setText("Selected Date is : " + materialDatePicker.getHeaderText());
                        // in the above statement, getHeaderText
                        // is the selected date preview from the
                        // dialog
                    }
                });
    }
}