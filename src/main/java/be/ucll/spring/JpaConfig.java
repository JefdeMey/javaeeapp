package be.ucll.spring;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

import be.ucll.util.H2IsolationLevelInitializerBean;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class JpaConfig {

	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server h2Server() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpShutdownForce");
	}

	@Bean
	@DependsOn("h2Server")
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:tcp://localhost/mem:db;LOCK_MODE=0");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	@Bean(initMethod = "setIsolationLevelReadUncommited")
	public H2IsolationLevelInitializerBean h2IsolationLevelInitializerBean(DataSource dataSource) {
		return new H2IsolationLevelInitializerBean(dataSource);
	}

	/**
	 * Expliciet JpaTransactionManager als @Primary zodat WildFly's JtaTransactionManager
	 * niet wint via Spring Boot's JtaAutoConfiguration. Alle @Transactional-operaties
	 * lopen via onze BasicDataSource (RESOURCE_LOCAL) en zien de H2-tabellen.
	 */
	@Bean
	@Primary
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
