package be.ucll.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bestelling_regel")
public class BestellingRegel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bestelling_id", nullable = false)
	private Bestelling bestelling;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private int aantal;

	// Prijs vastgelegd op het moment van bestelling, onafhankelijk van latere prijswijzigingen
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal eenheidsprijs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Bestelling getBestelling() {
		return bestelling;
	}

	public void setBestelling(Bestelling bestelling) {
		this.bestelling = bestelling;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getAantal() {
		return aantal;
	}

	public void setAantal(int aantal) {
		this.aantal = aantal;
	}

	public BigDecimal getEenheidsprijs() {
		return eenheidsprijs;
	}

	public void setEenheidsprijs(BigDecimal eenheidsprijs) {
		this.eenheidsprijs = eenheidsprijs;
	}

	public BigDecimal getRegelTotaal() {
		return eenheidsprijs.multiply(BigDecimal.valueOf(aantal));
	}
}
