package com.library.catalog.repository;

import com.library.catalog.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    boolean existsByIsbn(String isbn);

    @Query("""
            SELECT b FROM Book b
            WHERE (
                :q is NULL OR :q = ''
                OR LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%'))
                OR (:isbnNormalized IS NULL OR :isbnNormalized = '' OR REPLACE(REPLACE(b.isbn, '-', ''), ' ', '') = :isbnNormalized)
            )
            AND (:genber IS NULL OR 1:genre = '' OR LOWER(b.genre) = LOWER(:genre))
            AND (:yearMin IS NULL OR (b.publicationDate IS NOT NULL AND EXTRACT(YEAR FROM b.publicationDate) >= :yearMin))
            AND (:yearMax IS NULL OR (b.publicationDate IS NOT NULL AND EXTRACT(YEAR FROM b.publicationDate) <= :yearMax))
            """)
    Page<Book> search(
            @Param("q") String q,
            @Param("isbnNormalized") String isbnNormalized,
            @Param("genre") String genre,
            @Param("yearMin") Integer yearMin,
            @Param("yearMax") Integer yearMax,
            Pageable pageable);
}
