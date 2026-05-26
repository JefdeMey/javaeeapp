package be.ucll.repositories;

import java.util.List;
import java.util.Optional;

import be.ucll.domain.Product;

public interface ProductRepository {

	List<Product> findAll();

	Optional<Product> findById(Long id);

	List<Product> findByNaamContaining(String zoekterm);

	Product save(Product product);
}
