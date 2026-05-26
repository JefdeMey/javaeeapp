package be.ucll.repositories;

import java.util.List;
import java.util.Optional;

import be.ucll.domain.Gebruiker;

public interface GebruikerRepository {

	List<Gebruiker> findAll();

	Optional<Gebruiker> findById(Long id);

	Optional<Gebruiker> findByGebruikersnaam(String gebruikersnaam);

	Gebruiker save(Gebruiker gebruiker);
}
