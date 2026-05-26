package be.ucll.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import be.ucll.domain.Product;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Product> findAll() {
		return entityManager
				.createQuery("SELECT p FROM Product p ORDER BY p.naam", Product.class)
				.getResultList();
	}

	@Override
	public Optional<Product> findById(Long id) {
		return Optional.ofNullable(entityManager.find(Product.class, id));
	}

	@Override
	public List<Product> findByNaamContaining(String zoekterm) {
		return entityManager
				.createQuery("SELECT p FROM Product p WHERE LOWER(p.naam) LIKE LOWER(:zoekterm) ORDER BY p.naam", Product.class)
				.setParameter("zoekterm", "%" + zoekterm + "%")
				.getResultList();
	}

	@Override
	public Product save(Product product) {
		if (product.getId() == null) {
			entityManager.persist(product);
			return product;
		}
		return entityManager.merge(product);
	}
}
