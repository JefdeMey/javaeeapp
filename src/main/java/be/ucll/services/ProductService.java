package be.ucll.services;

import java.util.List;
import java.util.Optional;

import be.ucll.domain.Product;

public interface ProductService {

	List<Product> findAll();

	Optional<Product> findById(Long id);

	List<Product> zoek(String zoekterm);

	Product save(Product product);
}
