package info.gnarly.email.notification;

import org.apache.log4j.Logger;

public class DefaultAlerter extends AlerterBase {

	private static Logger logger = Logger.getLogger(DefaultAlerter.class);

	private static final String BREAKER = "\n*********************************************\n";

	public DefaultAlerter(String name) {
		super(name);
	}

	@Override
	public void send(String title, String message) throws Exception {
		logger.info(BREAKER + title + "\n" + message + BREAKER);
	}

}
