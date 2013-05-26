package info.gnarly.email.notification;

import java.util.ArrayList;
import java.util.List;

import info.gnarly.email.Util;

public class Kde4Alerter extends AlerterBase {

	private static final int ALERT_SECONDS = 10;

	public Kde4Alerter() {
		super(Kde4Alerter.class.getSimpleName());
	}

	@Override
	public void send(String title, String message) throws Exception {
		List<String> cmdArgs = new ArrayList<>();
		cmdArgs.add("kdialog");
		cmdArgs.add("--title=" + title);
		cmdArgs.add("--passivepopup");
		cmdArgs.add(message);
		cmdArgs.add(Integer.toString(ALERT_SECONDS));
		Process p = Runtime.getRuntime().exec(cmdArgs.toArray(new String[] {}));
		int exitVal = p.waitFor();
		Util.log("Title: " + title + "\n" + "Message:\n" + message + "\nExit value: " + exitVal);
	}

}
