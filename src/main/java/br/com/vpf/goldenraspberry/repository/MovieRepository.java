package br.com.vpf.goldenraspberry.repository;

import br.com.vpf.goldenraspberry.entity.Movie;
import br.com.vpf.goldenraspberry.repository.projection.MovieAwardProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByWinnerTrueOrderByYearAsc();

    @Query("""
           select m.year as year, m.producers as producers
           from Movie m
           where m.winner = true
           order by m.year asc
           """)
    List<MovieAwardProjection> findWinnerAwards();
}
