package br.com.vpf.goldenraspberry.repository;

import br.com.vpf.goldenraspberry.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByWinnerTrueOrderByYearAsc();
}
