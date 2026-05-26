package be.ucll.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import be.ucll.domain.Gebruiker;

@Repository
public class GebruikerRepositoryImpl implements GebruikerRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Gebruiker> findAll() {
		return entityManager
				.createQuery("SELECT g FROM Gebruiker g ORDER BY g.achternaam, g.voornaam", Gebruiker.class)
				.getResultList();
	}

	@Override
	public Optional<Gebruiker> findById(Long id) {
		return Optional.ofNullable(entityManager.find(Gebruiker.class, id));
	}

	@Override
	public Optional<Gebruiker> findByGebruikersnaam(String gebruikersnaam) {
		try {
			Gebruiker gebruiker = entityManager
					.createQuery("SELECT g FROM Gebruiker g WHERE g.gebruikersnaam = :gebruikersnaam", Gebruiker.class)
					.setParameter("gebruikersnaam", gebruikersnaam)
					.getSingleResult();
			return Optional.of(gebruiker);
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	@Override
	public Gebruiker save(Gebruiker gebruiker) {
		if (gebruiker.getId() == null) {
			entityManager.persist(gebruiker);
			return gebruiker;
		}
		return entityManager.merge(gebruiker);
	}
}
