package be.ucll.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import be.ucll.domain.Bestelling;
import be.ucll.domain.BestellingStatus;
import be.ucll.jms.BestellingProducer;
import be.ucll.jms.ZoekResultaatBericht;
import be.ucll.services.BestellingService;
import be.ucll.services.ProductService;

@Route(value = "zoek", layout = MainLayout.class)
@PageTitle("Bestellingen zoeken")
public class ZoekView extends VerticalLayout implements BeforeEnterObserver {

	private static final String EMAIL_PATROON = "[a-zA-Z]+@[a-zA-Z]+\\.[a-zA-Z]{2,}";

	@Autowired
	private BestellingService bestellingService;

	@Autowired
	private ProductService productService;

	@Autowired
	private BestellingProducer bestellingProducer;

	private final TextField minBedragVeld = new TextField("Minimum bedrag");
	private final TextField maxBedragVeld = new TextField("Maximum bedrag");
	private final TextField aantalProductenVeld = new TextField("Aantal producten");
	private final Checkbox afgeleverdCheckbox = new Checkbox("Afgeleverd");
	private final ComboBox<String> productNaamVeld = new ComboBox<>("Product naam");
	private final TextField emailAdresVeld = new TextField("Email adres");

	private final Grid<Bestelling> grid = new Grid<>();
	private final VerticalLayout resultatenKaart = new VerticalLayout();
	private final Paragraph geenCriteriaFout = new Paragraph("Geef ten minste één zoekcriteria op.");
	private List<Bestelling> huidigeResultaten = new ArrayList<>();

	public ZoekView() {
		setSizeFull();
		setPadding(true);
		setSpacing(true);
		getStyle().set("background-color", "var(--lumo-contrast-5pct)");

		add(bouwZoekKaart(), bouwResultatenKaart());

		addAttachListener(e -> laadProductNamen());
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (!SessionBeheer.isIngelogd()) {
			event.rerouteTo(LoginView.class);
		}
	}

	private VerticalLayout bouwZoekKaart() {
		H3 titel = new H3("Bestellingen zoeken");
		titel.getStyle()
				.set("margin", "0 0 var(--lumo-space-m) 0")
				.set("color", "var(--lumo-primary-color)");

		VerticalLayout kaart = new VerticalLayout(titel, bouwZoekFormulier());
		kaart.setPadding(true);
		kaart.setSpacing(false);
		kaart.setWidthFull();
		kaart.getStyle()
				.set("background", "white")
				.set("border-radius", "var(--lumo-border-radius-l)")
				.set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
				.set("padding", "var(--lumo-space-l)");
		return kaart;
	}

