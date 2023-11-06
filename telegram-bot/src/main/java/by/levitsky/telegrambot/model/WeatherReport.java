package by.levitsky.telegrambot.model;

import lombok.Data;

@Data
public class WeatherReport {
    String city;
    Double tempC;
    Double tempF;
    Double feelsLikeC;
    Double feelsLikeF;
    Integer humidity;
    String windDir;
    Double windKPH;
    Double windMPH;
    Double uvIndex;

    String condition;
}
