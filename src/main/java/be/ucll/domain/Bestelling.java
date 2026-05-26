package be.ucll.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "bestelling")
public class Bestelling {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDateTime datum;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BestellingStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "gebruiker_id", nullable = false)
	private Gebruiker gebruiker;

	@OneToMany(mappedBy = "bestelling", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<BestellingRegel> regels = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDatum() {
		return datum;
	}

	public void setDatum(LocalDateTime datum) {
		this.datum = datum;
	}

	public BestellingStatus getStatus() {
		return status;
	}

	public void setStatus(BestellingStatus status) {
		this.status = status;
	}

	public Gebruiker getGebruiker() {
		return gebruiker;
	}

	public void setGebruiker(Gebruiker gebruiker) {
		this.gebruiker = gebruiker;
	}

	public List<BestellingRegel> getRegels() {
		return regels;
	}

	public void setRegels(List<BestellingRegel> regels) {
		this.regels = regels;
	}

	public BigDecimal getTotaalBedrag() {
		return regels.stream()
				.map(BestellingRegel::getRegelTotaal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
