
public class CrawlingTask implements Runnable {

	String username, password, targetUrl, outputFolder;

	public CrawlingTask(String username, String password, String targetUrl, String outputFolder) {
		this.username = username;
		this.password = password;
		this.targetUrl = targetUrl;
		this.outputFolder = outputFolder;
	}

	@Override
	public void run() {
		FoodyCrawler crawler = new FoodyCrawler(targetUrl, username, password, outputFolder);
		crawler.initCrawler();
		crawler.getRestaurantNames();
		crawler.outputData();
	}

}
