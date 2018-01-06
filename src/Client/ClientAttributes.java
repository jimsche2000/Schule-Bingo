package Client;

import java.io.Serializable;

public class ClientAttributes implements Serializable {

	private static final long serialVersionUID = 1L;
	String IP = "0.0.0.0", name = "";

	public ClientAttributes(String IP, String name) {

		this.IP = IP;
		this.name = name;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return ("{NAME=" + getName() + "}{IP=" + getIP() + "}");

	}
}