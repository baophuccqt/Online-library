package org.pio.backend.repository;

import org.pio.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Optional<Category> findById(Long id);

    HashSet<Category> findAllByIdIn(Set<Long> ids);
    boolean existsByName(String name);
    boolean existsById(Long id);
}
