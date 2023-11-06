package by.levitsky.telegrambot.service.impl;

import by.levitsky.telegrambot.model.CurrencyModel;
import by.levitsky.telegrambot.service.CurrencyService;
import by.levitsky.telegrambot.util.Utils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class CurrencyServiceImpl implements CurrencyService {
    @Override
    public String getCurrencyRates(String message, CurrencyModel model) throws IOException, ParseException {
        URL url = new URL("https://api.nbrb.by/exrates/rates/" + message + "?parammode=2");
        JSONObject jsonObject = Utils.getJson(url);

        model.setCur_ID(jsonObject.getInt("Cur_ID"));
        model.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(jsonObject.getString("Date")));
        model.setCur_Abbreviation(jsonObject.getString("Cur_Abbreviation"));
        model.setCur_Scale(jsonObject.getInt("Cur_Scale"));
        model.setCur_Name(jsonObject.getString("Cur_Name"));
        model.setCur_OfficialRate(jsonObject.getDouble("Cur_OfficialRate"));

        return "Official rate of BYN to " + model.getCur_Abbreviation() + "\n" +
                "on the date: " + Utils.getFormatDate(model) + "\n" +
                "is: " + model.getCur_OfficialRate() + " BYN per " + model.getCur_Scale() + " " + model.getCur_Abbreviation();
    }

}
