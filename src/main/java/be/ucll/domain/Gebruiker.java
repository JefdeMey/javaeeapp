package be.ucll.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "gebruiker")
public class Gebruiker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String gebruikersnaam;

	@Column(nullable = false)
	private String wachtwoord;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String voornaam;

	@Column(nullable = false)
	private String achternaam;

	@OneToMany(mappedBy = "gebruiker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Bestelling> bestellingen = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGebruikersnaam() {
		return gebruikersnaam;
	}

	public void setGebruikersnaam(String gebruikersnaam) {
		this.gebruikersnaam = gebruikersnaam;
	}

	public String getWachtwoord() {
		return wachtwoord;
	}

	public void setWachtwoord(String wachtwoord) {
		this.wachtwoord = wachtwoord;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public List<Bestelling> getBestellingen() {
		return bestellingen;
	}

	public void setBestellingen(List<Bestelling> bestellingen) {
		this.bestellingen = bestellingen;
	}

	public String getVolledigeNaam() {
		return voornaam + " " + achternaam;
	}
}
