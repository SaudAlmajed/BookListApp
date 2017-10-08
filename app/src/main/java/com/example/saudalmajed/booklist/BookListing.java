package com.example.saudalmajed.booklist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by saudalmajed on 9/21/2017 AD.
 */

public class BookListing implements Parcelable {
    private String bTitle;
    private String bAuthors;

    public BookListing(String title, String authors) {
        this.bTitle = title;
        this.bAuthors = authors;
    }

    protected BookListing(Parcel in) {
        bTitle = in.readString();
        bAuthors = in.readString();
    }

    public static final Creator<BookListing> CREATOR = new Creator<BookListing>() {
        @Override
        public BookListing createFromParcel(Parcel in) {
            return new BookListing(in);
        }

        @Override
        public BookListing[] newArray(int size) {
            return new BookListing[size];
        }
    };

    public String getTitle() {
        return bTitle;
    }

    public String getAuthors() {
        return bAuthors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bTitle);
        parcel.writeString(bAuthors);
    }
}
