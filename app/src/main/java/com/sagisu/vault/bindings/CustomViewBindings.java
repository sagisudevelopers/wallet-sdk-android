package com.sagisu.vault.bindings;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputLayout;
import com.sagisu.vault.R;
import com.sagisu.vault.models.Account;
import com.sagisu.vault.ui.transfer.CustomFooterAdapter;
import com.sagisu.vault.utils.PicassoTrustAll;
import com.squareup.picasso.Callback;

import java.util.List;

import kotlin.jvm.JvmStatic;

public class CustomViewBindings {
    @BindingAdapter("entries")
    public static void bindAutoCompleteView(AutoCompleteTextView autoCompleteTextView, String[] array) {
        if (array != null && array.length > 0)
            autoCompleteTextView.setAdapter(
                    new ArrayAdapter<>(autoCompleteTextView.getContext(),
                            R.layout.dropdown_menu_popup_item,
                            array));
    }

    @BindingAdapter("accountEntries")
    public static void bindAccountDropdown(AutoCompleteTextView autoCompleteTextView, List<Account> array) {
        if (array != null && array.size() > 0)
            autoCompleteTextView.setAdapter(
                    new CustomFooterAdapter(autoCompleteTextView.getContext(),
                            R.layout.dropdown_item_account,
                            R.id.dropdown_item_from_wallet,
                            array,
                            "Add Account"));
    }

    @BindingAdapter(value = "removeRequiredErrorOnTextChange")
    public static void setListener(TextInputLayout view, Boolean required) {
        if (!required) {
            return;
        }
        view.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.toString().isEmpty()) {
                    return;
                }

                if (s.toString().trim().length() > 0 && view.getError() != null) {
                    view.setError(null);
                    view.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @BindingAdapter("requiredValidator")
    public static void requiredValidator(TextInputLayout view, @NonNull String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (value.trim().length() > 0 && view.getError() != null) {
            view.setError(null);
            view.setErrorEnabled(false);
        }
    }

    @JvmStatic
    @BindingAdapter("requestFocus")
    public static void requestFocus(View view, Boolean requestFocus) {
        if (requestFocus) {
            view.requestFocus();
        }
    }


    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        PicassoTrustAll.getInstance(view.getContext())
                .load(imageUrl)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Toast.makeText(view.getContext(),"Success",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });

        /*Picasso.get()
                .load(imageUrl)
                .into(view,new Callback() {
                    @Override
                    public void onSuccess() {
                        //Toast.makeText(view.getContext(),"Success",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });*/
    }

    @BindingAdapter({"marginRaiseColor"})
    public static void setMarginRaiseColor(AppCompatTextView view, char firstLetter) {
        if (firstLetter == '-')
            view.setTextColor(view.getContext().getResources().getColor(R.color.red_500));
        else view.setTextColor(view.getContext().getResources().getColor(R.color.green));
    }

    @BindingAdapter({"abbreviatedValue"})
    public static void setAbbreviatedValue(AppCompatTextView view, Double value) {
        if (value != null)
            view.setText(coolFormat(value, 0));
    }


    private static char[] c = new char[]{'K', 'M', 'B', 'T'};

    /**
     * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
     *
     * @param n         the number to format
     * @param iteration in fact this is the class from the array c
     * @return a String representing the number n formatted in a cool looking way.
     */
    private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration + 1));

    }

    @BindingAdapter({"hyperlinkText"})
    public static void setHyperlinkText(AppCompatTextView view, String value) {
        if (value != null)
            view.setText(Html.fromHtml(value));
    }
}
