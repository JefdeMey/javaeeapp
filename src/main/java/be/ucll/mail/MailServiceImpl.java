package be.ucll.mail;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import be.ucll.domain.Bestelling;
import be.ucll.domain.BestellingStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

	private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

	@Autowired
	private JavaMailSender mailSender;

	@Value("${mail.afzender:noreply@techshop.be}")
	private String afzender;

	@Override
	public void stuurZoekResultaat(String emailAdres, String gebruikerNaam, List<Bestelling> bestellingen) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(afzender);
			helper.setTo(emailAdres);
			helper.setSubject("Uw zoekresultaten - " + bestellingen.size() + " bestelling(en) gevonden");
			helper.setText(buildHtmlEmail(gebruikerNaam, bestellingen), true);
			mailSender.send(mimeMessage);
			log.info("Mail verstuurd naar {} ({} bestellingen)", emailAdres, bestellingen.size());
		} catch (MessagingException | MailException e) {
			log.error("Fout bij versturen mail naar {}: {}", emailAdres, e.getMessage());
		}
	}

	private String buildHtmlEmail(String naam, List<Bestelling> bestellingen) {
		StringBuilder html = new StringBuilder();
		html.append("<html><body style='font-family: Arial, sans-serif;'>");
		html.append("<p>Beste ").append(naam).append(",</p>");
		html.append("<p>Hieronder vindt u het gevraagde overzicht van uw bestellingen:</p>");
		html.append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse; width:100%;'>");
		html.append("<thead><tr style='background-color:#0066cc; color:white;'>");
		html.append("<th>Bestel ID</th><th>Klantnr</th><th>#Producten</th><th>Afgeleverd</th><th>Totaal</th>");
		html.append("</tr></thead><tbody>");

		for (int i = 0; i < bestellingen.size(); i++) {
			Bestelling b = bestellingen.get(i);
			String rij = i % 2 == 0 ? "#f9f9f9" : "#ffffff";
			html.append("<tr style='background-color:").append(rij).append(";'>");
			html.append("<td>").append(b.getId()).append("</td>");
			html.append("<td>").append(b.getGebruiker().getId()).append("</td>");
			html.append("<td>").append(b.getRegels().size()).append("</td>");
			html.append("<td>").append(b.getStatus() == BestellingStatus.VERZONDEN ? "Ja" : "Nee").append("</td>");
			html.append("<td>&euro; ").append(b.getTotaalBedrag()).append("</td>");
			html.append("</tr>");
		}

		html.append("</tbody></table>");
		html.append("<p style='margin-top:20px;'>Met vriendelijke groeten,<br><strong>TechShop</strong></p>");
		html.append("</body></html>");
		return html.toString();
	}
}
