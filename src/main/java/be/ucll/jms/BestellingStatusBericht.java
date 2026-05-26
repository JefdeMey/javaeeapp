package be.ucll.jms;

import java.io.Serializable;

import be.ucll.domain.BestellingStatus;

public class BestellingStatusBericht implements Serializable {

	private Long bestellingId;
	private BestellingStatus nieuweStatus;
	private String gebruikerEmail;
	private String gebruikerNaam;

	public BestellingStatusBericht() {
	}

	public BestellingStatusBericht(Long bestellingId, BestellingStatus nieuweStatus,
			String gebruikerEmail, String gebruikerNaam) {
		this.bestellingId = bestellingId;
		this.nieuweStatus = nieuweStatus;
		this.gebruikerEmail = gebruikerEmail;
		this.gebruikerNaam = gebruikerNaam;
	}

	public Long getBestellingId() {
		return bestellingId;
	}

	public void setBestellingId(Long bestellingId) {
		this.bestellingId = bestellingId;
	}

	public BestellingStatus getNieuweStatus() {
		return nieuweStatus;
	}

	public void setNieuweStatus(BestellingStatus nieuweStatus) {
		this.nieuweStatus = nieuweStatus;
	}

	public String getGebruikerEmail() {
		return gebruikerEmail;
	}

	public void setGebruikerEmail(String gebruikerEmail) {
		this.gebruikerEmail = gebruikerEmail;
	}

	public String getGebruikerNaam() {
		return gebruikerNaam;
	}

	public void setGebruikerNaam(String gebruikerNaam) {
		this.gebruikerNaam = gebruikerNaam;
	}
}
