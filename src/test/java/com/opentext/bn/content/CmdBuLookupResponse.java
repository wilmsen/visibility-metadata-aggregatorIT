package com.opentext.bn.content;

import java.io.IOException;
import java.io.Writer;

import com.github.cliftonlabs.json_simple.Jsonable;

public class CmdBuLookupResponse implements Jsonable {

	private String senderAddress;
	private String senderQualifier;
	private String receiverAddress;
	private String receiverQualifier;
	private String response;
	
	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void toJson(Writer writable) throws IOException {
		// TODO Auto-generated method stub
		
	}
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	public String getSenderQualifier() {
		return senderQualifier;
	}
	public void setSenderQualifier(String senderQualifier) {
		this.senderQualifier = senderQualifier;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	public String getReceiverQualifier() {
		return receiverQualifier;
	}
	public void setReceiverQualifier(String receiverQualifier) {
		this.receiverQualifier = receiverQualifier;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	

}
