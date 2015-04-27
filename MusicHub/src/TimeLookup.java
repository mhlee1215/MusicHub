// List of time servers: http://tf.nist.gov/service/time-servers.html

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class TimeLookup {
	public static final String TIME_SERVER = "time-a.nist.gov";

	public static void main(String[] args) throws Exception {
		//Global Time
		NTPUDPClient timeClient = new NTPUDPClient();
		InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
		TimeInfo timeInfo = timeClient.getTime(inetAddress);
		long returnTime = timeInfo.getReturnTime();
		Date time = new Date(returnTime);
		System.out.println("Time from " + TIME_SERVER + ": " + time);
		System.out.println(returnTime);
		
		
		//Local time
		Calendar rightNow = Calendar.getInstance();

		// offset to add since we're not UTC
		long offset = rightNow.get(Calendar.ZONE_OFFSET) +
		    rightNow.get(Calendar.DST_OFFSET);
		long sinceMidnight = (rightNow.getTimeInMillis() + offset) %
		    (24 * 60 * 60 * 1000);

		System.out.println(sinceMidnight + " milliseconds since midnight");
	}
}