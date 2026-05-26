package be.ucll.ui;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class FooterComponent extends HorizontalLayout {

	public FooterComponent() {
		setWidthFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		setPadding(true);
		getStyle()
				.set("background-color", "var(--lumo-contrast-5pct)")
				.set("border-top", "1px solid var(--lumo-contrast-10pct)");

		Span disclaimer = new Span(
				"\u00A9 2026 TechShop BV. Alle rechten voorbehouden. "
				+ "Niet reproduceren of verspreiden zonder schriftelijke toestemming van TechShop BV.");
		disclaimer.getStyle()
				.set("color", "var(--lumo-secondary-text-color)")
				.set("font-size", "var(--lumo-font-size-xs)");

		add(disclaimer);
	}
}
