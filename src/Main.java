import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	static String inputFile, outputFolder, username, password;
	static ExecutorService threadPool;
	static int nThreads;
	public static void main(String args[]) {
		if (args.length < 5) {
			System.out.println("Not enough arguments!");
			return;
		}
			
		outputFolder = ".";
		inputFile = "./foody_list.txt";
		if (args.length != 0) {
			inputFile = args[0];
			outputFolder  = args[1];
			username = args[2];
			password = args[3];
			nThreads = Integer.parseInt(args[4]);
		}
		
		System.out.println("Start crawling program with:\n"
				+ "Input: " + inputFile + "\n"
						+ "Output: " + outputFolder);
		threadPool = Executors.newFixedThreadPool(nThreads);
		try {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFile))));
			String line;
			while ((line = fileReader.readLine()) != null) {
				threadPool.submit(new CrawlingTask(username, password, line, outputFolder));
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		threadPool.shutdown();
		
	}
}
