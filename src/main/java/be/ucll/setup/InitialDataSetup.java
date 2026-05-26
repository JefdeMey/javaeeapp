package be.ucll.setup;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import be.ucll.domain.Bestelling;
import be.ucll.domain.BestellingRegel;
import be.ucll.domain.BestellingStatus;
import be.ucll.domain.Gebruiker;
import be.ucll.domain.Product;

@Component
public class InitialDataSetup {

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void setup() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		transactionTemplate.execute(e -> {

			// Producten
			Product laptop = nieuwProduct("Laptop Pro 15", "Krachtige laptop met 16GB RAM en 512GB SSD", new BigDecimal("1299.99"), 10);
			Product muis = nieuwProduct("Draadloze Muis", "Ergonomische draadloze muis met lange batterijduur", new BigDecimal("49.99"), 50);
			Product toetsenbord = nieuwProduct("Mechanisch Toetsenbord", "RGB mechanisch toetsenbord met blauwe switches", new BigDecimal("129.99"), 25);
			Product monitor = nieuwProduct("4K Monitor 27\"", "Ultra HD IPS monitor met 144Hz verversingssnelheid", new BigDecimal("699.99"), 8);
			Product headset = nieuwProduct("Gaming Headset", "Surround sound headset met noise-cancelling microfoon", new BigDecimal("89.99"), 30);

			entityManager.persist(laptop);
			entityManager.persist(muis);
			entityManager.persist(toetsenbord);
			entityManager.persist(monitor);
			entityManager.persist(headset);

			// Gebruikers
			Gebruiker jan = nieuwGebruiker("jan.janssen", "wachtwoord123", "jefdemey@gmail.com", "Jan", "Janssen");
			Gebruiker marie = nieuwGebruiker("marie.peeters", "wachtwoord456", "jefdemey@gmail.com", "Marie", "Peeters");
			Gebruiker admin = nieuwGebruiker("admin", "admin", "jefdemey@gmail.com", "Admin", "Beheerder");
			Gebruiker test = nieuwGebruiker("test", "test", "jefdemey@gmail.com", "Test", "Gebruiker");

			entityManager.persist(jan);
			entityManager.persist(marie);
			entityManager.persist(admin);
			entityManager.persist(test);

			// Bestellingen voor Jan
			Bestelling bestelling1 = nieuweBestelling(jan, BestellingStatus.VERZONDEN, LocalDateTime.now().minusDays(10));
			voegRegelToe(bestelling1, laptop, 1, laptop.getPrijs());
			voegRegelToe(bestelling1, muis, 2, muis.getPrijs());
			entityManager.persist(bestelling1);

			Bestelling bestelling2 = nieuweBestelling(jan, BestellingStatus.NIEUW, LocalDateTime.now().minusDays(1));
			voegRegelToe(bestelling2, toetsenbord, 1, toetsenbord.getPrijs());
			voegRegelToe(bestelling2, headset, 1, headset.getPrijs());
			entityManager.persist(bestelling2);

			// Bestelling voor Marie
			Bestelling bestelling3 = nieuweBestelling(marie, BestellingStatus.VERWERKT, LocalDateTime.now().minusDays(3));
			voegRegelToe(bestelling3, monitor, 1, monitor.getPrijs());
			voegRegelToe(bestelling3, muis, 1, muis.getPrijs());
			entityManager.persist(bestelling3);

			return null;
		});
	}

	private Product nieuwProduct(String naam, String beschrijving, BigDecimal prijs, int stock) {
		Product p = new Product();
		p.setNaam(naam);
		p.setBeschrijving(beschrijving);
		p.setPrijs(prijs);
		p.setStock(stock);
		return p;
	}

	private Gebruiker nieuwGebruiker(String gebruikersnaam, String wachtwoord, String email, String voornaam, String achternaam) {
		Gebruiker g = new Gebruiker();
		g.setGebruikersnaam(gebruikersnaam);
		g.setWachtwoord(wachtwoord);
		g.setEmail(email);
		g.setVoornaam(voornaam);
		g.setAchternaam(achternaam);
		return g;
	}

	private Bestelling nieuweBestelling(Gebruiker gebruiker, BestellingStatus status, LocalDateTime datum) {
		Bestelling b = new Bestelling();
		b.setGebruiker(gebruiker);
		b.setStatus(status);
		b.setDatum(datum);
		return b;
	}

	private void voegRegelToe(Bestelling bestelling, Product product, int aantal, BigDecimal eenheidsprijs) {
		BestellingRegel regel = new BestellingRegel();
		regel.setBestelling(bestelling);
		regel.setProduct(product);
		regel.setAantal(aantal);
		regel.setEenheidsprijs(eenheidsprijs);
		bestelling.getRegels().add(regel);
	}
}
