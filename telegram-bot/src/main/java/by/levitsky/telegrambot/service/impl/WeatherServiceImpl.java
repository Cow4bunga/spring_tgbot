package by.levitsky.telegrambot.service.impl;

import by.levitsky.telegrambot.model.ForecastModel;
import by.levitsky.telegrambot.model.WeatherReport;
import by.levitsky.telegrambot.util.Utils;
import by.levitsky.telegrambot.service.WeatherService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Override
    public String getRealTimeForecast(String message, WeatherReport report) throws IOException, ParseException {
        URL url = new URL("http://api.weatherapi.com/v1/current.json?key=*******************************&q=" + message);
        return getWeatherReportData(url, report);
    }

    @Override
    public String getForecast(String message, ForecastModel report) throws IOException, ParseException {
        URL url = new URL("http://api.weatherapi.com/v1/forecast.json?key=*******************************&q=" + message);
        return getForecastData(url, report);
    }

    private String getWeatherReportData(URL url, WeatherReport report) throws IOException {
        JSONObject jsonObject = Utils.getJson(url);
        JSONObject locJson = jsonObject.getJSONObject("location");
        JSONObject jsonObject1 = jsonObject.getJSONObject("current");

        report.setCity(locJson.getString("name") + ", " + locJson.getString("country"));
        report.setTempF(jsonObject1.getDouble("temp_f"));
        report.setTempC(jsonObject1.getDouble("temp_c"));
        report.setHumidity(jsonObject1.getInt("humidity"));
        report.setWindDir(jsonObject1.getString("wind_dir"));
        report.setWindKPH(jsonObject1.getDouble("wind_kph"));
        report.setWindMPH(jsonObject1.getDouble("wind_mph"));
        report.setFeelsLikeF(jsonObject1.getDouble("feelslike_f"));
        report.setFeelsLikeC(jsonObject1.getDouble("feelslike_c"));
        report.setUvIndex(jsonObject1.getDouble("uv"));

        JSONObject cond = jsonObject1.getJSONObject("condition");
        report.setCondition(cond.getString("text"));

        return "Real time weather in " + report.getCity() +
                " at the moment\n"
                + "temperature in Celsius: " + report.getTempC() + "\n"
                + "feels like: " + report.getFeelsLikeC() + "\n\n"
                + "humidity: " + report.getHumidity() + "\n"
                + "wind direction and speed: " + report.getWindDir() + ", " + report.getWindKPH() + "\n\n"
                + "UV index: " + report.getUvIndex() + "\n"
                + report.getCondition();
    }

    private String getForecastData(URL url, ForecastModel report) throws IOException {
        JSONObject jsonObject = Utils.getJson(url);
        JSONObject locJson = jsonObject.getJSONObject("location");
        JSONObject jsonObject1 = jsonObject.getJSONObject("forecast");
        JSONArray jsonArray = jsonObject1.getJSONArray("forecastday");
        JSONObject fday = jsonArray.getJSONObject(0);
        JSONObject day = fday.getJSONObject("day");

        report.setCity(locJson.getString("name") + ", " + locJson.getString("country"));
        report.setMaxTempC(day.getDouble("maxtemp_c"));
        report.setMinTempC(day.getDouble("mintemp_c"));
        report.setHumidity(day.getInt("avghumidity"));
        report.setRainChance(day.getInt("daily_chance_of_rain"));
        report.setSnowChance(day.getInt("daily_chance_of_snow"));
        report.setWindKPHMax(day.getDouble("maxwind_kph"));
        report.setAvgTempC(day.getDouble("avgtemp_c"));
        report.setUvIndex(day.getDouble("uv"));

        JSONObject cond = day.getJSONObject("condition");
        report.setCondition(cond.getString("text"));

        return "Forecast for the day in " + report.getCity() +
                "\n"
                + "Average temperature in Celsius: " + report.getAvgTempC() + "\n"
                + "highest temperature " + report.getMaxTempC() + ", lowest " + report.getMinTempC() + "\n\n"
                + "humidity: " + report.getHumidity() + "\n"
                + "max wind speed: " + report.getWindKPHMax() + "\n\n"
                + "rain chance: " + report.getRainChance() + "\n"
                + "snow chance: " + report.getSnowChance() + "\n\n"
                + "UV index: " + report.getUvIndex() + "\n"
                + report.getCondition();
    }
}
