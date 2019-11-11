package ca.bcit.a3717assignment2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;


/**
 * List adapter for list items.
 */
public class ItemListAdapter extends ArrayAdapter<FormItems> {
    private Activity context;
    private List<FormItems> itemList;

    public ItemListAdapter(Activity context, List<FormItems> itemList) {
        super(context, R.layout.list_layout, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    public ItemListAdapter(Context context, int resource, List<FormItems> objects, Activity context1, List<FormItems> itemList) {
        super(context, resource, objects);
        this.context = context1;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView tvUserId = listViewItem.findViewById(R.id.textViewUserId);
        TextView tvReadingDate= listViewItem.findViewById(R.id.tvDate);
        TextView tvReadingTime = listViewItem.findViewById(R.id.tvTime);
        TextView tvSystolicReading = listViewItem.findViewById(R.id.textViewSystolicReading);
        TextView tvDiastolicReading = listViewItem.findViewById(R.id.textViewDiastolicReading);
        TextView tvCondition = listViewItem.findViewById((R.id.textViewCondition));

        FormItems item = itemList.get(position);
        tvUserId.setText(item.getUserId());
        tvReadingDate.setText(item.getDateReading());
        tvReadingTime.setText(item.getTimeReading());
        tvSystolicReading.setText(item.getSystolicReading());
        tvDiastolicReading.setText(item.getDiastolicReading());
        tvCondition.setText(item.getCond());
        return listViewItem;
    }

}
