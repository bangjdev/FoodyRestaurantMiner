import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class FoodyCrawler {
	String outputFolder, foodyUrl, username, password, userToken, cookie, hostUrl = "https://www.foody.vn";
	HashMap<String, String> headers = new HashMap<>();
	JSONParser jparser = new JSONParser();
	WebDriver browser = new ChromeDriver();
	JavascriptExecutor jex = (JavascriptExecutor) browser;
	ArrayList<RestaurantResult> result = new ArrayList<>();
	int numberOfRestaurant;

	public FoodyCrawler(String url, String user, String pass, String output) {
		foodyUrl = url;
		username = user;
		password = pass;
		outputFolder = output;
	}

	boolean initCrawler() {
		HttpURLConnection connection = login();
		if (connection == null) {
			System.out.println("Ngu vai!");
		}
		cookie = "flg=vn; fd.keys=; _ga=GA1.2.585977472.1541173242; _gid=GA1.2.1651580600.1541173242; fbm_395614663835338=base_domain=.foody.vn; FOODY.AUTH.UDID=c72ec591-8c1e-4e66-8ce6-56a7cb32176b; gcat=food; fd.res.view.219=690761; __utmz=257500956.1541263345.4.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); floc=217; __ondemand_sessionid=vn4pvxb0qqexspgt2jzp2yb3; __utma=257500956.585977472.1541173242.1541268877.1541345324.7; __utmc=257500956; _fbp=fb.1.1541345324847.450194517; ilg=0; FOODY.AUTH=832B94C74BE4D5EF0F9B82BF7912654849EA98451FE279E9D49361327DC6F095352C9B10B15C5B6FE524C8F7683859D6CA89D9AF3D77283E6E7778144971FF408DB27D0DF1683C29A43BAE8FA5D17FAC29F118CC47B22FECDEABB75E36B9F41FA218FFE0DAAE7A452326DB724F591E48BC1590DDB3A1C2CB9785F499DFDE0ED7D086FAB629537D4536DE7DA2B5B62DF6AF6FFB3A93F47B4379EABD483886B137557E7EB75B22D38DEAD932E94E427130C0CC29A8EE3CD1D5C4E28031E62CCD84FA576A5F42B9DCB9153488BCBEE532ED13DCCABCD64C92170D4DB92AF1D9AD32; fbsr_395614663835338=5GCjoTyYE3qM5T47sBNjyBhroywcE3mySH-_762A7A0.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImNvZGUiOiJBUUN1enRTZFRfWS1XNGZaa2RCbTFRb2JYUHdsenhVVmFOeEQ4TEZXa3R5V05McGw3QkpXbmNWTnhQbW9uTVNUZEdncWY2Ym0yYjZfcGNBMG1oRWZzamZnejFlZEUzaFdrSjFyX09fN0tGUDRIaHZPeWgtaVpLRTR6OFRUZF84ZVpoc0R4TUcyS25kQlRMeVhFZE82WU1keWhydnp4a1dYRDhaRFZHeW95YWp4b3FjZjdLbl9TVTlPbHVUNEFBWVg2ZXV0d1ZmRUxPWkNMWXEzV2pqcW9hbjhjYnFXcTYxNERwZkRGYm9LWWdlMTNBS3hwZk4yOFJPekUwYUZhalVvMzlWVk5Hd1VTeHhTc2ZrVHU4bzBhTnFsandad3BZOWpEcVlpY1ZPNVNwMzlqdHZtYW96SFpLUTRnRWF2Tkh2empZVjQxemw2Tk9SQWhVblppcFMtaURCXyIsImlzc3VlZF9hdCI6MTU0MTM0OTQ2NywidXNlcl9pZCI6IjE5ODY1OTQ5MDQ4OTc0OTEifQ; _gat=1; _gat_ads=1; __utmt_UA-33292184-1=1; __utmb=257500956.14.10.1541345324";
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("X-Foody-User-Token", userToken);
		headers.put("Cookie", cookie);
		return true;
	}

	HttpURLConnection login() {
		System.out.println("Login...");
		browser.navigate().to("https://id.foody.vn/dang-nhap?returnUrl=" + foodyUrl);
		browser.findElement(By.id("Email")).sendKeys(username);
		browser.findElement(By.id("Password")).sendKeys(password);
		browser.findElement(By.id("bt_submit")).click();

		WebDriverWait wait = new WebDriverWait(browser, 1000);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className("row-view")));
		userToken = jex.executeScript("return window.CONST_MEDIA_XUSER_TOKEN").toString();
		String textNumber = browser.findElement(By.className("result-status-count")).getAttribute("innerText")
				.replaceAll(",", "");
		textNumber = textNumber.substring(0, textNumber.indexOf(" "));
		numberOfRestaurant = Integer.parseInt(textNumber);
		System.out.println(numberOfRestaurant);
		browser.close();

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL("https://id.foody.vn/dang-nhap")
					.openConnection();
			String loginParam = "Email=" + username + "&Password=" + password;
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setInstanceFollowRedirects(false);
			OutputStream paramWriter = connection.getOutputStream();
			paramWriter.write(loginParam.getBytes());
			paramWriter.flush();
			paramWriter.close();

			return connection;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	String getCookie(HttpURLConnection con) {
		String headerName = null, result = "";
		for (int i = 1; (headerName = con.getHeaderFieldKey(i)) != null; i++) {
			if (headerName.toLowerCase().equals("set-cookie")) {
				result += con.getHeaderField(i);
				System.out.println(con.getHeaderField(i));
			}
		}
		return result;
	}

	void getRestaurantNames() {
		int pageNum = 0;
		boolean duplicated = false;
		while (result.size() < numberOfRestaurant) {
			pageNum ++;
			JSONArray restaurantList = (JSONArray) getJsonResponse(foodyUrl, "page=" + pageNum + "&append=true").get("searchItems");
			for (Object restaurantJson : restaurantList) {
				RestaurantResult current = new RestaurantResult();
				JSONArray subRestaurant = (JSONArray) ((JSONObject) restaurantJson).get("SubItems");
				current.setRestaurantName(((JSONObject) restaurantJson).get("Name").toString());
				System.out.println((result.size() + 1) + ". " + current.getRestaurantName());
				current.addUrl(hostUrl + ((JSONObject) restaurantJson).get("DetailUrl").toString());
				for (Object sub : subRestaurant) {
					current.addUrl(hostUrl + ((JSONObject) sub).get("DetailUrl").toString());
				}
				if (result.size() != 0)
				for (int i = result.size() - 1; i >= Math.max(result.size() - 20, 0); i --) {
					if (result.get(i).getRestaurantName().equals(current.getRestaurantName())) {
						duplicated = true;
						break;
					}
				}
				if (duplicated)
					break;
				result.add(current);
			}
			if (duplicated)
				break;
		}
	}

	JSONObject getJsonResponse(String url, String param) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("X-Foody-User-Token", headers.get("X-Foody-User-Token"));
			connection.setRequestProperty("Accept", headers.get("Accept"));
			connection.setRequestProperty("X-Requested-With", headers.get("X-Requested-With"));
			connection.setRequestProperty("Cookie", headers.get("Cookie"));

			OutputStream paramWriter = connection.getOutputStream();

			paramWriter.write(param.getBytes());
			paramWriter.flush();
			paramWriter.close();

			BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line, resp = "";
			while ((line = responseReader.readLine()) != null) {
				resp += line;
			}
			responseReader.close();
			JSONParser jparser = new JSONParser();
			JSONObject res = (JSONObject) jparser.parse(resp);
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	void outputData() {
		try {
			FileWriter pw = new FileWriter(new File(outputFolder + "/" + foodyUrl.replaceAll("\\.", "_").replaceAll("\\/|:", "_") + ".dat"));
			for (RestaurantResult res : result) {
				pw.write(res.getRestaurantName() + "\n");
				for (String url : res.getUrls()) {
					pw.write(">" + url + "\n");
				}
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
