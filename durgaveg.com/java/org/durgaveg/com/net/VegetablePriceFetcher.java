package org.durgaveg.com.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import static java.lang.System.out;

public class VegetablePriceFetcher {
	String mostGainers = "";
	String mostLosers = "";
	// http://www.wsj.com/mdc/public/page/2_3021-losennm-loser.html
	// http://www.wsj.com/mdc/public/page/2_3021-gainnnm-gainer-20180410.html
	// /*April 10*/
	// http://www.wsj.com/mdc/public/page/2_3021-losennm-loser-20180416.html
	// "https://finance.yahoo.com/gainers";
	String filePath = "";

	private void fetchAndSave(String exchange, String urlstr) throws IOException {
		URL url = new URL(urlstr);
		URLConnection conn = (URLConnection) url.openConnection();
		conn.connect();
		StringBuilder webpageDetails = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		do {
			webpageDetails.append(reader.readLine());
			webpageDetails.append("\n");
		} while (reader.ready());
		// printWebPage(webpageDetails.toString());
		saveStockQuote(exchange, webpageDetails.toString());
	}

	public String getNasdaqLosers() throws IOException {
		mostLosers = "http://www.wsj.com/mdc/public/page/2_3021-losennm-loser.html";
		return getNasdaqLosers(mostLosers);
	}

	public String getNasdaqLosers(String uri) throws IOException {
		fetchAndSave("NSDQL", uri);
		return this.filePath;
	}

	public String getNyseLosers() throws IOException {
		mostLosers = "http://www.wsj.com/mdc/public/page/2_3021-losenyse-loser.html";
		return getNyseLosers(mostLosers);
	}

	public String getNyseLosers(String uri) throws IOException {
		fetchAndSave("NYSEL", uri);
		return this.filePath;
	}

	public String getNasdaqGainers() throws IOException {
		mostGainers = "http://www.wsj.com/mdc/public/page/2_3021-gainnnm-gainer.html";
		return getNasdaqGainers(mostGainers);
	}

	public String getNasdaqGainers(String uri) throws IOException {
		fetchAndSave("NSDQ", uri);
		return this.filePath;
	}

	public String getNyseGained() throws IOException {
		mostGainers = "http://www.wsj.com/mdc/public/page/2_3021-gainnyse-gainer.html";
		return getNyseGained(mostGainers);
	}

	public String getNyseGained(String uri) throws IOException {
		fetchAndSave("NYSE", uri);
		return this.filePath;
	}

	private void saveStockQuote(String type, String allHtml) throws IOException {
		Calendar cal = Calendar.getInstance();
		String file = cal.get(Calendar.DATE) + "-" + cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE);
		file = "C:/docs/quotes/ex/" + type + file + ".txt";
		System.out.println("saving to file" + file);
		File output = new File(file);
		if (!output.exists()) {
			output.createNewFile();
		}
		FileWriter writer = new FileWriter(output);
		writer.write(allHtml);
		writer.flush();
		this.filePath = file;
	}

	private void printWebPage(String webpageDetails) {
		System.out.println(webpageDetails);
	}

	String commodity = "Tx_Commodity=23&";
	String commodityHead = "Tx_CommodityHead=Onion&";
	String commodityTemp = "Tx_Commodity=#&";
	String commodityHeadTemp = "Tx_CommodityHead=#&";

	String exUrl = "http://agmarknet.gov.in/SearchCmmMkt.aspx?" + commodity + "Tx_State=KK&" + "Tx_District=10&"
			+ "Tx_Market=120&" + "DateFrom=19-Mar-2010&" + "DateTo=18-Mar-2019&" + "Fr_Date=19-Mar-2010&"
			+ "To_Date=18-Mar-2019&" + "Tx_Trend=0&" + commodityHead + "Tx_StateHead=Karnataka&"
			+ "Tx_DistrictHead=Kolar&" + "Tx_MarketHead=Kolar";

	private String buildURL() {
		StringBuilder builder = new StringBuilder();
		builder.append("http://agmarknet.gov.in/SearchCmmMkt.aspx?").append(commodity).append("Tx_State=KK&")
				.append("Tx_District=10&").append("Tx_Market=120&").append("DateFrom=19-Mar-2010&")
				.append("DateTo=18-Mar-2019&").append("Fr_Date=19-Mar-2010&").append("To_Date=18-Mar-2019&")
				.append("Tx_Trend=0&").append(commodityHead).append("Tx_StateHead=Karnataka&")
				.append("Tx_DistrictHead=Kolar&").append("Tx_MarketHead=Kolar");
		return builder.toString();
	}

	private List<String> createURL(String... types) throws FileNotFoundException, IOException {
		Properties cTypes = new Properties();
		cTypes.load(new FileInputStream("src/main/resources/commodities.properties"));
		String commKey = "";
		List<String> ret = new ArrayList<String>();
		Map<String,String> keys = new HashMap<String,String>();
		cTypes.keySet().forEach(new Consumer<Object>() {
 			@Override
			public void accept(Object t) {
				// TODO Auto-generated method stub
 				String key= (String)t;
				keys.put(key.trim().split(" ")[0], key);
			}});
		for (String type : types) {
			commKey = cTypes.getProperty(type.trim().split(" ")[0]);
			if(commKey == null)
			{
				commKey = keys.get(type.trim());
				if(commKey == null ) {
				out.println("No key found "+commKey+" Value "+type);
				continue;
				}
			}
			commodity = commodityTemp.replace("#", commKey);
			commodityHead = commodityHeadTemp.replace("#", type);
			ret.add(buildURL());
		}
		return ret;
	}

	public String[] getIdealSolutions() {
		String[] ret = new String[] { "Carrot Nati/Ooty	", "Beans	", "Beetroot	", "Cabbage	",
				"Brinjal  long green	", "Brinjal (Varikatri)	", "Brinjal  green round	",
				"Chilli Green	", "Capsicum Green	", "Chilli Bajji 	", "Cauliflower	",
				"Tomato Hybrid	", "Tomato Nati	", "Okra	", "Knol khol 	", "Pumpkin	",
				"Ash Gourd	", "Ridge Gourd	", "Bottle Gourd	", "Radish White	", "Arbi	",
				"Coccenia 	", "Potato	", "Potato Baby	", "Onion	", "Onion Sambar	",
				"Garlic 	", "Ginger	", "Coconut	", "Spinach	", "Coriander 	", "Mint	",
				"Spinach	", "Methi	", "Amaranthus Red 	", "Amaranthus Green	" };
		return ret;
	}

	public static void main(String arg[]) throws IOException {
		VegetablePriceFetcher fetch = new VegetablePriceFetcher();
		List<String> urls = fetch.createURL(fetch.getIdealSolutions());//fetch.createURL("Beans", "Cabbage", "Carrot", "Brinjal", "Cauliflower", "Garlic");
		for (String ste : urls) {
			out.println(ste);
		}
	}
}
