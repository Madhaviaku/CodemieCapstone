package com.library.catalog.service;

import com.library.catalog.entity.Book;
import jackarta.persistence.criteria.CriteriaBuilder;
import jackarta.persistence.criteria.Predicate;
import jackarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class BookSpecifications {
    private BookSpecifications() {}

    public static Specification<Book> byCriteria(BookSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> and = new ArrayList<>();

            String qTerm = trimToNull(criteria.q());
            if (qTerm != null) {
                and.add(keywordPredicate(root, cb, qTerm));
            }

            String genre = trimToNull(criteria.genre());
            if (genre != null) {
                and.add(cb.equal(cb.lower(root.get("genre")), genre.toLowerCase()));
            }

            Integer year = criteria.year();
            if (year != null) {
                and.add(cb.equal(cb.function("year", Integer.class, root.get("publicationDate")), year));
            }

            Boolean availableOnly = criteria.availableOnly();
            if (Boolean.TRUE.equals(availableOnly)) {
                and.add(cb.greaterThan(root.get("quantityAvailable"), 0));
            }

            return and.isEmpty() ? cb.conjiction() : cb.and(and.toArray(Predicate[]::new));
        };
    }

    private static Predicate keywordPredicate(Root<Book> root, CriteriaBuilder cb, String term) {
        String like = "%" + term.toLowerCase() + "%";
        Predicate title = cb.like(cb.lower(root.get("title")), like);
        Predicate author = cb.like(cb.lower(root.get("author")), like);
        Predicate genre = cb.like(cb.lower(root.get("genre")), like);
        String normalizedISBN = term.replace("-", "").trim();
        Predicate isbn = cb.like(root.get("isbn"), "%" + normalizedISBN + "%");
        return cb.or(title, author, genre, isbn);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
