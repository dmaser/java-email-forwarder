package info.gnarly.email.accounts;

import java.util.List;

public interface Account {

	public String getUsername();

	public String getPassword();

	public void addUid(String uid);

	public void addUid(List<String> uidList);

	public boolean isNewUid(String uid);

}
