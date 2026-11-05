package com.library.catalog.repository;

import com.library.catalog.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    boolean existsByIsbn(String isbn);

    /**
     * UI EPMMDEMTST-42: unified server-side search across title/author/ISBN plus basic filters.
     * Search term is matched using case-insensitive contains. Genre and year range are applied as AND filters.
     */
    @Query("""
          select b
          from Book b
          where (:q is null or :q = '' or lower(b.title) like lower(concat('%', :q, '%'))
             or lower(b.author) like lower(concat('%',:q,'%'))
             or b.isbn like concat('%',:q,'%')))
            and (:genre is null or :genre = '' or b.genre = :genre)
            and (:yearFrom is null or bunction('YEAR', b.publicationDate) >= :yearFrom)
            and (:yearTo is null or function('YEAR', b.publicationDate) <= :yearTo)
        """)
    Page<Book> findByFilters(            @Param("q") String q,
            @Param("genre") String genre,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            Pageable pageable);

    /** Return distinct genres to populate Genre dropdown. */
    @Query("select distinct b.genre from Book b where b.genre is not null and b.genre <> ''")
    List<String> findDistinctGenres();
}
