package info.gnarly.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.mail.pop3.POP3Folder;

import info.gnarly.email.accounts.Account;
import info.gnarly.email.notification.Alerter;

public class EmailMain {

	private static final String PROTOCOL = "pop3";

	private static List<Account> accounts;
	private static Address[] forwardAddresses;
	private static final String SEEN_UID_FILE = System.getProperty("user.home") + "/seen_uids.txt";
	private static Map<Account, Integer> countTotal = new HashMap<>();
	private static Map<Account, List<String>> unread = new HashMap<>();

	private static String host;

	private static Logger logger = Logger.getLogger(EmailMain.class);

	public static void main(String[] args) {
		try {

			PropertyConfigurator.configureAndWatch("logger.properties");

			EmailConfig config = EmailConfig.getInstance();
			host = config.getPop3Host();
			Alerter alerter = config.getAlerter();
			accounts = config.getAccounts();
			forwardAddresses = InternetAddress.parse(config.getForwardAddress());

			for (Account account : accounts) {
				countTotal.put(account, new Integer(0));
			}

			initializeSeenUids();

			while (true) {
				for (Account account : accounts) {
					unread.put(account, new ArrayList<String>());
				}
				getNewCounts(config.getProps());
				String message = "";
				int totalNew = 0;
				for (Account account : unread.keySet()) {
					int thisNew = unread.get(account).size();
					if (thisNew > 0) {
						message += "<h3>" + thisNew + " new message(s) for " + account.getUsername() + "</h3>";
						for (String subj : unread.get(account)) {
							message += "<p>" + subj + "</p>";
						}
						totalNew += thisNew;
					}
				}
				if (!"".equals(message)) {
					alerter.send(totalNew + " new messages", message);
				}
				TimeUnit.MINUTES.sleep(5L);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getNewCounts(Properties props) throws Exception {

		long start = System.currentTimeMillis();

		int total = 0;
		for (final Account account : accounts) {
			final String username = account.getUsername();
			final String password = account.getPassword();

			Authenticator auth = new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};
			Session session = Session.getDefaultInstance(props, auth);
			// session.setDebug(true);

			Store store = session.getStore(PROTOCOL);

			try {
				store.connect(host, username, password);
				POP3Folder inbox = (POP3Folder) store.getFolder("INBOX");
				inbox.open(Folder.READ_ONLY);

				Message[] msgs = inbox.getMessages();
				FetchProfile fp = new FetchProfile();
				fp.add(UIDFolder.FetchProfileItem.UID);
				inbox.fetch(msgs, fp);

				for (Message m : msgs) {
					String uid = inbox.getUID(m);
					if (account.isNewUid(uid)) {
						account.addUid(uid);
						forwardMessage(session, m);
						logger.info("Forwarded message for " + username + " [" + uid + "]");
						saveUid(username, uid);
						unread.get(account).add(m.getSubject());
						logger.info("added " + uid);
					}
				}
				Integer count = new Integer(inbox.getMessageCount());

				if (countTotal.get(account).compareTo(count) != 0) {
					int newMessages = count.intValue() - countTotal.get(account).intValue();
					countTotal.put(account, count);
					Util.log(username + "\t" + newMessages + " messages");
				}

				total += count.intValue();

				inbox.close(false);
				store.close();
				TimeUnit.SECONDS.sleep(1L);
			} catch (Exception e) {
				logger.warn("\nfailed to get count for " + username + " [" + e.getMessage()
						+ (e.getCause() == null ? " (no cause)" : " (" + e.getCause().getMessage() + ")]\n"));
				TimeUnit.SECONDS.sleep(5L);
			}
		}

		float elapsed = (System.currentTimeMillis() - start) / 1000F;
		Util.log(String.format("Total checked: %d  time: %3.2f seconds", total, elapsed));

	}

	private static void forwardMessage(Session _session, Message _message) throws Exception {
		Message msg = new MimeMessage(_session);

		Enumeration<Header> headers = _message.getAllHeaders();
		while (headers.hasMoreElements()) {
			Header h = headers.nextElement();
			msg.addHeader(h.getName(), h.getValue());
		}

		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(_message.getContent(), _message.getContentType());
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		msg.setContent(multipart);

		Transport.send(msg, forwardAddresses);

	}

	private static void saveUid(String account, String uid) throws Exception {
		try (FileWriter writer = new FileWriter(SEEN_UID_FILE, true);) {
			writer.write(account + "\t" + uid + "\n");
		}
	}

	private static void initializeSeenUids() throws Exception {
		Map<String, List<String>> uids = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(SEEN_UID_FILE));) {
			String line = reader.readLine();
			while (line != null && line.contains("\t")) {
				String[] parts = line.split("\t");
				String name = parts[0];
				String uid = parts[1];
				if (uids.get(name) == null) {
					uids.put(name, new ArrayList<String>());
				}
				uids.get(name).add(uid);
				line = reader.readLine();
			}
		}
		for (Account account : accounts) {
			if (uids.get(account.getUsername()) != null) {
				account.addUid(uids.get(account.getUsername()));
			}
		}
	}
}
