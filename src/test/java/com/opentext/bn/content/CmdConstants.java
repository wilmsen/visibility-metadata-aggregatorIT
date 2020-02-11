package com.opentext.bn.content;


public class CmdConstants {


	//@Value("${LENS_REGION:TG16-DEV}")
	//public final String lensRegion;

	public static String CMD_REST_URL = "http://qtotcra.qa.gxsonline.net:8080/communitymasterdata/rest/v1";
	public static String CMD_COMMUNITY_ID = "GCM69516";
	public static String CMD_INSTANCE_ID = "178571";
	public static String CMD_PRINCIPAL_TYPE = "SERVICE_SESSION";
	public static String CMD_RESPONSE_TIMEOUT = "1000ms";
	
	public static final String IM_SERVICE_INSTANCE_ID = "im_service_instance_id";
	public static final String IM_PRINCIPAL_TYPE = "im_principal_type";
	public static final String IM_COMMUNITY_ID = "im_community_id";

	public final String buidLookUpByAddr = "/resolver/rootParentsByAddresses?senderAddress={senderAddress}&senderQualifier={senderQualifier}&receiverAddress={receiverAddress}&receiverQualifier={receiverQualifier}";
	public final String buidLookupbyMailbox = "/resolver/rootParentsByMailboxes?senderMailbox={senderMailbox}&receiverMailbox={recieverMailbox}";
	public final String companyNameLookup = "/businessUnits/{buId}";
	public final String ownerBuidLookup = "/solutions/attrs?id={solutionId}";
	public final String mdMildLookup = "/messagingAddresses/attrs?address={address}&qualifier={qualifier}";
	public final String mailboxLookup = "/mailboxes/{mdMild}";

}
