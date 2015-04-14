package org.sef4j.testwebapp.repository;

import java.util.List;

import org.sef4j.testwebapp.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    List<ProductEntity> findAllByShortDescrLike(String shortDescrLike);

}
