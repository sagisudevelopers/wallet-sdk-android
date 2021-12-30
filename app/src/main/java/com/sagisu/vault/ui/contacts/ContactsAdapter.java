package com.sagisu.vault.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sagisu.vault.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends ArrayAdapter<ContactsInfo> implements Filterable {

    interface IContactsClickListener {
        void onContactClick(ContactsInfo contactsInfo);
    }

    private final List<ContactsInfo> contactsInfoList;
    private List<ContactsInfo> mFilterContactsInfoList;
    private final Context context;
    private IContactsClickListener listener;

    public ContactsAdapter(@NonNull Context context, int resource, @NonNull List<ContactsInfo> objects, IContactsClickListener listener) {
        super(context, resource, objects);
        this.contactsInfoList = objects;
        this.mFilterContactsInfoList = objects;
        this.context = context;
        this.listener = listener;
    }

    private static class ViewHolder {
        TextView displayName;
        TextView phoneNumber;
        TextView displayFirstLetter;
        ConstraintLayout rootLayout;
    }

    @Override
    public int getCount() {
        return mFilterContactsInfoList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.contacts_item, null);

            holder = new ViewHolder();
            holder.rootLayout = convertView.findViewById(R.id.contact_root);
            holder.displayName = convertView.findViewById(R.id.contact_display_name);
            holder.phoneNumber = convertView.findViewById(R.id.contact_phone);
            holder.displayFirstLetter = convertView.findViewById(R.id.contact_display_first_letter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactsInfo contactsInfo = mFilterContactsInfoList.get(position);
        holder.displayFirstLetter.setText(contactsInfo.getDisplayName().substring(0, 1).toUpperCase());
        holder.displayName.setText(contactsInfo.getDisplayName());
        holder.phoneNumber.setText(contactsInfo.getPhoneNumber());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onContactClick(contactsInfo);
            }
        });
        return convertView;
    }

    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getFilter()
     */
    @Override
    public Filter getFilter() {
        return new Filter() {

            /* (non-Javadoc)
             * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                /*
                 * Here, you take the constraint and let it run against the array
                 * You return the result in the object of FilterResults in a form
                 * you can read later in publichResults.
                 */
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    mFilterContactsInfoList = contactsInfoList;
                } else {
                    ArrayList<ContactsInfo> newContactList = new ArrayList<ContactsInfo>();
                    for (ContactsInfo t : contactsInfoList) {
                        if ((t.getDisplayName()!=null && t.getDisplayName().toUpperCase().startsWith(constraint.toString().toUpperCase())) || (t.getPhoneNumber()!=null && t.getPhoneNumber().startsWith(constraint.toString()))) {
                            newContactList.add(t);
                        }
                    }
                    mFilterContactsInfoList = newContactList;
                }
                results.values = mFilterContactsInfoList;
                results.count = mFilterContactsInfoList.size();
                return results;
            }

            /* (non-Javadoc)
             * @see android.widget.Filter#publishResults(java.lang.CharSequence, android.widget.Filter.FilterResults)
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // TODO Auto-generated method stub
                /*
                 * Here, you take the result, put it into Adapters array
                 * and inform about the the change in data.
                 */
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
                //mFilterContactsInfoList = (List<ContactsInfo>) results.values;
                //notifyDataSetChanged();
            }

        };
    }
}
