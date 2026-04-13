package br.com.vpf.goldenraspberry.config;

import br.com.vpf.goldenraspberry.entity.Movie;
import br.com.vpf.goldenraspberry.repository.MovieRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Component
@Profile("!test")
public class CsvDataLoader implements CommandLineRunner {

    private final MovieRepository movieRepository;

    public CsvDataLoader(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (movieRepository.count() > 0) {
            return;
        }

        loadCsv();
    }

    public void loadCsv() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/Movielist.csv");

        try (Reader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = CSVFormat.DEFAULT.builder()
                     .setDelimiter(';')
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreEmptyLines(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord record : csvParser) {
                Movie movie = new Movie();
                movie.setYear(parseYear(record.get("year")));
                movie.setTitle(record.get("title"));
                movie.setStudios(record.get("studios"));
                movie.setProducers(record.get("producers"));
                movie.setWinner(parseWinner(record.get("winner")));

                movieRepository.save(movie);
            }
        }
    }

    private Integer parseYear(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Ano do filme não informado no CSV.");
        }
        return Integer.valueOf(value.trim());
    }

    private Boolean parseWinner(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return "yes".equalsIgnoreCase(value.trim());
    }

}
