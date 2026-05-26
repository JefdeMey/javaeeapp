package be.ucll.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HeaderComponent extends HorizontalLayout {

	public HeaderComponent() {
		setWidthFull();
		setAlignItems(Alignment.CENTER);
		setPadding(true);
		setSpacing(true);
		getStyle()
				.set("background-color", "var(--lumo-primary-color)")
				.set("color", "white")
				.set("box-shadow", "0 2px 4px rgba(0,0,0,0.2)");

		Image logo = new Image("images/logo.svg", "TechShop logo");
		logo.setHeight("52px");

		Span tagline = new Span("Uw betrouwbare technologiepartner");
		tagline.getStyle()
				.set("color", "rgba(255,255,255,0.8)")
				.set("font-style", "italic")
				.set("font-size", "var(--lumo-font-size-s)");

		Button uitloggenKnop = new Button("Uitloggen");
		uitloggenKnop.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		uitloggenKnop.getStyle().set("color", "white");
		uitloggenKnop.setVisible(SessionBeheer.isIngelogd());
		uitloggenKnop.addClickListener(e -> {
			SessionBeheer.uitloggen();
			UI.getCurrent().navigate(LoginView.class);
		});

		add(logo, tagline, uitloggenKnop);
		expand(tagline);
	}
}
