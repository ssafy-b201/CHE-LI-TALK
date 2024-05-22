package com.ssafy.chelitalk.api.history;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTypeAdapter extends TypeAdapter<Date>  {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            String dateFormatAsString = dateFormat.format(value) + "Z";
            out.value(dateFormatAsString);
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String dateStr = in.nextString();
        try {
            return dateFormat.parse(truncateDateToMilliseconds(dateStr));
        } catch (ParseException e) {
            throw new JsonSyntaxException(e);
        }
    }

    private String truncateDateToMilliseconds(String dateTime) {
        int zIndex = dateTime.lastIndexOf('Z');
        if (zIndex != -1) {
            dateTime = dateTime.substring(0, zIndex);
        }
        int dotIndex = dateTime.lastIndexOf('.');
        if (dotIndex != -1 && dateTime.length() > dotIndex + 4) {
            dateTime = dateTime.substring(0, dotIndex + 4);
        }
        return dateTime + "Z";
    }
}
