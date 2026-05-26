package be.ucll.jms;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import be.ucll.domain.Bestelling;
import be.ucll.mail.MailService;
import be.ucll.services.BestellingService;

@Component
public class BestellingStatusListener {

	private static final Logger log = LoggerFactory.getLogger(BestellingStatusListener.class);

	@Autowired
	private BestellingService bestellingService;

	@Autowired
	private MailService mailService;

	@JmsListener(destination = BestellingProducer.ZOEK_QUEUE, containerFactory = "jmsListenerContainerFactory")
	public void onZoekResultaat(ZoekResultaatBericht bericht) {
		log.info("JMS: ontvangen zoekresultaat voor {} ({} bestellingen)",
				bericht.getEmailAdres(), bericht.getBestellingIds().size());

		List<Bestelling> bestellingen = bericht.getBestellingIds().stream()
				.map(id -> bestellingService.findById(id).orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		mailService.stuurZoekResultaat(bericht.getEmailAdres(), bericht.getGebruikerNaam(), bestellingen);
	}
}
