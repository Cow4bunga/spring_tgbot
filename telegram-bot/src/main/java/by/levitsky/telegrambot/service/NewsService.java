package by.levitsky.telegrambot.service;

import by.levitsky.telegrambot.model.NewsModel;
import by.levitsky.telegrambot.model.SourceModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface NewsService {

    public List<String> getEverythingByKeyword(String keyword, NewsModel newsModel) throws IOException, ParseException;
    public List<String> getHeadlinesByCountry(String country, NewsModel newsModel) throws IOException, ParseException;
    public List<String> getHeadlinesByCategory(String category, NewsModel newsModel) throws IOException,ParseException;
    public List<String> getHeadlinesBySource(String source, NewsModel newsModel) throws IOException,ParseException;

    public List<String> getSourcesByCountry(String country, SourceModel sourceModel) throws IOException, ParseException;
    public List<String> getSourcesByCategory(String category, SourceModel sourceModel) throws IOException, ParseException;
}
