package be.ucll.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.ucll.domain.Product;
import be.ucll.repositories.ProductRepository;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Override
	public Optional<Product> findById(Long id) {
		return productRepository.findById(id);
	}

	@Override
	public List<Product> zoek(String zoekterm) {
		if (zoekterm == null || zoekterm.isBlank()) {
			return productRepository.findAll();
		}
		return productRepository.findByNaamContaining(zoekterm);
	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
	}
}
