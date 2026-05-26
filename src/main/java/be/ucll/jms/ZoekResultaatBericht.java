package be.ucll.jms;

import java.io.Serializable;
import java.util.List;

public class ZoekResultaatBericht implements Serializable {

	private List<Long> bestellingIds;
	private String emailAdres;
	private String gebruikerNaam;

	public ZoekResultaatBericht() {
	}

	public ZoekResultaatBericht(List<Long> bestellingIds, String emailAdres, String gebruikerNaam) {
		this.bestellingIds = bestellingIds;
		this.emailAdres = emailAdres;
		this.gebruikerNaam = gebruikerNaam;
	}

	public List<Long> getBestellingIds() {
		return bestellingIds;
	}

	public void setBestellingIds(List<Long> bestellingIds) {
		this.bestellingIds = bestellingIds;
	}

	public String getEmailAdres() {
		return emailAdres;
	}

	public void setEmailAdres(String emailAdres) {
		this.emailAdres = emailAdres;
	}

	public String getGebruikerNaam() {
		return gebruikerNaam;
	}

	public void setGebruikerNaam(String gebruikerNaam) {
		this.gebruikerNaam = gebruikerNaam;
	}
}
