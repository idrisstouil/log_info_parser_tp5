package logParser;
class LogEntry {
    private final String time;
    private final String logLevel;
    private final String className;
    private final String mehodeName;
    private final String logMessage;
    private final User user;

    public LogEntry(String time, String logLevel, String className, String logMessage, User user) {
        this.time = time;
        this.logLevel = logLevel;
        this.className = className;
		this.mehodeName = logMessage.indexOf("MethodeName") != -1 ? logMessage.substring( logMessage.indexOf("MethodeName")+"MethodeName".length() + 3).split(";")[0] :"";
        this.logMessage = logMessage;
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getClassName() {
        return className;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public User getUser() {
        return user;
    }

	public String getMehodeName() {
		return mehodeName;
	}
	
}