package org.pio.backend.repository;

import org.pio.backend.entity.Book;
import org.pio.backend.entity.Review;
import org.pio.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByBook(Book book, Pageable pageable);
    boolean existsByUserAndBook(User user, Book book);
}
