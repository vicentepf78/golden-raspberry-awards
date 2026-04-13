package br.com.vpf.goldenraspberry.integration;

import br.com.vpf.goldenraspberry.entity.Movie;
import br.com.vpf.goldenraspberry.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProducerAwardIntervalScenarioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    @Test
    void shouldReturnTieForMinInterval() throws Exception {
        saveMovie(2000, "Film 1", "Studio 1", "Producer A", true);
        saveMovie(2001, "Film 2", "Studio 1", "Producer A", true);

        saveMovie(2010, "Film 3", "Studio 2", "Producer B", true);
        saveMovie(2011, "Film 4", "Studio 2", "Producer B", true);

        saveMovie(2000, "Film 5", "Studio 3", "Producer C", true);
        saveMovie(2010, "Film 6", "Studio 3", "Producer C", true);

        mockMvc.perform(get("/api/awards/producers/intervals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.min.length()").value(2))

                .andExpect(jsonPath("$.min[0].producer").value("Producer A"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.min[0].previousWin").value(2000))
                .andExpect(jsonPath("$.min[0].followingWin").value(2001))

                .andExpect(jsonPath("$.min[1].producer").value("Producer B"))
                .andExpect(jsonPath("$.min[1].interval").value(1))
                .andExpect(jsonPath("$.min[1].previousWin").value(2010))
                .andExpect(jsonPath("$.min[1].followingWin").value(2011))

                .andExpect(jsonPath("$.max").isArray())
                .andExpect(jsonPath("$.max.length()").value(1))
                .andExpect(jsonPath("$.max[0].producer").value("Producer C"))
                .andExpect(jsonPath("$.max[0].interval").value(10))
                .andExpect(jsonPath("$.max[0].previousWin").value(2000))
                .andExpect(jsonPath("$.max[0].followingWin").value(2010));
    }


    @Test
    void shouldReturnTieForMaxInterval() throws Exception {
        saveMovie(2000, "Film 1", "Studio 1", "Producer A", true);
        saveMovie(2010, "Film 2", "Studio 1", "Producer A", true);

        saveMovie(1995, "Film 3", "Studio 2", "Producer B", true);
        saveMovie(2005, "Film 4", "Studio 2", "Producer B", true);

        saveMovie(2018, "Film 5", "Studio 3", "Producer C", true);
        saveMovie(2019, "Film 6", "Studio 3", "Producer C", true);

        mockMvc.perform(get("/api/awards/producers/intervals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min.length()").value(1))
                .andExpect(jsonPath("$.min[0].producer").value("Producer C"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.min[0].previousWin").value(2018))
                .andExpect(jsonPath("$.min[0].followingWin").value(2019))
                .andExpect(jsonPath("$.max.length()").value(2))
                .andExpect(jsonPath("$.max[0].producer").value("Producer A"))
                .andExpect(jsonPath("$.max[0].interval").value(10))
                .andExpect(jsonPath("$.max[0].previousWin").value(2000))
                .andExpect(jsonPath("$.max[0].followingWin").value(2010))
                .andExpect(jsonPath("$.max[1].producer").value("Producer B"))
                .andExpect(jsonPath("$.max[1].interval").value(10))
                .andExpect(jsonPath("$.max[1].previousWin").value(1995))
                .andExpect(jsonPath("$.max[1].followingWin").value(2005));
    }

    @Test
    void shouldCalculateIntervalsForProducerWithMoreThanTwoWins() throws Exception {
        saveMovie(1990, "Film 1", "Studio 1", "Producer A", true);
        saveMovie(1993, "Film 2", "Studio 1", "Producer A", true);
        saveMovie(2000, "Film 3", "Studio 1", "Producer A", true);

        saveMovie(2001, "Film 4", "Studio 2", "Producer B", true);
        saveMovie(2002, "Film 5", "Studio 2", "Producer B", true);

        mockMvc.perform(get("/api/awards/producers/intervals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min.length()").value(1))
                .andExpect(jsonPath("$.min[0].producer").value("Producer B"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.min[0].previousWin").value(2001))
                .andExpect(jsonPath("$.min[0].followingWin").value(2002))
                .andExpect(jsonPath("$.max.length()").value(1))
                .andExpect(jsonPath("$.max[0].producer").value("Producer A"))
                .andExpect(jsonPath("$.max[0].interval").value(7))
                .andExpect(jsonPath("$.max[0].previousWin").value(1993))
                .andExpect(jsonPath("$.max[0].followingWin").value(2000));
    }

    @Test
    void shouldHandleMovieWithMultipleProducers() throws Exception {
        saveMovie(2000, "Film 1", "Studio 1", "Producer A, Producer B and Producer C", true);
        saveMovie(2001, "Film 2", "Studio 1", "Producer A", true);
        saveMovie(2010, "Film 3", "Studio 2", "Producer B", true);
        saveMovie(2020, "Film 4", "Studio 3", "Producer C", true);

        mockMvc.perform(get("/api/awards/producers/intervals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min[0].producer").value("Producer A"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.max[0].producer").value("Producer C"))
                .andExpect(jsonPath("$.max[0].interval").value(20));
    }

    @Test
    void shouldReturnEmptyListsWhenNoProducerHasTwoWins() throws Exception {
        saveMovie(2000, "Film 1", "Studio 1", "Producer A", true);
        saveMovie(2001, "Film 2", "Studio 2", "Producer B", true);
        saveMovie(2002, "Film 3", "Studio 3", "Producer C", true);

        mockMvc.perform(get("/api/awards/producers/intervals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min.length()").value(0))
                .andExpect(jsonPath("$.max.length()").value(0));
    }

    private void saveMovie(int year, String title, String studios, String producers, boolean winner) {
        Movie movie = new Movie();
        movie.setYear(year);
        movie.setTitle(title);
        movie.setStudios(studios);
        movie.setProducers(producers);
        movie.setWinner(winner);
        movieRepository.save(movie);
    }
}
