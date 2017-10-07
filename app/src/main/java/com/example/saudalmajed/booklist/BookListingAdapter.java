package com.example.saudalmajed.booklist;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by saudalmajed on 9/21/2017 AD.
 */

public class BookListingAdapter extends ArrayAdapter<BookListing>{


    public BookListingAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        BookListing book = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        // Find the TextView with view ID author
        TextView author = (TextView) convertView.findViewById(R.id.author);
        title.setText(book.getTitle());
        author.setText(book.getAuthors());

return convertView;
    }
}
