package logParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LogParser {
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Please provide the file path as a command line argument");
			return;
		}
		String fileName = args[0];
		BufferedReader reader = new BufferedReader(new FileReader(fileName));

		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		String line;
		while ((line = reader.readLine()) != null) {
			// Split the line by spaces
			String[] parts = line.split("\\s+");
			// The time is the first part
			String time = parts[0];
			// The log level is the third part
			String logLevel = parts[2];
			// The class is the fifth part
			String className = parts[4];

			// The log message is the rest of the line
			String logMessage = line.substring(line.indexOf(className) + className.length() + 1);

			User user = parseUser(logMessage);
			LogEntry logEntry = new LogEntry(time, logLevel, className, logMessage, user);
			logEntries.add(logEntry);
		}
		// save log_entries to external file

		File logFile = new File(fileName);
		String logFileDirectory = logFile.getParent();
		String jsonFileName = logFileDirectory + File.separator + "log_entries.json";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(logEntries);

		FileWriter writer = new FileWriter(jsonFileName);
		writer.write(json);
		writer.close();

		reader.close();

		Map<String, Map<String, Integer>> userProfiles = buildUserProfiles(logEntries);
		showAndSaveUserProfiles(userProfiles,logFileDirectory);
	
	}

	private static User parseUser(String logMessage) {
		// Check if the log message contains user data
		int userIndex = logMessage.indexOf("User");
		if (userIndex == -1) {
			return null;
		}

		// Extract the user data from the log message
		String userString = logMessage.substring(userIndex);
		int openBracketIndex = userString.indexOf("[");
		int closeBracketIndex = userString.indexOf("]");
		if (openBracketIndex == -1 || closeBracketIndex == -1) {
			return null;
		}
		userString = userString.substring(openBracketIndex + 1, closeBracketIndex);
		String[] parts = userString.split(", ");
		String id = parts[0].split("=")[1];
		String name = parts[1].split("=")[1];
		String age = parts[2].split("=")[1];
		String email = parts[3].split("=")[1];

		// Create and return a User object
		return new User(id, name, age, email);
	}


	public static void saveUserProfilesToJson(Map<String, List<User>> userProfiles, String filePath)
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(filePath), userProfiles);
	}

	public static Map<String, Map<String, Integer>> buildUserProfiles(List<LogEntry> logEntries) {
		
		Map<String, Map<String, Integer>> userProfiles = new HashMap<String, Map<String, Integer>>();
		
		Map<String, Integer> readUsers = new HashMap<String, Integer>();
		Map<String, Integer> writeUsers = new HashMap<String, Integer>();
		Map<String, Integer> searchUsers = new HashMap<String, Integer>();

		for (LogEntry entry : logEntries) {
		  User user = entry.getUser();
		  String methodName = entry.getMehodeName();
		  if (methodName.startsWith("getAllProducts")) {
		    readUsers.put(user.toString(), readUsers.getOrDefault(user.toString(), 0) + 1);
		  } else if (methodName.startsWith("updateProduct") || methodName.startsWith("addProduct")) {
		    writeUsers.put(user.toString(), writeUsers.getOrDefault(user.toString(), 0) + 1);
		  } else if (methodName.startsWith("getProductById")) {
		    searchUsers.put(user.toString(), searchUsers.getOrDefault(user.toString(), 0) + 1);
		  }
		}

		//to remove the 5 initials products in the database created by default
		for (Map.Entry<String, Integer> entry : writeUsers.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    value -= 5;
		    
		    writeUsers.put(key, value);
		}
		//remove from map if value is 0
		writeUsers.entrySet().removeIf(entry -> entry.getValue() == 0);

		
		userProfiles.put("read", readUsers);
		userProfiles.put("write", writeUsers);
		userProfiles.put("search", searchUsers);
		
		return userProfiles;
	}
	public static void showAndSaveUserProfiles(Map<String, Map<String, Integer>> userProfiles,String logFileDirectory){
		
		for (Map.Entry<String, Map<String, Integer>> entry : userProfiles.entrySet()) {
			  String profile = entry.getKey();
			  Map<String, Integer> users = entry.getValue();
			  System.out.println("Profile: " + profile);
			  for (Map.Entry<String, Integer> userEntry : users.entrySet()) {
			    String user = userEntry.getKey();
			    Integer count = userEntry.getValue();
			    System.out.println("  User: " + user + " | Count: " + count);
			  }
			}

		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(new File(logFileDirectory + File.separator + "user-profiles.json"), userProfiles);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
