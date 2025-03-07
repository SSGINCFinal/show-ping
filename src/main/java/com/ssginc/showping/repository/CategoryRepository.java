package com.ssginc.showping.repository;

import com.ssginc.showping.entity.CartId;
import com.ssginc.showping.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}