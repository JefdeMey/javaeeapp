package be.ucll.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.ucll.domain.Gebruiker;
import be.ucll.repositories.GebruikerRepository;

@Service
@Transactional
public class GebruikerServiceImpl implements GebruikerService {

	@Autowired
	private GebruikerRepository gebruikerRepository;

	@Override
	public List<Gebruiker> findAll() {
		return gebruikerRepository.findAll();
	}

	@Override
	public Optional<Gebruiker> findById(Long id) {
		return gebruikerRepository.findById(id);
	}

	@Override
	public Optional<Gebruiker> login(String gebruikersnaam, String wachtwoord) {
		return gebruikerRepository.findByGebruikersnaam(gebruikersnaam)
				.filter(g -> g.getWachtwoord().equals(wachtwoord));
	}

	@Override
	public Gebruiker save(Gebruiker gebruiker) {
		return gebruikerRepository.save(gebruiker);
	}
}
