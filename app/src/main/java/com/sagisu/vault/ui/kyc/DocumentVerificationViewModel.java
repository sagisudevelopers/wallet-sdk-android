package com.sagisu.vault.ui.kyc;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.sagisu.vault.R;
import com.sagisu.vault.network.VaultAPIError;
import com.sagisu.vault.network.VaultApiClient;
import com.sagisu.vault.network.VaultApiInterface;
import com.sagisu.vault.network.VaultResult;
import com.sagisu.vault.network.VaultServerResponse;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.ui.OTP.Otp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class DocumentVerificationViewModel extends ViewModel {
    private final MutableLiveData<Integer> pageNoObservable = new MutableLiveData<>(1);
    private final MutableLiveData<KycBean> kycBeanObservable = new MutableLiveData<>(new KycBean());
    private final MutableLiveData<KycFormError> formError = new MutableLiveData<>(new KycFormError());
    private final MutableLiveData<String> loadingObservable = new MutableLiveData<>(null);
    private final MediatorLiveData<Boolean> kycPostStatus = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> kycScanDetails = new MediatorLiveData<>();
    private final MediatorLiveData<String> enableVerificationFields = new MediatorLiveData<>();
    private final MutableLiveData<String> verifiedEmail = new MutableLiveData<>(null);
    private MediatorLiveData<KycScanResult> documentScanResult = new MediatorLiveData<>();
    private final Integer totalPages = 4;
    private boolean validateFields = true;
    private ObservableField<String> kycScanStatus = new ObservableField();

    public enum KycScanResult {
        PENDING,
        AWAITING,
        VERIFIED
    }

    public void init() {
        getScanDetails();
    }

    private void getScanDetails() {
        loadingObservable.setValue("Getting kyc scan details");
        LiveData<VaultResult<KycScanResultBean>> liveData = NetworkRepository.getInstance().getKycScanDetails();
        documentScanResult.addSource(liveData, new Observer<VaultResult<KycScanResultBean>>() {
            @Override
            public void onChanged(VaultResult<KycScanResultBean> agentResult) {
                loadingObservable.setValue(null);
                if (agentResult instanceof VaultResult.Success) {
                    KycScanResultBean kycScanResultBean = ((VaultResult.Success<KycScanResultBean>) agentResult).getData();

                    if (kycScanResultBean == null || kycScanResultBean.getDocument() == null || kycScanResultBean.getDocument().getStatus() == null)
                        documentScanResult.setValue(KycScanResult.PENDING);
                    else {
                        kycScanStatus.set(kycScanResultBean.getDocument().getStatus());
                        if (kycScanResultBean.getTransaction().getStatus().equals("PENDING"))
                            documentScanResult.setValue(KycScanResult.AWAITING);
                        else if (kycScanResultBean.getDocument().getStatus().equals("APPROVED_VERIFIED")) {
                            documentScanResult.setValue(KycScanResult.VERIFIED);
                            kycBeanObservable.setValue(kycScanResultBean.getDocument());
                        } else documentScanResult.setValue(KycScanResult.PENDING);
                    }
                } else if (agentResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) agentResult).getError();
                    KycFormError tmpFormError = new KycFormError();
                    tmpFormError.setToastError(vaultApiError.message());
                    formError.setValue(tmpFormError);
                }
            }
        });
    }

    public void goNext() {
        Integer pageNo = pageNoObservable.getValue();
        if (valid(pageNo)) {
            if (pageNo.equals(totalPages)) submitKyc();
            else incrementPage();
        }
    }

    public void onEmailChanged(CharSequence s, int start, int before, int count) {
        String email = s.toString();
        if (email.substring(email.lastIndexOf(".") + 1).equals("com")) {
            //Email is entered fully
            sendEmailVerificationCode(email);
        }
    }

    private void incrementPage() {
        Integer currentPage = pageNoObservable.getValue();
        assert currentPage != null;
        if (currentPage.equals(0)) return;
        if (currentPage.equals(totalPages)) return;
        pageNoObservable.setValue(currentPage + 1);
    }

    private void sendEmailVerificationCode(String email) {
        enableVerificationFields.setValue("shimmer");
        KycBean kycBean = kycBeanObservable.getValue();
        Otp otp = new Otp(email, kycBean.getFirstName());
        VaultApiInterface api = VaultApiClient.buildRetrofitService();
        Call<VaultServerResponse<JsonObject>> call = api.generateOtp(otp);
        call.enqueue(new Callback<VaultServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<VaultServerResponse<JsonObject>> call, Response<VaultServerResponse<JsonObject>> response) {

                if (response.isSuccessful()) {
                    // showOtpPopup();
                    enableVerificationFields.setValue("show");
                } else {
                    VaultAPIError vaultApiError = new VaultResult.Error(response.errorBody()).getError();
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VaultServerResponse<JsonObject>> call, Throwable t) {
                t.printStackTrace();
                //listener.loading(null, false);
                if (t instanceof HttpException) {
                    VaultAPIError vaultApiError = new VaultResult.Error((HttpException) t).getError();
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verifyEmail() {
        KycBean kycBean = kycBeanObservable.getValue();

        assert kycBean != null;
        if (kycBean.getOtpToken() == null || kycBean.getOtpToken().isEmpty()) return;

        VaultApiInterface api = VaultApiClient.buildRetrofitService();
        Call<VaultServerResponse<JsonObject>> call = api.validateOtp(null, kycBean.getEmail(), kycBean.getOtpToken(), null, null);
        call.enqueue(new Callback<VaultServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<VaultServerResponse<JsonObject>> call, Response<VaultServerResponse<JsonObject>> response) {
                //listener.loading(null, false);
                if (response.isSuccessful()) {
                    // showOtpPopup();
                    // mBottomSheetDialog.dismiss();
                    enableVerificationFields.setValue("hide");
                    verifiedEmail.setValue(kycBean.getEmail());
                    KycFormError tmpFormError = formError.getValue();
                    if (tmpFormError.getEmailError() != null) {
                        tmpFormError.setEmailError(null);
                        formError.setValue(tmpFormError);
                    }
                } else {
                    VaultAPIError vaultApiError = new VaultResult.Error(response.errorBody()).getError();
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VaultServerResponse<JsonObject>> call, Throwable t) {
                //listener.loading(null, false);
                t.printStackTrace();
                if (t instanceof HttpException) {
                    VaultAPIError vaultApiError = new VaultResult.Error((HttpException) t).getError();
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void submitKyc() {
        loadingObservable.setValue("Posting kyc");
        LiveData<VaultResult<KycBean>> liveData = NetworkRepository.getInstance().postKyc(kycBeanObservable.getValue());
        kycPostStatus.addSource(liveData, new Observer<VaultResult<KycBean>>() {
            @Override
            public void onChanged(VaultResult<KycBean> agentResult) {
                loadingObservable.setValue(null);
                if (agentResult instanceof VaultResult.Success) {
                    kycPostStatus.setValue(true);
                } else if (agentResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) agentResult).getError();
                    KycFormError tmpFormError = new KycFormError();
                    tmpFormError.setToastError(vaultApiError.message());
                    formError.setValue(tmpFormError);
                }
            }
        });
    }

    public void postKycScanId(String scanID) {
        loadingObservable.setValue("Posting scan details");
        LiveData<VaultResult<JsonObject>> liveData = NetworkRepository.getInstance().postKycScanId(scanID);
        documentScanResult.addSource(liveData, new Observer<VaultResult<JsonObject>>() {
            @Override
            public void onChanged(VaultResult<JsonObject> agentResult) {
                loadingObservable.setValue(null);
                if (agentResult instanceof VaultResult.Success) {
                    documentScanResult.setValue(KycScanResult.AWAITING);
                } else if (agentResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) agentResult).getError();
                    KycFormError tmpFormError = new KycFormError();
                    tmpFormError.setToastError(vaultApiError.message());
                    formError.setValue(tmpFormError);
                }
            }
        });
    }

    private boolean valid(Integer pageNo) {
        boolean flag = true;
        switch (pageNo) {
            case 1:
                flag = validateDocScanInfo();
                break;
            case 2:
                flag = validPersonalInfo();
                break;
            case 3:
                flag = validAddressInfo();
                break;
            case 4:
                flag = validSsnInfo();
                break;
        }

        return flag;
    }

    private boolean validateDocScanInfo() {
        boolean flag = true;
        if (documentScanResult.getValue().equals(KycScanResult.PENDING)) {
            flag = false;
            KycFormError tmpFormError = new KycFormError();
            tmpFormError.setToastError("Please scan the documents to continue");
            formError.setValue(tmpFormError);
        } /*else if (documentScanResult.getValue().equals(KycScanResult.AWAITING)) {
            flag = false;
            KycFormError tmpFormError = new KycFormError();
            tmpFormError.setToastError("Please wait for the scan results");
            formError.setValue(tmpFormError);
        }*/

        return flag;
    }

    private boolean validPersonalInfo() {
        boolean flag = true;
        KycBean kycBean = kycBeanObservable.getValue();
        KycFormError tmpFormError = new KycFormError();

        assert kycBean != null;
        if (kycBean.getFirstName() == null || kycBean.getFirstName().isEmpty()) {
            flag = false;
            tmpFormError.setFirstNameError(R.string.required_field_error);
        }

        if (kycBean.getLastName() == null || kycBean.getLastName().isEmpty()) {
            flag = false;
            tmpFormError.setLastNameError(R.string.required_field_error);
        }

        if (kycBean.getDob() == null) {
            flag = false;
            tmpFormError.setDobError(R.string.required_field_error);
        }

        if (kycBean.getEmail() == null || kycBean.getEmail().isEmpty()) {
            flag = false;
            tmpFormError.setEmailError(R.string.required_field_error);
        } else if (verifiedEmail.getValue() == null || !verifiedEmail.getValue().equals(kycBean.getEmail())) {
            flag = false;
            tmpFormError.setEmailError(R.string.please_Verify_your_email_id);
        }

        formError.setValue(tmpFormError);
        return flag;
    }

    private boolean validAddressInfo() {
        boolean flag = true;
        KycBean kycBean = kycBeanObservable.getValue();
        KycFormError tmpFormError = new KycFormError();

        assert kycBean != null;
        if (kycBean.getCountry() == null || kycBean.getCountry().isEmpty()) {
            flag = false;
            tmpFormError.setCountryError(R.string.required_field_error);
        }

        if (kycBean.getStreet() == null || kycBean.getStreet().isEmpty()) {
            flag = false;
            tmpFormError.setStreetError(R.string.required_field_error);
        }

        if (kycBean.getAppNo() == null || kycBean.getAppNo().isEmpty()) {
            flag = false;
            tmpFormError.setAppNoError(R.string.required_field_error);
        }

        if (kycBean.getAddress() == null || kycBean.getAddress().isEmpty()) {
            flag = false;
            tmpFormError.setAddressError(R.string.required_field_error);
        }

        if (kycBean.getState() == null || kycBean.getState().isEmpty()) {
            flag = false;
            tmpFormError.setStateError(R.string.required_field_error);
        }

        if (kycBean.getPinCode() == null || kycBean.getPinCode().isEmpty()) {
            flag = false;
            tmpFormError.setPinCodeError(R.string.required_field_error);
        }
        formError.setValue(tmpFormError);
        return flag;
    }

    private boolean validSsnInfo() {
        boolean flag = true;
        KycBean kycBean = kycBeanObservable.getValue();
        KycFormError tmpFormError = new KycFormError();
        if (kycBean.getSsn() == null || kycBean.getSsn().isEmpty()) {
            flag = false;
            tmpFormError.setSsnError(R.string.required_field_error);
        }
        return flag;
    }

    public MutableLiveData<Integer> getPageNoObservable() {
        return pageNoObservable;
    }

    public MutableLiveData<KycBean> getKycBeanObservable() {
        return kycBeanObservable;
    }

    public void setKycBeanObservable(KycBean kycBeanObservable) {
        this.kycBeanObservable.setValue(kycBeanObservable);
    }

    public void setDob(Long dob) {
        this.kycBeanObservable.getValue().setDob(dob);
    }

    public Long getDob() {
        return kycBeanObservable.getValue().getDob();
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public boolean isValidateFields() {
        return validateFields;
    }

    public MutableLiveData<KycFormError> getFormError() {
        return formError;
    }

    public MediatorLiveData<Boolean> getKycPostStatus() {
        return kycPostStatus;
    }

    public MutableLiveData<String> getLoadingObservable() {
        return loadingObservable;
    }

    public MediatorLiveData<String> getEnableVerificationFields() {
        return enableVerificationFields;
    }

    public void setDocumentScanResult(KycScanResult documentScanResult) {
        this.documentScanResult.setValue(documentScanResult);
    }

    public MediatorLiveData<KycScanResult> getDocumentScanResult() {
        return documentScanResult;
    }

    public ObservableField<String> getKycScanStatus() {
        return kycScanStatus;
    }
}