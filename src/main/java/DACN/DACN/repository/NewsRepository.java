package DACN.DACN.repository;

import DACN.DACN.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE n.id < :id ORDER BY n.id DESC")
    List<News> findPreviousById(@Param("id") Long id);

    @Query("SELECT n FROM News n WHERE n.id > :id ORDER BY n.id ASC")
    List<News> findNextById(@Param("id") Long id);
}
