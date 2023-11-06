package by.levitsky.telegrambot.service;

import by.levitsky.telegrambot.model.CurrencyModel;
import by.levitsky.telegrambot.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface CurrencyService {
    public String getCurrencyRates(String message, CurrencyModel model) throws IOException, ParseException;
    static Set<String> getCurrenciesList() throws IOException {
        Set<String> currencies=new HashSet<>();
        URL url = new URL("https://api.nbrb.by/exrates/currencies");
        JSONArray arr = Utils.getJsonArray(url);
        currencies= IntStream.range(0, arr.length())
                .mapToObj(index -> ((JSONObject)arr.get(index)).optString("Cur_Abbreviation"))
                .collect(Collectors.toSet());
        return currencies;
    }
}
