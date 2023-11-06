package by.levitsky.telegrambot.service.impl;

import by.levitsky.telegrambot.model.NewsModel;
import by.levitsky.telegrambot.model.SourceModel;
import by.levitsky.telegrambot.service.NewsService;
import by.levitsky.telegrambot.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private final int MAX_ARTICLES = 5;
    private final int MAX_SOURCES = 3;

    @Override
    public List<String> getEverythingByKeyword(String keyword, NewsModel newsModel) throws IOException, ParseException {
        String dateToday = Utils.getTodayDate();
        String dateYesterday = Utils.getYesterdayDate();
        URL url = new URL("https://newsapi.org/v2/everything?q=" + keyword + "&from=" + dateYesterday + "&to=" + dateToday + "&sortBy=popularity&apiKey=b315e55bf7e541b3bbe198e527af246c");
        return getNewsData(url, newsModel);
    }

    @Override
    public List<String> getHeadlinesByCountry(String country, NewsModel newsModel) throws IOException, ParseException {
        URL url = new URL("https://newsapi.org/v2/top-headlines?country=" + country + "&apiKey=b315e55bf7e541b3bbe198e527af246c");
        return getNewsData(url, newsModel);
    }

    @Override
    public List<String> getHeadlinesByCategory(String category, NewsModel newsModel) throws IOException, ParseException {
        URL url = new URL("https://newsapi.org/v2/top-headlines?category=" + category + "&apiKey=b315e55bf7e541b3bbe198e527af246c");
        return getNewsData(url, newsModel);
    }

    @Override
    public List<String> getHeadlinesBySource(String source, NewsModel newsModel) throws IOException, ParseException {
        URL url = new URL("https://newsapi.org/v2/top-headlines?sources=" + source + "&apiKey=b315e55bf7e541b3bbe198e527af246c");
        return getNewsData(url, newsModel);
    }

    @Override
    public List<String> getSourcesByCountry(String country, SourceModel sourceModel) throws IOException, ParseException {
        URL url = new URL("https://newsapi.org/v2/top-headlines/sources?country=" + country + "&sortBy=popularity&apiKey=b315e55bf7e541b3bbe198e527af246c");
        return getSourcesData(url, sourceModel);
    }

    @Override
    public List<String> getSourcesByCategory(String category, SourceModel sourceModel) throws IOException, ParseException {
        URL url = new URL("https://newsapi.org/v2/top-headlines/sources?category=" + category + "&sortBy=popularity&apiKey=b315e55bf7e541b3bbe198e527af246c");
        return getSourcesData(url, sourceModel);
    }

    private String createNewsResponse(NewsModel newsModel) {
        return "Source: " + newsModel.getSource() + "\n"
                + newsModel.getTitle() + "\n"
                + "Description: " + newsModel.getDescription() + "\n"
                + newsModel.getContent() + "\n\n"
                + "Learn more: " + newsModel.getUrl();
    }

    private String createSourcesResponse(SourceModel sourceModel) {
        return "Source: " + sourceModel.getSource() + "\n"
                + "Description: " + sourceModel.getDescription() + "\n"
                + "Category: " + sourceModel.getCategory() + "\n"
                + "Country: " + sourceModel.getCountry() + "\n\n"
                + "Learn more: " + sourceModel.getUrl();
    }

    private List<String> getNewsData(URL url, NewsModel newsModel) throws IOException {
        JSONObject jsonObject = Utils.getJson(url);
        JSONArray jsonArray = jsonObject.getJSONArray("articles");

        List<String> articles = new ArrayList<>();
        for (int i = 0; i < MAX_ARTICLES; i++) {
            JSONObject instance = jsonArray.getJSONObject(i);
            newsModel.setSource(instance.getJSONObject("source").getString("name"));
            newsModel.setTitle(instance.getString("title"));
            if (instance.get("description") != JSONObject.NULL) {
                newsModel.setDescription(instance.getString("description").substring(0,instance.getString("description").indexOf("\n")));
            } else newsModel.setDescription("none");
            newsModel.setUrl(instance.getString("url"));
            if (instance.get("content") != JSONObject.NULL) {
                newsModel.setContent(instance.getString("content"));
            } else newsModel.setContent("none");
            articles.add(createNewsResponse(newsModel));
        }

        return articles;
    }

    private List<String> getSourcesData(URL url, SourceModel sourceModel) throws IOException {
        JSONObject jsonObject = Utils.getJson(url);
        JSONArray jsonArray = jsonObject.getJSONArray("sources");

        List<String> sources = new ArrayList<>();
        for (int i = 0; i < MAX_SOURCES; i++) {
            JSONObject instance = jsonArray.getJSONObject(i);
            sourceModel.setSource(instance.getString("name"));
            sourceModel.setUrl(instance.getString("url"));
            if (instance.getString("description") != null) {
                sourceModel.setDescription(instance.getString("description").substring(0,instance.getString("description").indexOf("\n")));
            } else sourceModel.setDescription("none");
            sourceModel.setCategory(instance.getString("category"));
            sourceModel.setCountry(instance.getString("country"));
            sourceModel.setLanguage(instance.getString("language"));
            sources.add(createSourcesResponse(sourceModel));
        }
        return sources;
    }
}
