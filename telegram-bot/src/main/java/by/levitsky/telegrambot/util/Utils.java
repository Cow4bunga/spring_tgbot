package by.levitsky.telegrambot.util;

import by.levitsky.telegrambot.model.CurrencyModel;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface Utils {
    static String getFormatDate(CurrencyModel model) {
        return new SimpleDateFormat("dd MMM yyyy").format(model.getDate());
    }

    static JSONArray getJsonArray(URL url) throws IOException {
        String json = IOUtils.toString(url, Charset.forName("UTF-8"));
        return new JSONArray(json);
    }

    static JSONObject getJson(URL url) throws IOException {
        String json = IOUtils.toString(url, Charset.forName("UTF-8"));
        return new JSONObject(json);
    }

    static String getTodayDate(){
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateObj.format(formatter);

        return date;
    }

    static String getYesterdayDate(){
        LocalDate dateObj = LocalDate.now();
        LocalDate yestObj=dateObj.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date=yestObj.format(formatter);

        return date;
    }
}
