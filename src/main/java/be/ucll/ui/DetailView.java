package be.ucll.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import be.ucll.domain.Bestelling;
import be.ucll.domain.BestellingRegel;
import be.ucll.domain.BestellingStatus;
import be.ucll.services.BestellingService;

@Route(value = "detail", layout = MainLayout.class)
@PageTitle("Bestelling detail")
public class DetailView extends VerticalLayout implements HasUrlParameter<Long>, BeforeEnterObserver {

	@Autowired
	private BestellingService bestellingService;

	private final VerticalLayout inhoud = new VerticalLayout();
	private Long bestellingId;

	public DetailView() {
		setSizeFull();
		setPadding(true);
		setSpacing(true);
		getStyle().set("background-color", "var(--lumo-contrast-5pct)");

		Button terugKnop = new Button("← Terug naar zoeken");
		terugKnop.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		terugKnop.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(ZoekView.class)));


		inhoud.setPadding(false);
		inhoud.setSpacing(true);
		inhoud.setWidthFull();

		add(terugKnop, inhoud);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (!SessionBeheer.isIngelogd()) {
			event.rerouteTo(LoginView.class);
		}
	}

	@Override
	public void setParameter(com.vaadin.flow.router.BeforeEvent event, Long id) {
		this.bestellingId = id;
		laadBestelling();
	}

	private void laadBestelling() {
		inhoud.removeAll();
		bestellingService.findById(bestellingId).ifPresentOrElse(
				this::toonBestelling,
				() -> inhoud.add(maakFoutKaart()));
	}

	private VerticalLayout maakFoutKaart() {
		Span bericht = new Span("Bestelling niet gevonden.");
		bericht.getStyle().set("color", "var(--lumo-error-color)");
		VerticalLayout kaart = maakKaart();
		kaart.add(bericht);
		return kaart;
	}

	private void toonBestelling(Bestelling bestelling) {
		inhoud.add(maakInfoKaart(bestelling), maakProductenKaart(bestelling));
	}

	private VerticalLayout maakInfoKaart(Bestelling bestelling) {
		H3 titel = new H3("Bestelling #" + bestelling.getId());
		titel.getStyle()
				.set("margin", "0 0 var(--lumo-space-s) 0")
				.set("color", "var(--lumo-primary-color)");

		Hr lijn = new Hr();
		lijn.getStyle().set("margin", "var(--lumo-space-s) 0");

		FormLayout info = new FormLayout();
		info.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1),
				new FormLayout.ResponsiveStep("480px", 2),
				new FormLayout.ResponsiveStep("800px", 3));

		info.addFormItem(maakWaarde(String.valueOf(bestelling.getId())), "Bestel ID");
		info.addFormItem(maakWaarde(String.valueOf(bestelling.getGebruiker().getId())), "Klantnummer");
		info.addFormItem(maakWaarde(String.valueOf(bestelling.getRegels().size())), "Aantal producten");
		info.addFormItem(maakStatusBadge(bestelling.getStatus()), "Afgeleverd");
		info.addFormItem(maakWaarde("€ " + bestelling.getTotaalBedrag()), "Totaalbedrag");
		info.addFormItem(maakWaarde(bestelling.getStatus().name()), "Status");

		VerticalLayout kaart = maakKaart();
		kaart.add(titel, lijn, info);
		return kaart;
	}

	private VerticalLayout maakProductenKaart(Bestelling bestelling) {
		H4 titel = new H4("Productdetails");
		titel.getStyle()
				.set("margin", "0 0 var(--lumo-space-s) 0")
				.set("color", "var(--lumo-primary-color)");

		Grid<BestellingRegel> regelGrid = new Grid<>();
		regelGrid.addColumn(r -> r.getProduct().getId())
				.setHeader("Product ID").setAutoWidth(true).setFlexGrow(0);
		regelGrid.addColumn(r -> r.getProduct().getNaam())
				.setHeader("Naam").setFlexGrow(2);
		regelGrid.addColumn(r -> r.getProduct().getBeschrijving())
				.setHeader("Beschrijving").setFlexGrow(3);
		regelGrid.addColumn(BestellingRegel::getAantal)
				.setHeader("Aantal").setAutoWidth(true).setFlexGrow(0);
		regelGrid.addColumn(r -> "€ " + r.getProduct().getPrijs())
				.setHeader("Eenheidsprijs").setAutoWidth(true).setFlexGrow(0);
		regelGrid.addColumn(r -> "€ " + r.getRegelTotaal())
				.setHeader("Regeltotaal").setAutoWidth(true).setFlexGrow(0);

		regelGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
		regelGrid.setItems(bestelling.getRegels());
		regelGrid.setAllRowsVisible(true);
		regelGrid.setWidthFull();

		VerticalLayout kaart = maakKaart();
		kaart.add(titel, regelGrid);
		return kaart;
	}

	private VerticalLayout maakKaart() {
		VerticalLayout kaart = new VerticalLayout();
		kaart.setWidthFull();
		kaart.setPadding(true);
		kaart.setSpacing(true);
		kaart.getStyle()
				.set("background", "white")
				.set("border-radius", "var(--lumo-border-radius-l)")
				.set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
				.set("padding", "var(--lumo-space-l)");
		return kaart;
	}

	private Span maakWaarde(String tekst) {
		Span s = new Span(tekst);
		s.getStyle().set("font-weight", "500");
		return s;
	}

	private HorizontalLayout maakStatusBadge(BestellingStatus status) {
		boolean afgeleverd = status == BestellingStatus.VERZONDEN;
		Span badge = new Span(afgeleverd ? "Ja" : "Nee");
		badge.getStyle()
				.set("background-color", afgeleverd ? "var(--lumo-success-color-10pct)" : "var(--lumo-error-color-10pct)")
				.set("color", afgeleverd ? "var(--lumo-success-text-color)" : "var(--lumo-error-text-color)")
				.set("padding", "2px 10px")
				.set("border-radius", "var(--lumo-border-radius-m)")
				.set("font-size", "var(--lumo-font-size-s)")
				.set("font-weight", "600");
		HorizontalLayout wrapper = new HorizontalLayout(badge);
		wrapper.setPadding(false);
		wrapper.setSpacing(false);
		return wrapper;
	}
}
