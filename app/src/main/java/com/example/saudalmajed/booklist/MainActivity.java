package com.example.saudalmajed.booklist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=android";
    EditText searchEdit;
    Button searchButton;
    TextView emptyListTextView;
    ListView listView;
    BookListingAdapter bookListAdapter;
    static final String SEARCH_RESULTS = "booksSearchResults";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEdit = (EditText) findViewById(R.id.search_box);

        emptyListTextView = (TextView) findViewById(R.id.text_no_data_found);

        // Create a new adapter
        bookListAdapter = new BookListingAdapter(this, -1);

        listView = (ListView) findViewById(R.id.list);
        // Set the adapter on the {@link ListView}
        listView.setAdapter(bookListAdapter);

        if (savedInstanceState != null) {
            BookListing[] books = (BookListing[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            bookListAdapter.addAll(books);
        }


    }

    public void SearchButton(View view) {
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isInternetConnectionAvailable()) {
                    BookListingAsync task = new BookListingAsync();
                    task.execute();
                } else {
                    Toast.makeText(MainActivity.this, R.string.network_not_found,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isInternetConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.isConnectedOrConnecting();
    }


    private void updateUi(List<BookListing> books) {
        if (books.isEmpty()) {
            // if no books found, show a message
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            emptyListTextView.setVisibility(View.GONE);
        }
        bookListAdapter.clear();
        bookListAdapter.addAll(books);
    }

    private String getUserInput() {
        return searchEdit.getText().toString();
    }

    public String getUrlForHttpRequest() {
        final String baseUrl = BOOK_REQUEST_URL;
        String formatUserInput = getUserInput().trim().replaceAll("\\s+", "+");
        String url = baseUrl + formatUserInput;
        return url;
    }


    public class BookListingAsync extends AsyncTask<URL, Void, List<BookListing>> {
        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Problem building the URL ", e);
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            // If the URL is null, then return early.
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // Closing the input stream could throw an IOException, which is why
                    // the makeHttpRequest(URL url) method signature specifies than an IOException
                    // could be thrown.
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


        @Override
        protected List<BookListing> doInBackground(URL... urls) {
            URL url = createUrl(getUrlForHttpRequest());
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<BookListing> books = parseJson(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(List<BookListing> books) {
            if (books == null) {
                return;
            }
            updateUi(books);
        }


        private List<BookListing> parseJson(String json) {

            if (json == null) {
                return null;
            }

            List<BookListing> books = QueryUtils.extractBooks(json);
            return books;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BookListing[] books = new BookListing[bookListAdapter.getCount()];
        for (int i = 0; i < books.length; i++) {
            books[i] = bookListAdapter.getItem(i);
        }

    }
}
