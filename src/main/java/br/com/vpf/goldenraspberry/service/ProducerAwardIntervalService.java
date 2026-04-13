package br.com.vpf.goldenraspberry.service;

import br.com.vpf.goldenraspberry.dto.ProducerIntervalItemResponse;
import br.com.vpf.goldenraspberry.dto.ProducerIntervalResponse;
import br.com.vpf.goldenraspberry.entity.Movie;
import br.com.vpf.goldenraspberry.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProducerAwardIntervalService {

    private final MovieRepository movieRepository;

    public ProducerAwardIntervalService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public ProducerIntervalResponse getProducerIntervals() {
        List<Movie> winnerMovies = movieRepository.findByWinnerTrueOrderByYearAsc();

        Map<String, List<Integer>> winsByProducer = groupWinsByProducer(winnerMovies);
        List<ProducerIntervalItemResponse> intervals = buildIntervals(winsByProducer);

        if (intervals.isEmpty()) {
            return new ProducerIntervalResponse(new ArrayList<>(), new ArrayList<>());
        }

        int minInterval = findMinInterval(intervals);
        int maxInterval = findMaxInterval(intervals);

        List<ProducerIntervalItemResponse> min = filterByInterval(intervals, minInterval);
        List<ProducerIntervalItemResponse> max = filterByInterval(intervals, maxInterval);

        return new ProducerIntervalResponse(min, max);
    }

    private Map<String, List<Integer>> groupWinsByProducer(List<Movie> winnerMovies) {
        Map<String, List<Integer>> winsByProducer = new HashMap<>();

        for (Movie movie : winnerMovies) {
            List<String> producers = splitProducers(movie.getProducers());

            for (String producer : producers) {
                winsByProducer
                        .computeIfAbsent(producer, key -> new ArrayList<>())
                        .add(movie.getYear());
            }
        }

        return winsByProducer;
    }

    private List<ProducerIntervalItemResponse> buildIntervals(Map<String, List<Integer>> winsByProducer) {
        List<ProducerIntervalItemResponse> intervals = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : winsByProducer.entrySet()) {
            String producer = entry.getKey();
            List<Integer> years = entry.getValue();

            if (years.size() < 2) {
                continue;
            }

            for (int i = 1; i < years.size(); i++) {
                int previousWin = years.get(i - 1);
                int followingWin = years.get(i);
                int interval = followingWin - previousWin;

                intervals.add(new ProducerIntervalItemResponse(
                        producer,
                        interval,
                        previousWin,
                        followingWin
                ));
            }
        }

        return intervals;
    }

    private int findMinInterval(List<ProducerIntervalItemResponse> intervals) {
        int min = Integer.MAX_VALUE;

        for (ProducerIntervalItemResponse item : intervals) {
            if (item.interval() < min) {
                min = item.interval();
            }
        }

        return min;
    }

    private int findMaxInterval(List<ProducerIntervalItemResponse> intervals) {
        int max = Integer.MIN_VALUE;

        for (ProducerIntervalItemResponse item : intervals) {
            if (item.interval() > max) {
                max = item.interval();
            }
        }

        return max;
    }

    private List<ProducerIntervalItemResponse> filterByInterval(List<ProducerIntervalItemResponse> intervals, int intervalTarget) {
        List<ProducerIntervalItemResponse> result = new ArrayList<>();

        for (ProducerIntervalItemResponse item : intervals) {
            if (item.interval() == intervalTarget) {
                result.add(item);
            }
        }

        result.sort(
                Comparator.comparing(ProducerIntervalItemResponse::producer)
                        .thenComparingInt(ProducerIntervalItemResponse::previousWin)
        );

        return result;
    }

    private List<String> splitProducers(String producers) {
        List<String> result = new ArrayList<>();

        if (producers == null || producers.isBlank()) {
            return result;
        }

        String normalized = producers.trim();
        normalized = normalized.replaceAll("\\s+and\\s+", ",");
        normalized = normalized.replaceAll("\\s*,\\s*", ",");

        String[] parts = normalized.split(",");

        for (String part : parts) {
            String producer = part.trim();
            if (!producer.isEmpty()) {
                result.add(producer);
            }
        }

        return result;
    }

    private int findEmpateMinInterval(List<ProducerIntervalItemResponse> intervals) {
        int min = Integer.MAX_VALUE;

        for (ProducerIntervalItemResponse item : intervals) {
            if (item.interval() == min) {
                min = item.interval();
            }
        }

        return min;
    }
}
