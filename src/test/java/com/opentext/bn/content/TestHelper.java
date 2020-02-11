package com.opentext.bn.content;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.google.common.base.Strings;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class TestHelper {

	public static String setVariableValues(String str, String transactionId, String payloadId) {
		String taskId = UUID.randomUUID().toString();
		String processId = UUID.randomUUID().toString();
		String eventId = UUID.randomUUID().toString();
		String assetId = UUID.randomUUID().toString();
		String documentId = UUID.randomUUID().toString();
		String envelopeId = UUID.randomUUID().toString();
		String fileId = UUID.randomUUID().toString();
		String errorId = UUID.randomUUID().toString();
		String controlNumber = UUID.randomUUID().toString().substring(0, 9);
		String correlationId = UUID.randomUUID().toString();
		String parentProcessId = UUID.randomUUID().toString();;


		// ContentError only
		if (str.contains("errorId")) {
			str = str.replaceAll("var_errorId", errorId);

			int start = str.indexOf("errorLevel");
			int end = str.indexOf("offset");
			String s = str.substring(start, end);
			if(s.contains("ENVELOPE")) {
				str = str.replaceAll("var_fileId", "")
						.replaceAll("var_documentId", "")
						.replaceAll("var_envelopeId", envelopeId);
			}else if(s.contains("DOCUMENT")) {
				str = str.replaceAll("var_fileId", "")
						.replaceAll("var_envelopeId", "")
						.replaceAll("var_documentId", documentId);
			}else if(s.contains("FILE")) {
				str = str.replaceAll("var_documentId", "")
						.replaceAll("var_envelopeId", "")
						.replaceAll("var_fileId", fileId);
			}
		}else {
			str = str.replaceAll("var_documentId", documentId)
					.replaceAll("var_envelopeId", envelopeId)
					.replaceAll("var_fileId", fileId);
		}

		str = str.replaceAll("var_transactionId", transactionId == null? "": transactionId)
				.replaceAll("var_taskId", taskId)
				.replaceAll("var_processId", processId)
				.replaceAll("var_payloadId", payloadId == null? "": payloadId)
				.replaceAll("var_assetId", assetId)
				.replaceAll("var_controlNumber", controlNumber)
				.replaceAll("var_correlationId", correlationId)
				.replaceAll("var_parentProcessId", parentProcessId)
				.replaceAll("var_eventId", eventId);

		return str;
	}

	public static String setVariableValuesForXIF(String str) {
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		String uuid3 = UUID.randomUUID().toString();
		String uuid4 = UUID.randomUUID().toString();
		String uuid5 = UUID.randomUUID().toString();
		String uuid6 = UUID.randomUUID().toString();

		String dsmkey1 = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
		String dsmkey2 = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
		String dsmkey3 = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
		String dsmkey4 = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
		String dsmkey5 = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
		String dsmkey6 = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);

		String controlNumber1 = UUID.randomUUID().toString().substring(0, 9);
		String controlNumber2 = UUID.randomUUID().toString().substring(0, 9);
		String controlNumber3 = UUID.randomUUID().toString().substring(0, 9);
		String controlNumber4 = UUID.randomUUID().toString().substring(0, 9);
		String controlNumber5 = UUID.randomUUID().toString().substring(0, 9);
		String controlNumber6 = UUID.randomUUID().toString().substring(0, 9);
		String controlNumber7 = UUID.randomUUID().toString().substring(0, 9);


		String senderId = UUID.randomUUID().toString().substring(0, 10);
		String receiverId = UUID.randomUUID().toString().substring(0, 10);

		String aprf = Integer.toString((int)(Math.random()*((999-100)+1))+100);
		String serviceName = UUID.randomUUID().toString().substring(0, 10);

		str = str.replaceAll("var_uuid1", uuid1)
				.replaceAll("var_uuid2", uuid2)
				.replaceAll("var_uuid3", uuid3)
				.replaceAll("var_uuid4", uuid4)
				.replaceAll("var_uuid5", uuid5)
				.replaceAll("var_uuid6", uuid6)
				.replaceAll("var_controlNumber1", controlNumber1)
				.replaceAll("var_controlNumber2", controlNumber2)
				.replaceAll("var_controlNumber3", controlNumber3)
				.replaceAll("var_controlNumber4", controlNumber4)
				.replaceAll("var_controlNumber5", controlNumber5)
				.replaceAll("var_controlNumber6", controlNumber6)
				.replaceAll("var_controlNumber7", controlNumber7)
				.replaceAll("var_dsmkey1", dsmkey1)
				.replaceAll("var_dsmkey2", dsmkey2)
				.replaceAll("var_dsmkey3", dsmkey3)
				.replaceAll("var_dsmkey4", dsmkey4)
				.replaceAll("var_dsmkey5", dsmkey5)
				.replaceAll("var_dsmkey6", dsmkey6)
				.replaceAll("var_senderId", senderId)
				.replaceAll("var_receiverId", receiverId)
				.replaceAll("var_aprf", aprf)
				.replaceAll("var_serviceName", serviceName);


		return str;
	}

	/*
	 * public static <T> boolean haveSamePropertyValues (Class<T> type, T t1, T t2){
	 * 
	 * try { BeanInfo beanInfo = Introspector.getBeanInfo(type); for
	 * (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) { Method m =
	 * pd.getReadMethod(); Object o1 = m.invoke(t1); Object o2 = m.invoke(t2); if
	 * (!Objects.equals(o1, o2)) { System.out.println(type.getName() +
	 * " has different values at: " + m.getName() + ", envelope value: " + o1 +
	 * ", envelope value: " + o2); return false; } } return true; } catch(Exception
	 * e) { return false; } }
	 */

	/*
	 * public static <T> String haveSamePropertyValues (Class<T> type, T t1, T t2){
	 * 
	 * try { BeanInfo beanInfo = Introspector.getBeanInfo(type); for
	 * (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) { Method m =
	 * pd.getReadMethod(); Object o1 = m.invoke(t1); Object o2 = m.invoke(t2); if
	 * (!Objects.equals(o1, o2)) { String str = type.getName() +
	 * " has different values at: " + m.getName() + ", envelope value: " + o1 +
	 * ", envelope value: " + o2; return str; } } return ""; } catch(Exception e) {
	 * return e.toString(); } }
	 */

	public static String haveSamePropertyValues(Object o1, Object o2) {
		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = o1.getClass().getDeclaredFields();

		//print field names paired with their values
		for ( Field field : fields  ) {
			field.setAccessible(true);
			Object envelopeValue = null;
			Object payloadValue = null;
			try {
				envelopeValue = field.get(o1);
				payloadValue = field.get(o2);
				if((null == envelopeValue && null != payloadValue) 
						|| (null == payloadValue && null != envelopeValue)
						|| (null != envelopeValue && null != payloadValue && !envelopeValue.equals(payloadValue))){
					String str = o1.getClass().getName() +
							" has different values at: " + field.getName() + 
							", envelope value: " + envelopeValue + ", payload value: " + payloadValue; 
					return str;
				}
			}catch(Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				return field.getName() + ":  envelope = " + envelopeValue + ", payload = "+ payloadValue + ", " + exceptionAsString;
			}
		}		
		return "";
	}

	public static String haveSamePropertyValuesExcludeFields(Object o1, Object o2, List<String> excludeFieldNames) {
		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = o1.getClass().getDeclaredFields();

		//print field names paired with their values
		for ( Field field : fields  ) {
			//exclude some fields
			boolean needValidate = true;
			Object envelopeValue = null;
			Object payloadValue = null;
			String name =  field.getName();
			for(String ef: excludeFieldNames) {
				if(name.equals(ef)) {
					needValidate = false;
					break;
				}
			}
			if(needValidate) {
				field.setAccessible(true);
				try {
					envelopeValue = field.get(o1);
					payloadValue = field.get(o2);
					if((null == envelopeValue && null != payloadValue) 
							|| (null == payloadValue && null != envelopeValue)
							|| (null != envelopeValue && null != payloadValue && !envelopeValue.equals(payloadValue))){
						String str = o1.getClass().getName() +
								" has different values at: " + field.getName() + 
								", envelope value: " + envelopeValue + ", payload value: " + payloadValue; 
						return str;
					}
				}catch(Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionAsString = sw.toString();
					return field.getName() + ":  envelope = " + envelopeValue + ", payload = "+ payloadValue + ", " + exceptionAsString;
				}
			}
		}

		return "";
	}


	public static String toString(Object obj) {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append( obj.getClass().getName() );
		result.append( " Object {" );
		result.append(newLine);

		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = obj.getClass().getDeclaredFields();

		//print field names paired with their values
		for ( Field field : fields  ) {
			field.setAccessible(true);

			result.append("  ");
			try {
				result.append( field.getName() );
				result.append(": ");
				//requires access to private field:
				result.append( field.get(obj) );
			} catch ( IllegalAccessException ex ) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

	private static String convertStackTraceToString(Throwable throwable)  {
		try (StringWriter sw = new StringWriter(); 
				PrintWriter pw = new PrintWriter(sw)) 
		{
			throwable.printStackTrace(pw);
			return sw.toString();
		} 
		catch (IOException ioe) 
		{
			throw new IllegalStateException(ioe);
		}
	} 
	
	
	public static String getCmdBuLookupResponse(String param) {
		//citrus_http_query_params=senderAddress=ADHUBMDCS,senderQualifier=MS,receiverAddress=ADPARTMDCS,receiverQualifier=MS,
		System.out.print("getcmdBuLookupResponse citrus_http_query_params: " + param);
		
		int senderAddressIndex = param.indexOf("senderAddress");
		int senderQualifierIndex = param.indexOf("senderQualifier");
		int receiverAddressIndex = param.indexOf("receiverAddress");
		int receiverQualifierIndex = param.indexOf("receiverQualifier");
		
		
		String senderAddress = Strings.nullToEmpty(param.substring(senderAddressIndex+14, senderQualifierIndex-1));
		String senderQualifier = Strings.nullToEmpty(param.substring(senderQualifierIndex+16, receiverAddressIndex-1));
		String receiverAddress = Strings.nullToEmpty(param.substring(receiverAddressIndex+16, receiverQualifierIndex-1));
		String receiverQualifier = Strings.nullToEmpty(param.substring(receiverQualifierIndex+18));
		
		System.out.println("getcmdBuLookupResponse senderAddress=" + senderAddress + "senderQualifier=" + senderQualifier + "receiverAddress=" + receiverAddress + "receiverQualifier=" + receiverQualifier);
		
		try {
			FileReader fileReader = new FileReader("src/test/resources/testfiles/cmdBuLookupResponse.txt");
			
			JsonArray objects = Jsoner.deserializeMany(fileReader);

            Mapper mapper = new DozerBeanMapper();

            //JsonArray o = (JsonArray) objects.get(0);
            List<CmdBuLookupResponse> collect = objects.stream()
				.map(x -> mapper.map(x, CmdBuLookupResponse.class)).collect(Collectors.toList());
            
            //collect.forEach(x -> System.out.println(x));
            
            for(CmdBuLookupResponse cmd: collect) {
            	String sAddress = cmd.getSenderAddress();
        		String sQualifier = cmd.getSenderQualifier();
        		String rAddress = cmd.getReceiverAddress();
        		String rQualifier = cmd.getReceiverQualifier();

        		if(senderAddress.equals(sAddress) && senderQualifier.equals(sQualifier) 
        				&& receiverAddress.equals(rAddress) && receiverQualifier.equals(rQualifier)){
        			String response = cmd.getResponse();
        			System.out.print("getcmdBuLookupResponse: " + response);
        			return response;
        		}
            }
            
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getCmdCompanyLookupResponse(String buid) {
		System.out.print("getcmdCompanyLookupResponse buid: " + buid);
		String fileName = "src/test/resources/testfiles/cmdCompanyLookupResponse_" + buid + ".txt";
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}		
	}
	
	public static String getLensAccessToken() {
		String fileName = "src/test/resources/testfiles/accessToken.txt";
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}		
	}

	public static void main(String[] args) {
		String params="senderAddress=ADHUBMDCS,senderQualifier=MS,receiverAddress=ADPARTMDCS,receiverQualifier=MS";
		getCmdBuLookupResponse(params);
	}
}
