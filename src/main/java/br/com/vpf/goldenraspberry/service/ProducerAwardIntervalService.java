package br.com.vpf.goldenraspberry.service;

import br.com.vpf.goldenraspberry.dto.ProducerIntervalItemResponse;
import br.com.vpf.goldenraspberry.dto.ProducerIntervalResponse;
import br.com.vpf.goldenraspberry.repository.MovieRepository;
import br.com.vpf.goldenraspberry.repository.projection.MovieAwardProjection;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProducerAwardIntervalService {

    private final MovieRepository movieRepository;

    public ProducerAwardIntervalService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public ProducerIntervalResponse getProducerIntervals() {
        List<MovieAwardProjection> winnerMovies = movieRepository.findWinnerAwards();

        Map<String, Integer> lastWinByProducer = new HashMap<>();

        List<ProducerIntervalItemResponse> minList = new ArrayList<>();
        List<ProducerIntervalItemResponse> maxList = new ArrayList<>();

        Integer minInterval = null;
        Integer maxInterval = null;

        for (MovieAwardProjection movie : winnerMovies) {
            int currentYear = movie.getYear();

            for (String producer : splitProducers(movie.getProducers())) {
                Integer previousWin = lastWinByProducer.put(producer, currentYear);

                if (previousWin == null) {
                    continue;
                }

                int interval = currentYear - previousWin;

                ProducerIntervalItemResponse item = new ProducerIntervalItemResponse(
                        producer,
                        interval,
                        previousWin,
                        currentYear
                );

                if (minInterval == null || interval < minInterval) {
                    minInterval = interval;
                    minList.clear();
                    minList.add(item);
                } else if (interval == minInterval) {
                    minList.add(item);
                }

                if (maxInterval == null || interval > maxInterval) {
                    maxInterval = interval;
                    maxList.clear();
                    maxList.add(item);
                } else if (interval == maxInterval) {
                    maxList.add(item);
                }
            }
        }

        sortResult(minList);
        sortResult(maxList);

        return new ProducerIntervalResponse(minList, maxList);
    }

    private void sortResult(List<ProducerIntervalItemResponse> items) {
        items.sort(
                Comparator.comparing(ProducerIntervalItemResponse::producer)
                        .thenComparingInt(ProducerIntervalItemResponse::previousWin)
        );
    }

    private List<String> splitProducers(String producers) {
        List<String> result = new ArrayList<>();

        if (producers == null || producers.isBlank()) {
            return result;
        }

        String normalized = normalizeProducers(producers);

        for (String part : normalized.split(",")) {
            String producer = part.trim();
            if (!producer.isEmpty()) {
                result.add(producer);
            }
        }

        return result;
    }

    private String normalizeProducers(String producers) {
        return producers.trim()
                .replaceAll("\\s+and\\s+", ",")
                .replaceAll("\\s*,\\s*", ",");
    }

}
