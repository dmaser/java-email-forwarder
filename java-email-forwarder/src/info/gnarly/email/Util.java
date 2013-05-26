package info.gnarly.email;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class Util {

	protected static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static Logger logger = Logger.getLogger(Util.class);

	public static void log(String _info) {
		logger.info(SDF.format(new Date()) + ": " + _info);
	}

}
