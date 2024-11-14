package DACN.DACN.services;

import DACN.DACN.entity.News;
import DACN.DACN.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    public Page<News> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    public void saveNews(News news) {
        newsRepository.save(news);
    }

    public News getNewsById(Long id) {
        return newsRepository.findById(id).orElse(null);
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
    public News getPreviousNews(Long id) {
        List<News> previousNewsList = newsRepository.findPreviousById(id);
        return previousNewsList.isEmpty() ? null : previousNewsList.get(0);
    }

    public News getNextNews(Long id) {
        List<News> nextNewsList = newsRepository.findNextById(id);
        return nextNewsList.isEmpty() ? null : nextNewsList.get(0);
    }
}