package be.ucll.spring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import be.ucll.jms.BestellingProducer;
import jakarta.jms.Queue;

@Configuration
@EnableJms
public class JmsConfig {

	private static final String BROKER_URL = "tcp://localhost:61616";

	/**
	 * Embedded ActiveMQ broker die luistert op TCP.
	 * vm:// werkt niet in WildFly omdat zijn classloader het transport niet kent.
	 * TCP is altijd beschikbaar en werkt in elke servlet container.
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	public BrokerService activeMQBroker() throws Exception {
		BrokerService broker = new BrokerService();
		broker.addConnector(BROKER_URL);
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setUseShutdownHook(false);
		return broker;
	}

	@Bean
	@DependsOn("activeMQBroker")
	public ActiveMQConnectionFactory activeMQConnectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL(BROKER_URL);
		factory.setUserName("admin");
		factory.setPassword("admin");
		return factory;
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate template = new JmsTemplate(activeMQConnectionFactory());
		template.setMessageConverter(messageConverter());
		return template;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(activeMQConnectionFactory());
		factory.setMessageConverter(messageConverter());
		factory.setSessionTransacted(false);
		factory.setConcurrency("1-3");
		return factory;
	}

	@Bean
	public Queue zoekResultaatQueue() {
		return new ActiveMQQueue(BestellingProducer.ZOEK_QUEUE);
	}

	@Bean
	public MessageConverter messageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
}
