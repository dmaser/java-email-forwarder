package info.gnarly.email.notification;

public interface Alerter {
	public void send(String title, String message) throws Exception;
}
