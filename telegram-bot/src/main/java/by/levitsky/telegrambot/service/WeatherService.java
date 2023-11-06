package by.levitsky.telegrambot.service;

import by.levitsky.telegrambot.model.ForecastModel;
import by.levitsky.telegrambot.model.WeatherReport;

import java.io.IOException;
import java.text.ParseException;

public interface WeatherService {
    public String getRealTimeForecast(String message, WeatherReport report) throws IOException, ParseException;
    public String getForecast(String message, ForecastModel report) throws IOException,ParseException;
}
