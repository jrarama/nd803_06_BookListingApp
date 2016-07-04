package com.jprarama.booklistingapp.task;

import android.os.AsyncTask;

import com.jprarama.booklistingapp.model.BookInfo;
import com.jprarama.booklistingapp.util.HttpUtil;
import com.jprarama.booklistingapp.util.JsonConsumer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joshua on 4/7/16.
 */
public class BookFetcher extends AsyncTask<String, Void, ArrayList<BookInfo>> {

    private static final int MAX_RESULTS = 20;
    private static final String QUERY_KEY = "q";
    private static final String MAX_RESULTS_KEY = "maxResults";
    private static final String ITEMS_KEY = "items";
    private static final String VOLUME_INFO_KEY = "volumeInfo";
    private static final String TITLE_KEY = "title";
    private static final String AUTHORS_KEY = "authors";
    private static final String TOTAL_ITEMS_KEY = "totalItems";

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    private JsonConsumer<BookInfo> consumer;
    private Exception catchedException;

    public BookFetcher(JsonConsumer<BookInfo> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected ArrayList<BookInfo> doInBackground(String... strings) {
        ArrayList<BookInfo> items = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put(QUERY_KEY, String.valueOf(strings[0]));
        params.put(MAX_RESULTS_KEY, String.valueOf(MAX_RESULTS));

        try {
            catchedException = null;
            String json = HttpUtil.getJson(BASE_URL, params);
            JSONObject jsonObject = new JSONObject(json);
            int total = jsonObject.getInt(TOTAL_ITEMS_KEY);
            if (total == 0) {
                return items;
            }

            JSONArray jsonArray = jsonObject.getJSONArray(ITEMS_KEY);

            for (int i = 0, len = jsonArray.length(); i < len; i++) {
                JSONObject volumeInfo = jsonArray.getJSONObject(i).getJSONObject(VOLUME_INFO_KEY);
                String title = volumeInfo.getString(TITLE_KEY);

                List<String> authors = new ArrayList<>();

                if (volumeInfo.has(AUTHORS_KEY)) {
                    JSONArray authorsArray = volumeInfo.getJSONArray(AUTHORS_KEY);

                    for (int j = 0, alen = authorsArray.length(); j < alen; j++) {
                        authors.add(authorsArray.getString(j));
                    }
                }
                BookInfo info = new BookInfo(title, authors);
                items.add(info);
            }

        } catch (Exception e) {
            catchedException = e;
            return null;
        }
        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<BookInfo> bookInfos) {
        if (catchedException != null) {
            consumer.consumeException(catchedException);
        } else {
            consumer.consume(bookInfos);
        }
    }
}
