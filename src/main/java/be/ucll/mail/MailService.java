package be.ucll.mail;

import java.util.List;

import be.ucll.domain.Bestelling;

public interface MailService {

	void stuurZoekResultaat(String emailAdres, String gebruikerNaam, List<Bestelling> bestellingen);
}
