package org.pio.backend.repository;

import org.pio.backend.entity.Book;
import org.pio.backend.entity.Review;
import org.pio.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(
            value = "SELECT r FROM Review r " +
                    "JOIN FETCH r.user " +
                    "JOIN FETCH r.book " +
                    "WHERE r.book = :book",
            countQuery = "SELECT count(r) FROM Review r WHERE r.book = :book"
    )
    Page<Review> findAllByBook(Book book, Pageable pageable);
    boolean existsByUserAndBook(User user, Book book);
}
