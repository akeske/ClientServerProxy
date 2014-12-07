class Commands {

	public final static int COMMAND_UPLOAD_FILE_TO_SERVER_PROXY = 0;
	public final static int COMMAND_DOWNLOAD_FILE_FROM_SERVER = 1;
	public final static int COMMAND_FILE_NOT_FOUNT = 2;
	public final static int COMMAND_GET_SEND_FILELIST = 3;
	public final static int COMMAND_START_RECEIVING_FILELIST = 4;
	public final static int COMMAND_END_RECEIVING_FILELIST = 5;
	public final static int COMMAND_SEND_FILE_SIZE = 6;
	public final static int COMMAND_DISCONNECT = 7;
	public final static int COMMAND_USERNAME = 8;
	public final static int REPORT = 9;
	public final static int VOLUNTEER = 10;
	public final static int COMMAND_NEXT_NODE_IP = 11;
	public final static int COMMAND_NEXT_NODE_PORT = 12;
	public final static int COMMAND_NODE_IP = 13;
	public final static int COMMAND_NODE_PORT = 14;
	public final static int COMMAND_HOOP = 15;


	public final static String commandMessages[] = {
			"000_UPLOAD_FILE_TO_SERVER_", "001_DOWNLOAD_FILE_FROM_SERVER_",
			"002_FILE_NOT_EXIST", "003_SEND_GET_FILELIST", "004_START_RECEIVING_FILELIST",
			"005_END_RECEIVING_FILELIST",
			"006_SEND_FILE_SIZE", "007_DISCONNECT", "008_USERNAME_", "REPORT", "ISVOLUNTEER_",
			"NEXT_NODE_IP_", "NEXT_NODE_PORT_", "NODE_IP_", "NODE_PORT_", "HOOP_"
	};

}
