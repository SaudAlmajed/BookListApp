package com.example.saudalmajed.booklist;

/**
 * Created by saudalmajed on 9/21/2017 AD.
 */

public class BookListing {
    private String bTitle;
    private String bAuthors;

    public BookListing(String title, String authors) {
        this.bTitle = title;
        this.bAuthors = authors;
    }

    public String getTitle() {
        return bTitle;
    }

    public String getAuthors() {
        return bAuthors;
    }
}
