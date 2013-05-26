package info.gnarly.email.accounts;

import java.util.ArrayList;
import java.util.List;

public class EmailAccount implements Account {

	private String username;
	private String password;
	private List<String> uids = new ArrayList<>();

	public EmailAccount(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isNewUid(String uid) {
		return !uids.contains(uid);
	}

	@Override
	public void addUid(String uid) {
		uids.add(uid);
	}

	@Override
	public void addUid(List<String> uidList) {
		uids.addAll(uidList);
	}
}
