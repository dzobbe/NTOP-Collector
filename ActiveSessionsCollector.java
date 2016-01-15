package antiDoS.sentinel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ActiveSessionsCollector {
	
	private String ntopIP;
	private int ntopPort;
	
	public ActiveSessionsCollector(String ntopIP){ //By default NTOP listens on port 3000
		this.ntopIP=ntopIP;
		this.ntopPort=3000;
	}
	
	public ActiveSessionsCollector(String ntopIP, int ntopPort){
		this.ntopIP=ntopIP;
		this.ntopPort=ntopPort;
	}
	
	
	public List<EntryActiveSessions> getActiveSessions() {

		final Pattern ipPattern = Pattern
				.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
		List<EntryActiveSessions> entries = new ArrayList<EntryActiveSessions>(); // All
																					// Entries

		final String url = "http://" + ntopIP + ":"+ntopPort+"/activeSessions.html?page=";

		int page = 0;
		
		while (true) { // Loop through pages
			try {

				String responseHtml = sendGet(url + page);
				page++;
				if (responseHtml.contains("No Data To Display")) {
					break;
				}

				Document doc = Jsoup.parse(responseHtml);
				Elements element = doc.select("tr");

				for (int j = 4; j < doc.select("tr").size(); j++) { // Select
																	// all 'tr'
																	// tags from
																	// document

					int index = 0;
					Elements tableHeader = element.get(j).select("td");

					if (tableHeader.get(index++).text().contains("TCP")) { //Select only TCP connections

						EntryActiveSessions entry = new EntryActiveSessions();

						if (tableHeader.size() > 5) {

							/******* Get Client IP  *******/
							String ipclient = tableHeader.get(index++).text();
							Matcher mtch = ipPattern.matcher(ipclient);
							if (mtch.find()) {
								ipclient = mtch.group();
							}
							entry.setClient(ipclient);

							/******* Get Server IP *******/
							String ipserver = tableHeader.get(index++).text();
							mtch = ipPattern.matcher(ipserver);
							if (mtch.find()) {
								ipserver = mtch.group();
							}
							entry.setServer(ipserver);

							/******* Get Data Sent to the Server *******/
							String entryDataSent = tableHeader.get(index++).text();
							Float effectiveValue = new Float(0);
							if (entryDataSent.contains("KBytes")) {
								effectiveValue = Float.parseFloat(
										entryDataSent.replace("KBytes", "").replaceAll("\\p{javaSpaceChar}", ""))
										* 1000;
							}
							entry.setDataSent(effectiveValue);

							/******* Get Data Received by the Server *******/
							String entryDataRcvd = tableHeader.get(index++).text();
							if (entryDataRcvd.contains("KBytes")) {
								effectiveValue = Float.parseFloat(
										entryDataRcvd.replace("KBytes", "").replaceAll("\\p{javaSpaceChar}", ""))
										* 1000;
							}
							entry.setDataRcv(effectiveValue);

							/******* Get Time Of Session Creation *******/
							String timeOfCreation = tableHeader.get(index++).text();
							timeOfCreation = timeOfCreation.replaceAll("\\p{javaSpaceChar}", " ");
							DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
							Date result = df.parse(timeOfCreation);
							entry.setTimeOfCreation(result.getTime());
							entries.add(entry);

						}

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return entries;
	}

	private static String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes("");
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();

		// print result
		return response.toString();

	}

}
