import java.util.ArrayList;

public class RestaurantResult {
	String restaurantName;
	ArrayList<String> urls = new ArrayList<>();
	
	public ArrayList<String> getUrls() {
		return urls;
	}
	public String getRestaurantName() {
		return restaurantName;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	
	public void addUrl(String url) {
		urls.add(url);
	}
	
}
