package org.pio.backend.repository;

import org.pio.backend.entity.Book;
import org.pio.backend.entity.BorrowRecord;
import org.pio.backend.entity.BorrowStatus;
import org.pio.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findAllByUser(User user);
    boolean existsByUserAndBookAndStatus(User user, Book book, BorrowStatus status);
}
