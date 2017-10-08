package com.example.saudalmajed.booklist;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saudalmajed on 9/21/2017 AD.
 */
public class QueryUtils {
    private QueryUtils() {
    }

    public static String formatListOfAuthors(JSONArray authorsList) throws JSONException {

        String authorsListInString = null;

        if (authorsList.length() == 0) {
            return null;
        }

        for (int i = 0; i < authorsList.length(); i++) {
            if (i == 0) {
                authorsListInString = authorsList.getString(0);
            } else {
                authorsListInString += ", " + authorsList.getString(i);
            }
        }
        return authorsListInString;
    }

    public static List<BookListing> extractBooks(String json) {

        List<BookListing> books = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(json);

            if (jsonResponse.getInt("totalItems") == 0) {
                return books;
            }
            JSONArray jsonArray = jsonResponse.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bookObject = jsonArray.getJSONObject(i);
                JSONObject bookInfo = bookObject.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String title = bookInfo.getString("title");
                // Extract the value for the key called "authors"
                String authors = new String("No Authors");
                if (!bookInfo.isNull("authors")) {
                    JSONArray authorsArray = bookInfo.getJSONArray("authors");
                    authors = formatListOfAuthors(authorsArray);
                }
                // Add the new {@link book} to the list of books.
                books.add(new BookListing(title, authors));
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Books JSON results", e);
        }
        // Return the list of books
        return books;
    }
}
