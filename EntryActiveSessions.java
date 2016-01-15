
public class EntryActiveSession {

	private String client;
	private String server;
	private Float dataSent;
	private Float dataRcv;
	private Long timeOfCreation_ms;
	

	public String getClient() {
		return client;
	}
	public String getServer() {
		return server;
	}
	public Float getDataSent() {
		return dataSent;
	}
	public Float getDataRcv() {
		return dataRcv;
	}
	public Long getTimeOfCreation() {
		return timeOfCreation_ms;
	}

	public void setClient(String client) {
		this.client = client;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public void setDataSent(Float dataSent) {
		this.dataSent = dataSent;
	}
	public void setDataRcv(Float dataRcv) {
		this.dataRcv = dataRcv;
	}
	public void setTimeOfCreation(Long timeOfCreation) {
		this.timeOfCreation_ms = timeOfCreation;
	}
	
}
