package be.ucll.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import be.ucll.domain.Gebruiker;
import be.ucll.services.GebruikerService;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Inloggen")
public class LoginView extends VerticalLayout {

	@Autowired
	private GebruikerService gebruikerService;

	private final TextField gebruikersnaamVeld = new TextField("Gebruikersnaam");
	private final PasswordField wachtwoordVeld = new PasswordField("Wachtwoord");
	private final Paragraph foutmelding = new Paragraph();

	public LoginView() {
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		getStyle().set("background-color", "var(--lumo-contrast-5pct)");

		gebruikersnaamVeld.setWidthFull();
		gebruikersnaamVeld.setAutofocus(true);
		gebruikersnaamVeld.setPrefixComponent(new Span("👤"));

		wachtwoordVeld.setWidthFull();
		wachtwoordVeld.setPrefixComponent(new Span("🔒"));

		Button inloggenKnop = new Button("Inloggen");
		inloggenKnop.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
		inloggenKnop.setWidthFull();
		inloggenKnop.addClickShortcut(Key.ENTER);
		inloggenKnop.addClickListener(e -> inloggen());

		foutmelding.getStyle()
				.set("color", "var(--lumo-error-color)")
				.set("font-size", "var(--lumo-font-size-s)")
				.set("margin", "0");
		foutmelding.setVisible(false);

		H2 titel = new H2("Aanmelden");
		titel.getStyle()
				.set("margin-top", "0")
				.set("margin-bottom", "var(--lumo-space-m)")
				.set("color", "var(--lumo-primary-color)");

		VerticalLayout kaart = new VerticalLayout(titel, gebruikersnaamVeld, wachtwoordVeld, inloggenKnop, foutmelding);
		kaart.setSpacing(true);
		kaart.setPadding(true);
		kaart.setMaxWidth("420px");
		kaart.setWidthFull();
		kaart.getStyle()
				.set("background", "white")
				.set("border-radius", "var(--lumo-border-radius-l)")
				.set("box-shadow", "0 4px 16px rgba(0,0,0,0.12)")
				.set("padding", "var(--lumo-space-xl)");

		add(kaart);
	}

	private void inloggen() {
		String gebruikersnaam = gebruikersnaamVeld.getValue().trim();
		String wachtwoord = wachtwoordVeld.getValue();

		if (gebruikersnaam.isEmpty() || wachtwoord.isEmpty()) {
			toonFout("Vul gebruikersnaam en wachtwoord in.");
			return;
		}

		gebruikerService.login(gebruikersnaam, wachtwoord).ifPresentOrElse(
				this::navigeerNaarZoek,
				() -> toonFout("Ongeldige gebruikersnaam of wachtwoord."));
	}

	private void navigeerNaarZoek(Gebruiker gebruiker) {
		SessionBeheer.setGebruiker(gebruiker);
		getUI().ifPresent(ui -> ui.navigate(ZoekView.class));
	}

	private void toonFout(String bericht) {
		foutmelding.setText(bericht);
		foutmelding.setVisible(true);
		wachtwoordVeld.clear();
	}
}
