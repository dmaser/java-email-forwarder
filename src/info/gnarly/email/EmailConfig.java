package info.gnarly.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import info.gnarly.email.accounts.Account;
import info.gnarly.email.accounts.EmailAccount;
import info.gnarly.email.notification.Alerter;
import info.gnarly.email.notification.DefaultAlerter;
import info.gnarly.email.notification.Kde4Alerter;

public class EmailConfig {

	private static EmailConfig emailConfig;
	private static PropertiesConfiguration config;
	private Properties props = new Properties();
	private String pop3Host;
	private Alerter alerter;
	private String forwardAddress;

	private List<Account> accounts;

	private static Logger logger = Logger.getLogger(EmailConfig.class);

	private EmailConfig() throws Exception {
		config = new PropertiesConfiguration("email.properties");
		String alerterName = config.getString("alerter");
		if ("default".equals(alerterName)) {
			alerter = new DefaultAlerter("default alerter");
		} else if ("kde".equals(alerterName.toLowerCase())) {
			alerter = new Kde4Alerter();
		}
		logger.debug("alerter   =    " + alerter);

		forwardAddress = config.getString("forwardAddress");
		logger.info("forwaring messages to " + forwardAddress);

		pop3Host = config.getString("pop3Host");
		logger.info("pop3Host: " + pop3Host);

		props.put("mail.pop3.disabletop", config.getString("mail.pop3.disabletop", "true"));
		props.put("mail.smtp.auth", config.getString("mail.smtp.auth", "true"));
		props.put("mail.smtp.host", config.getString("mail.smtp.host", "smtp.1and1.com"));
		props.put("mail.smtp.port", config.getString("mail.smtp.port", "587"));

		accounts = new ArrayList<>();
		String[] accountInfo = config.getStringArray("accounts");
		logger.debug("found " + accountInfo.length + " accounts:");
		for (String account : accountInfo) {
			String[] parts = account.split("\\|");
			logger.debug("\t" + parts[0] + "\t" + parts[1]);
			accounts.add(new EmailAccount(parts[0], parts[1]));
		}
	}

	public static EmailConfig getInstance() throws Exception {
		if (emailConfig == null) {
			emailConfig = new EmailConfig();
		}
		return emailConfig;
	}

	public Properties getProps() {
		return props;
	}

	public String getPop3Host() {
		return pop3Host;
	}

	public Alerter getAlerter() {
		return alerter;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public String getForwardAddress() {
		return forwardAddress;
	}
}
