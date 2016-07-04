package com.jprarama.booklistingapp.util;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by joshua on 4/7/16.
 */
public interface JsonConsumer<T> {

    void consume(ArrayList<T> items);

    void consumeException(Exception ex);

}
