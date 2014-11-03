package controller;

public class POJOSettings {

    private String serverIP;
    private String serverPort;
    private String proxyIP;
    private String proxyPort;
    private String nickName;
	private Boolean anonym;

	public Boolean getAnonym() {
		return anonym;
	}

	public void setAnonym(Boolean anonym) {
		this.anonym = anonym;
	}

	public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getProxyIP() {
        return proxyIP;
    }

    public void setProxyIP(String proxyIP) {
        this.proxyIP = proxyIP;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
