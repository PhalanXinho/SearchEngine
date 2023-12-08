import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import filter.SearchResultFilter;
import filter.StreamSearchResultFilter;
import repository.search.HazelcastSearchRepository;
import repository.search.SearchRepository;
import search.SearchResult;
import search.SearchService;
import search.SearchServiceImpl;
import repository.book.BookRepository;
import repository.book.PostgreSQLBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import search.parser.WordParser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.before;
import static spark.Spark.get;

public class Main {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Main.class);

        BookRepository bookRepository = new PostgreSQLBookRepository();
        SearchRepository searchRepository = new HazelcastSearchRepository();
        SearchService searchService = new SearchServiceImpl(searchRepository, bookRepository);


        // Disable CORS for all routes
        // ONLY FOR localhost TESTING
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        get("/document/:word", "application/json", (request, response) -> {

            String from = request.queryParams("from");
            String to = request.queryParams("to");
            String author = request.queryParams("author");

            logger.info("Received request " + request.url() + " from " + request.ip());
            logger.info(new Timestamp(System.currentTimeMillis()) + ": " + request.params(":word"));
            logger.info("Author: " + author );
            logger.info("From: " + from);
            logger.info("To: " + to);

            response.type("application/json");
            List<String> words = new WordParser().parse(request.params(":word"));

            List<SearchResult> searchResult = searchService.search(words);

            SearchResultFilter filter = new StreamSearchResultFilter(author, from, to);
            searchResult = filter.filter(searchResult);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            return gson.toJson(searchResult);
        });
    }
}