	private FormLayout bouwZoekFormulier() {
		configureerVelden();

		Button wissenKnop = new Button("Wissen");
		wissenKnop.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		wissenKnop.addClickListener(e -> wisVelden());

		Button zoekenKnop = new Button("Zoeken");
		zoekenKnop.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		zoekenKnop.addClickListener(e -> voerZoekopdrachtUit());

		geenCriteriaFout.getStyle()
				.set("color", "var(--lumo-error-color)")
				.set("margin", "0");
		geenCriteriaFout.setVisible(false);

		HorizontalLayout knoppen = new HorizontalLayout(geenCriteriaFout, wissenKnop, zoekenKnop);
		knoppen.setWidthFull();
		knoppen.setAlignItems(FlexComponent.Alignment.CENTER);
		knoppen.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		knoppen.expand(geenCriteriaFout);

		FormLayout formulier = new FormLayout();
		formulier.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1),
				new FormLayout.ResponsiveStep("480px", 2),
				new FormLayout.ResponsiveStep("800px", 3));
		formulier.add(minBedragVeld, maxBedragVeld, aantalProductenVeld,
				productNaamVeld, emailAdresVeld, afgeleverdCheckbox);
		formulier.add(knoppen, 3);
		return formulier;
	}

	private void configureerVelden() {
		minBedragVeld.setPlaceholder("bv. 10.00");
		minBedragVeld.setPrefixComponent(new com.vaadin.flow.component.html.Span("€"));
		minBedragVeld.setWidthFull();

		maxBedragVeld.setPlaceholder("bv. 500.00");
		maxBedragVeld.setPrefixComponent(new com.vaadin.flow.component.html.Span("€"));
		maxBedragVeld.setWidthFull();

		aantalProductenVeld.setPlaceholder("bv. 2");
		aantalProductenVeld.setWidthFull();

		emailAdresVeld.setPlaceholder("bv. naam@domein.be");
		emailAdresVeld.setWidthFull();

		productNaamVeld.setWidthFull();
		productNaamVeld.setAllowCustomValue(true);
		productNaamVeld.addCustomValueSetListener(e -> productNaamVeld.setValue(e.getDetail()));

		afgeleverdCheckbox.getStyle().set("margin-top", "var(--lumo-space-m)");

		minBedragVeld.addBlurListener(e -> valideerDecimaalVeld(minBedragVeld));
		maxBedragVeld.addBlurListener(e -> valideerDecimaalVeld(maxBedragVeld));
		aantalProductenVeld.addBlurListener(e -> valideerGeheelGetalVeld(aantalProductenVeld));
		emailAdresVeld.addBlurListener(e -> valideerEmailVeld(emailAdresVeld));
	}

	private VerticalLayout bouwResultatenKaart() {
		bouwGrid();

		H4 titel = new H4("Gevonden bestellingen");
		titel.getStyle()
				.set("margin", "0 0 var(--lumo-space-s) 0")
				.set("color", "var(--lumo-primary-color)");

		Button emailKnop = new Button("Stuur resultaten per e-mail");
		emailKnop.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		emailKnop.addClickListener(e -> verstuurEmail());

		HorizontalLayout actieBalk = new HorizontalLayout(emailKnop);
		actieBalk.setWidthFull();
		actieBalk.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

		resultatenKaart.add(titel, grid, actieBalk);
		resultatenKaart.setPadding(true);
		resultatenKaart.setSpacing(true);
		resultatenKaart.setWidthFull();
		resultatenKaart.getStyle()
				.set("background", "white")
				.set("border-radius", "var(--lumo-border-radius-l)")
				.set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
				.set("padding", "var(--lumo-space-l)");
		resultatenKaart.setVisible(false);
		return resultatenKaart;
	}

	private void bouwGrid() {
		grid.addColumn(Bestelling::getId)
				.setHeader("Bestel ID").setAutoWidth(true).setFlexGrow(0);
		grid.addColumn(b -> b.getGebruiker().getId())
				.setHeader("Klantnr").setAutoWidth(true).setFlexGrow(0);
		grid.addColumn(b -> b.getRegels().size())
				.setHeader("#Producten").setAutoWidth(true).setFlexGrow(0);
		grid.addColumn(b -> b.getStatus() == BestellingStatus.VERZONDEN ? "Ja" : "Nee")
				.setHeader("Afgeleverd?").setAutoWidth(true).setFlexGrow(0);
		grid.addColumn(b -> "€ " + b.getTotaalBedrag())
				.setHeader("Totaal").setAutoWidth(true).setFlexGrow(1);
		grid.addComponentColumn(b -> {
			Button detailKnop = new Button("Detail");
			detailKnop.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
			detailKnop.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DetailView.class, b.getId())));
			return detailKnop;
		}).setHeader("").setAutoWidth(true).setFlexGrow(0);

		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
		grid.setAllRowsVisible(true);
		grid.setWidthFull();
	}

	private void wisVelden() {
		minBedragVeld.clear(); minBedragVeld.setInvalid(false);
		maxBedragVeld.clear(); maxBedragVeld.setInvalid(false);
		aantalProductenVeld.clear(); aantalProductenVeld.setInvalid(false);
		afgeleverdCheckbox.setValue(false);
		productNaamVeld.clear();
		emailAdresVeld.clear(); emailAdresVeld.setInvalid(false);
		geenCriteriaFout.setVisible(false);
		resultatenKaart.setVisible(false);
	}

	private void voerZoekopdrachtUit() {
		if (heeftValidatiefouten()) return;
		if (!heeftCriteria()) {
			geenCriteriaFout.setVisible(true);
			return;
		}
		geenCriteriaFout.setVisible(false);

		huidigeResultaten = bestellingService.zoek(
				parseerDecimaal(minBedragVeld.getValue()),
				parseerDecimaal(maxBedragVeld.getValue()),
				parseerGeheelGetal(aantalProductenVeld.getValue()),
				afgeleverdCheckbox.getValue(),
				productNaamVeld.getValue(),
				emailAdresVeld.getValue().trim());
		grid.setItems(huidigeResultaten);
		resultatenKaart.setVisible(true);
	}

	private boolean heeftValidatiefouten() {
		return minBedragVeld.isInvalid() || maxBedragVeld.isInvalid()
				|| aantalProductenVeld.isInvalid() || emailAdresVeld.isInvalid();
	}

	private boolean heeftCriteria() {
		return !minBedragVeld.getValue().isBlank()
				|| !maxBedragVeld.getValue().isBlank()
				|| !aantalProductenVeld.getValue().isBlank()
				|| afgeleverdCheckbox.getValue()
				|| (productNaamVeld.getValue() != null && !productNaamVeld.getValue().isBlank())
				|| !emailAdresVeld.getValue().isBlank();
	}

	private void verstuurEmail() {
		if (huidigeResultaten.isEmpty()) {
			Notification.show("Geen bestellingen om te versturen.");
			return;
		}
		List<Long> ids = huidigeResultaten.stream().map(Bestelling::getId).collect(Collectors.toList());
		String emailAdres = SessionBeheer.getGebruiker().getEmail();
		String naam = SessionBeheer.getGebruiker().getVolledigeNaam();
		bestellingProducer.verstuurZoekResultaat(new ZoekResultaatBericht(ids, emailAdres, naam));
		Notification melding = Notification.show("E-mail wordt verstuurd naar " + emailAdres);
		melding.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		melding.setDuration(3000);
	}

	private void laadProductNamen() {
		List<String> namen = productService.findAll().stream()
				.map(p -> p.getNaam())
				.collect(Collectors.toList());
		productNaamVeld.setItems(namen);
	}

	private void valideerDecimaalVeld(TextField veld) {
		String waarde = veld.getValue().trim();
		if (!waarde.isEmpty() && !waarde.matches("\\d+(\\.\\d+)?")) {
			veld.setInvalid(true);
			veld.setErrorMessage("Voer een geldig decimaal getal in (bv. 99.99)");
		} else {
			veld.setInvalid(false);
		}
	}

	private void valideerGeheelGetalVeld(TextField veld) {
		String waarde = veld.getValue().trim();
		if (!waarde.isEmpty() && !waarde.matches("\\d+")) {
			veld.setInvalid(true);
			veld.setErrorMessage("Voer een geldig geheel getal in");
		} else {
			veld.setInvalid(false);
		}
	}

	private void valideerEmailVeld(TextField veld) {
		String waarde = veld.getValue().trim();
		if (!waarde.isEmpty() && !waarde.matches(EMAIL_PATROON)) {
			veld.setInvalid(true);
			veld.setErrorMessage("Ongeldig e-mailadres (bv. naam@domein.be)");
		} else {
			veld.setInvalid(false);
		}
	}

	private BigDecimal parseerDecimaal(String waarde) {
		if (waarde == null || waarde.isBlank()) return null;
		try { return new BigDecimal(waarde.trim()); } catch (NumberFormatException e) { return null; }
	}

	private Integer parseerGeheelGetal(String waarde) {
		if (waarde == null || waarde.isBlank()) return null;
		try { return Integer.parseInt(waarde.trim()); } catch (NumberFormatException e) { return null; }
	}
}
