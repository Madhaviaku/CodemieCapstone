package com.library.catalog.repository;

import com.library.catalog.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("""
            SELECT b
            FROM Book b
            WHERE (:q IS NULL OR :q = '' OR (
                LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(b.isbn) LIKE LOWER(CONCAT('%', :q, '%'))
            ))
            AND (:genre IS NULL OR :genre = '' OR LOWER(b.genre) = LOWER(:genre))
            AND (:yearFrom IS NULL OR b.publicationDate IS NOT NULL AND b.publicationDate >= :yearFrom)
            AND (:yearTo IS NULL OR b.publicationDate IS NOT NULL AND b.publicationDate <= :yearTo)
            """)
    Page<Book> search(@Param("q") String q,
                      @Param("genre") String genre,
                      @Param("yearFrom") LocalDate yearFrom,
                      @Param("yearTo") LocalDate yearTo,
                      Pageable pageable);

    boolean existsByIsbn(String isbn);
}
