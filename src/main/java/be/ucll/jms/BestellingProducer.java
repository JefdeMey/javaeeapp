package be.ucll.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class BestellingProducer {

	public static final String ZOEK_QUEUE = "zoek.resultaat.queue";

	private static final Logger log = LoggerFactory.getLogger(BestellingProducer.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	public void verstuurZoekResultaat(ZoekResultaatBericht bericht) {
		log.info("JMS: verstuur zoekresultaat ({} bestellingen) naar {} via queue {}",
				bericht.getBestellingIds().size(), bericht.getEmailAdres(), ZOEK_QUEUE);
		jmsTemplate.convertAndSend(ZOEK_QUEUE, bericht);
	}
}
