package com.jprarama.booklistingapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jprarama.booklistingapp.R;
import com.jprarama.booklistingapp.model.BookInfo;

import java.util.List;

/**
 * Created by joshua on 4/7/16.
 */
public class BookInfoAdapter extends ArrayAdapter<BookInfo> {

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvAuthors;
    }

    public BookInfoAdapter(Context context, int resource, List<BookInfo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookInfo bookInfo = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.book_info_item, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvAuthors = (TextView) convertView.findViewById(R.id.tvAuthors);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(bookInfo.getTitle());
        viewHolder.tvAuthors.setText(TextUtils.join(", ", bookInfo.getAuthors()));

        return convertView;
    }
}
