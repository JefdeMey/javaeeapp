package be.ucll.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.ucll.domain.Bestelling;
import be.ucll.domain.BestellingStatus;
import be.ucll.domain.Gebruiker;
import be.ucll.repositories.BestellingRepository;

@Service
@Transactional
public class BestellingServiceImpl implements BestellingService {

	@Autowired
	private BestellingRepository bestellingRepository;

	@Override
	public List<Bestelling> findAll() {
		return bestellingRepository.findAll();
	}

	@Override
	public Optional<Bestelling> findById(Long id) {
		return bestellingRepository.findById(id);
	}

	@Override
	public List<Bestelling> findByGebruiker(Gebruiker gebruiker) {
		return bestellingRepository.findByGebruiker(gebruiker);
	}

	@Override
	public List<Bestelling> findByStatus(BestellingStatus status) {
		return bestellingRepository.findByStatus(status);
	}

	@Override
	public List<Bestelling> zoek(BigDecimal minBedrag, BigDecimal maxBedrag, Integer aantalProducten,
			boolean afgeleverd, String productNaam, String emailAdres) {
		return bestellingRepository.zoek(minBedrag, maxBedrag, aantalProducten, afgeleverd, productNaam, emailAdres);
	}

	@Override
	public Bestelling save(Bestelling bestelling) {
		return bestellingRepository.save(bestelling);
	}
}
