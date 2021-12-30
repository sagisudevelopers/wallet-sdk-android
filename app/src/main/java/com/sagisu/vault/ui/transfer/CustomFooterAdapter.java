package com.sagisu.vault.ui.transfer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sagisu.vault.models.Account;

import java.util.ArrayList;
import java.util.List;

public class CustomFooterAdapter extends ArrayAdapter<Account> implements Filterable {

    public interface OnFooterClickListener {
        void onFooterClicked();
    }

    public interface OnItemClickListener {
        void onItemClicked(Account account);
    }

    private List<Account> mObjects;
    private final Object mLock = new Object();

    private final int mResource;
    private final int mDropDownResource;
    private final int mTextViewId;

    private List<Account> mOriginalValues;
    private ArrayFilter mFilter;

    private final LayoutInflater mInflater;

    // the last item, i.e the footer
    private String mFooterText;

    // our listener
    private OnFooterClickListener mListener;
    private OnItemClickListener mItemListener;

    public CustomFooterAdapter(Context context, int resource, int textViewId, List<Account> objects, String footerText) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = mDropDownResource = resource;
        mObjects = objects;
        mFooterText = footerText;
        mTextViewId = textViewId;
    }


    /**
     * Set listener for clicks on the footer item
     */
    public void setOnFooterClickListener(OnFooterClickListener listener) {
        mListener = listener;
    }

    /**
     * Set listener for clicks on the real item
     */
   /* public void setOnItemClickListener(OnItemClickListener listener) {
        mItemListener = listener;
    }*/

    @Override
    public int getCount() {
        return mObjects.size() + 1;
    }

    @Override
    public Account getItem(int position) {
        if (position == (getCount() - 1)) {
            // last item is always the footer text
            //return mFooterText;
            Account account = new Account();
            account.setName(mFooterText);
            return account;
        }

        // return real text
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
                                        int resource) {
        View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            //  If no custom field is assigned, assume the whole resource is a TextView
            text = (TextView) view.findViewById(mTextViewId);
        } catch (ClassCastException e) {
            Log.e("CustomAutoCompleteAdapt", "Layout XML file is not a text field");
            throw new IllegalStateException("Layout XML file is not a text field", e);
        }

        text.setText(getItem(position).toString());

        if (position == (getCount() - 1)) {
            // it's the last item, bind click listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onFooterClicked();
                    }
                }
            });
        } else {
            // it's a real item, set click listener to null and reset to original state
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemListener != null) {
                        mItemListener.onItemClicked(getItem(position));
                    }
                }
            });*/
            view.setOnClickListener(null);
            view.setClickable(false);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = mObjects;
                }
            }

            if (prefix == null || prefix.length() == 0) {
                List<Account> list;
                synchronized (mLock) {
                    list = mOriginalValues;
                }
                results.values = list;

                // add +1 since we have a footer item which is always visible
                results.count = list.size() + 1;
            } else {
                String prefixString = prefix.toString().toLowerCase();

                List<Account> values;
                synchronized (mLock) {
                    values = mOriginalValues;
                }

                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<String>();

                for (int i = 0; i < count; i++) {
                    final String value = values.get(i).toString();
                    final String valueText = value.toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                // add one since we always show the footer
                results.count = newValues.size() + 1;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<Account>) results.values;
            notifyDataSetChanged();
        }
    }
}
