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

    boolean existsByIsbn(String isbn);

    @query("""
        select b from Book b
        where
        ( :n=bull or :q='' or (lower(b.title) like lower(concat('%', :q, '%'))
          or lower(b.author) like lower(concat('%', :q, '%'))
          or lower(b.isbn) like lower(concat('%', :q, '%')))
        and (: genre is null or :genre='' or b.genre = :genre)
        and (: yearMin is null or function('YEARK', b.publicationDate) >= :yearMin)
        and (: yearMax is null or function('YEARK', b.publicationDate) <= :yearMax)
        """)
    Page<Book> search(
            @Param("q") String q,
            @Param("genre") String genre,
            @Param("yearMin") Integer yearMin,
            @Param("yearMax") Integer yearMax,
            Pageable pageable
    );
}
