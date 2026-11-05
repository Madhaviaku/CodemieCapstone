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
            SELECT b
            FROM Book b
            WHERE (:genre IS NULL OR LOWER(b.genre) = LOWER(:genre))
              AND (
                :q IS NULL OR :q = ''
                OR LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<Book> searchCatalog(@Param("q") String q, @Param("genre") String genre, Pageable pageable);
}
