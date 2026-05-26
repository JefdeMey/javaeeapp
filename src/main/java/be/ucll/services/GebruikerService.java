package be.ucll.services;

import java.util.List;
import java.util.Optional;

import be.ucll.domain.Gebruiker;

public interface GebruikerService {

	List<Gebruiker> findAll();

	Optional<Gebruiker> findById(Long id);

	/**
	 * Controleert gebruikersnaam en wachtwoord. Geeft de Gebruiker terug indien correct,
	 * anders een lege Optional.
	 */
	Optional<Gebruiker> login(String gebruikersnaam, String wachtwoord);

	Gebruiker save(Gebruiker gebruiker);
}
