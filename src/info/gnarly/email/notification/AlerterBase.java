package info.gnarly.email.notification;

public abstract class AlerterBase implements Alerter {

	protected String name;

	public AlerterBase(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}
