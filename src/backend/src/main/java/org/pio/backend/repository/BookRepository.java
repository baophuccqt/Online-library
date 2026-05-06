package org.pio.backend.repository;

import org.pio.backend.entity.Book;
import org.pio.backend.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Page<Book> findAllByCategoriesContaining(Category category, Pageable pageable);
}
