package be.ucll.ui;

import com.vaadin.flow.server.VaadinSession;

import be.ucll.domain.Gebruiker;

public class SessionBeheer {

	private static final String GEBRUIKER_KEY = "ingelogdeGebruiker";

	public static void setGebruiker(Gebruiker gebruiker) {
		VaadinSession.getCurrent().setAttribute(GEBRUIKER_KEY, gebruiker);
	}

	public static Gebruiker getGebruiker() {
		return (Gebruiker) VaadinSession.getCurrent().getAttribute(GEBRUIKER_KEY);
	}

	public static boolean isIngelogd() {
		return getGebruiker() != null;
	}

	public static void uitloggen() {
		VaadinSession.getCurrent().setAttribute(GEBRUIKER_KEY, null);
	}
}
