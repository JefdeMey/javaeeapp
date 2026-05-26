package be.ucll.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import be.ucll.domain.Bestelling;
import be.ucll.domain.BestellingStatus;
import be.ucll.domain.Gebruiker;

public interface BestellingService {

	List<Bestelling> findAll();

	Optional<Bestelling> findById(Long id);

	List<Bestelling> findByGebruiker(Gebruiker gebruiker);

	List<Bestelling> findByStatus(BestellingStatus status);

	List<Bestelling> zoek(BigDecimal minBedrag, BigDecimal maxBedrag, Integer aantalProducten,
			boolean afgeleverd, String productNaam, String emailAdres);

	Bestelling save(Bestelling bestelling);
}
