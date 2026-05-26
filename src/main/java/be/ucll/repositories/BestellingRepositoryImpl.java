package be.ucll.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import be.ucll.domain.Bestelling;
import be.ucll.domain.BestellingStatus;
import be.ucll.domain.Gebruiker;

@Repository
public class BestellingRepositoryImpl implements BestellingRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Bestelling> findAll() {
		return entityManager
				.createQuery("SELECT b FROM Bestelling b JOIN FETCH b.gebruiker ORDER BY b.datum DESC", Bestelling.class)
				.getResultList();
	}

	@Override
	public Optional<Bestelling> findById(Long id) {
		// JOIN FETCH om N+1 te vermijden bij het laden van regels en producten
		List<Bestelling> resultaten = entityManager
				.createQuery(
						"SELECT b FROM Bestelling b " +
						"JOIN FETCH b.gebruiker " +
						"LEFT JOIN FETCH b.regels r " +
						"LEFT JOIN FETCH r.product " +
						"WHERE b.id = :id", Bestelling.class)
				.setParameter("id", id)
				.getResultList();
		return resultaten.isEmpty() ? Optional.empty() : Optional.of(resultaten.get(0));
	}

	@Override
	public List<Bestelling> findByGebruiker(Gebruiker gebruiker) {
		return entityManager
				.createQuery("SELECT b FROM Bestelling b WHERE b.gebruiker = :gebruiker ORDER BY b.datum DESC", Bestelling.class)
				.setParameter("gebruiker", gebruiker)
				.getResultList();
	}

	@Override
	public List<Bestelling> findByStatus(BestellingStatus status) {
		return entityManager
				.createQuery("SELECT b FROM Bestelling b JOIN FETCH b.gebruiker WHERE b.status = :status ORDER BY b.datum DESC", Bestelling.class)
				.setParameter("status", status)
				.getResultList();
	}

	@Override
	public List<Bestelling> zoek(BigDecimal minBedrag, BigDecimal maxBedrag, Integer aantalProducten,
			boolean afgeleverd, String productNaam, String emailAdres) {
		List<Bestelling> alle = entityManager.createQuery(
				"SELECT DISTINCT b FROM Bestelling b " +
				"JOIN FETCH b.gebruiker " +
				"LEFT JOIN FETCH b.regels r " +
				"LEFT JOIN FETCH r.product",
				Bestelling.class).getResultList();

		return alle.stream()
				.filter(b -> emailAdres == null || emailAdres.isBlank()
						|| b.getGebruiker().getEmail().equalsIgnoreCase(emailAdres))
				.filter(b -> !afgeleverd || b.getStatus() == BestellingStatus.VERZONDEN)
				.filter(b -> productNaam == null || productNaam.isBlank()
						|| b.getRegels().stream().anyMatch(r ->
								r.getProduct().getNaam().toLowerCase().contains(productNaam.toLowerCase())))
				.filter(b -> aantalProducten == null || b.getRegels().size() == aantalProducten)
				.filter(b -> minBedrag == null || b.getTotaalBedrag().compareTo(minBedrag) >= 0)
				.filter(b -> maxBedrag == null || b.getTotaalBedrag().compareTo(maxBedrag) <= 0)
				.collect(Collectors.toList());
	}

	@Override
	public Bestelling save(Bestelling bestelling) {
		if (bestelling.getId() == null) {
			entityManager.persist(bestelling);
			return bestelling;
		}
		return entityManager.merge(bestelling);
	}
}
