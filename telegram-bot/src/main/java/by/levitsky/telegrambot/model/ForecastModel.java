package by.levitsky.telegrambot.model;

import lombok.Data;

@Data
public class ForecastModel {
    String city;
    Double maxTempC;
    Double minTempC;
    Double avgTempC;
    Integer humidity;
    Integer rainChance;
    Integer snowChance;
    String windDir;
    Double windKPHMax;
    Double uvIndex;
    String condition;
}
