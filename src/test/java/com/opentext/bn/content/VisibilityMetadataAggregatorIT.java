/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opentext.bn.content;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.NoWrappingJsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpServerActionBuilder;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.server.HttpServer;
import com.consol.citrus.kafka.endpoint.KafkaEndpoint;
import com.consol.citrus.kafka.message.KafkaMessage;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opentext.bn.converters.avro.entity.ContentErrorEvent;
import com.opentext.bn.converters.avro.entity.DocumentEvent;
import com.opentext.bn.converters.avro.entity.EnvelopeEvent;
import com.opentext.bn.converters.avro.entity.FileIntrospectedEvent;
import com.opentext.bn.converters.avro.entity.ReceiveCompletedEvent;
import com.opentext.bn.converters.avro.entity.TransactionContext;

public class VisibilityMetadataAggregatorIT extends TestNGCitrusTestRunner {

	@Autowired
	@Qualifier("eventInjectorClient")
	private HttpClient eventInjectorClient;

	@Autowired
	@Qualifier("cmdClient")
	private HttpClient cmdClient;

	@Autowired
	@Qualifier("lensServer")
	private HttpServer lensServer;

	@Autowired
	@Qualifier("citrusKafkaEndpoint")
	private KafkaEndpoint citrusKafkaEndpoint;

	@Autowired
	@Qualifier("receiveCompletedKafkaEndpoint")
	private KafkaEndpoint receiveCompletedKafkaEndpoint;

	@Autowired
	@Qualifier("documentKafkaEndpoint")
	private KafkaEndpoint documentKafkaEndpoint;

	@Autowired
	@Qualifier("envelopeKafkaEndpoint")
	private KafkaEndpoint envelopeKafkaEndpoint;

	@Autowired
	@Qualifier("fileKafkaEndpoint")
	private KafkaEndpoint fileKafkaEndpoint;

	@Autowired
	@Qualifier("contentErrorKafkacEndpoint")
	private KafkaEndpoint contentErrorKafkacEndpoint;

	@Autowired
	@Qualifier("cmdResponseAdapter")
	private EndpointAdapter cmdResponseAdapter;



	@Test
	@CitrusTest
	public void process_ReceiveCompletedIT() {

		try {
			String controlFile = "src/test/resources/testfiles/controlFile2.txt";
			String eventJsonString = new String(Files.readAllBytes(Paths.get(controlFile)));

			String transactionId = UUID.randomUUID().toString();
			echo("transactionId: " + transactionId);
			String payloadId = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
			echo("payloadId: " + payloadId);

			String receiveCompletedJsonString = TestHelper.setVariableValues(eventJsonString, transactionId, payloadId);

			ObjectMapper mapper = new ObjectMapper();
			ReceiveCompletedEvent receiveCompleteEvent = mapper.readValue(receiveCompletedJsonString, ReceiveCompletedEvent.class);


			send(sendMessageBuilder -> {

				sendMessageBuilder.endpoint(citrusKafkaEndpoint).message(new KafkaMessage(receiveCompleteEvent)
						.topic("visibility.platform.receivecompleted").messageKey(receiveCompleteEvent.getTransactionContext().getTransactionId()));

			});

			sleep(30000);

			http(httpActionBuilder -> httpActionBuilder
					.server(lensServer)
					.respond()
					.contentType("application/json") 
					);


			receive(receiveMessageBuilder -> {
				receiveMessageBuilder.endpoint(receiveCompletedKafkaEndpoint)
				.validationCallback(new JsonMappingValidationCallback<ReceiveCompletedEvent>(ReceiveCompletedEvent.class, mapper) {

					@Override
					public void validate(ReceiveCompletedEvent payload, Map<String, Object> headers, TestContext context) {
						String receiveCompletedResponse = null;
						receiveCompletedResponse = convertToJson(payload);
						echo("Payload received - receiveCompletedResponse : " + receiveCompletedResponse);

						try {
							JSONAssert.assertEquals(receiveCompletedJsonString, receiveCompletedResponse, JSONCompareMode.LENIENT);
						} catch (JSONException e) {
							e.printStackTrace();
							throw new ValidationException("this test failed : {} ", e);
						}

						Assert.assertNotNull(payload);
						//Assert.assertNotNull("missing transaction Id in the payload: ", payload.getTransactionId());
						Assert.assertNotNull("missing citrus_kafka_messageKey: ", String.valueOf(headers.get("citrus_kafka_messageKey")));
						Assert.assertEquals(String.valueOf(headers.get("citrus_kafka_messageKey")), transactionId, "transactionid is not matching with kafka key");

						// Compare all properties
						String resultStr = TestHelper.haveSamePropertyValues(receiveCompleteEvent, payload);
						echo("Validation Result: " + resultStr.toString());
						Boolean result = resultStr.isEmpty()? true: false;								
						Assert.assertEquals(Boolean.TRUE.toString(), result.toString(), "ReceiveCompletedEvent properies not matched: " + resultStr);
					}

				});

			});


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ValidationException("this test failed : {} ", e);

		}

	}


	@CitrusTest
	public void httpServerActionTest() {
		http(httpActionBuilder -> httpActionBuilder
				.server("helloHttpServer")
				.receive()
				.post("/test")
				.payload("<testRequestMessage>" +
						"<text<Hello HttpServer</text>" +
						"</testRequestMessage>")
				.contentType("application/xml")
				.accept("application/xml, */*")
				.header("X-CustomHeaderId", "${custom_header_id}")
				.header("Authorization", "Basic c29tZVVzZXJuYW1lOnNvbWVQYXNzd29yZA==")
				.extractFromHeader("X-MessageId", "message_id")
				);

		http(httpActionBuilder -> httpActionBuilder
				.server("helloHttpServer")
				.send()
				.response(HttpStatus.OK)
				.payload("<testResponseMessage>" +
						"<text<Hello Citrus</text>" +
						"</testResponseMessage>")
				.version("HTTP/1.1")
				.contentType("application/xml")
				.header("X-CustomHeaderId", "${custom_header_id}")
				.header("X-MessageId", "${message_id}")
				);
	}

	@Test
	@CitrusTest
	public void process_CMDRestCallIT() {

		try {
			String controlFile = "src/test/resources/testfiles/controlFile2.txt";
			String eventJsonString = new String(Files.readAllBytes(Paths.get(controlFile)));

			String transactionId = UUID.randomUUID().toString();
			echo("transactionId: " + transactionId);
			String payloadId = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
			echo("payloadId: " + payloadId);

			String receiveCompletedJsonString = TestHelper.setVariableValues(eventJsonString, transactionId, payloadId);

			ObjectMapper mapper = new ObjectMapper();
			ReceiveCompletedEvent receiveCompleteEvent = mapper.readValue(receiveCompletedJsonString, ReceiveCompletedEvent.class);

			TransactionContext transactionContext = receiveCompleteEvent.getTransactionContext();
			String receiverInfo = transactionContext.getReceiverAddress();
			String senderInfo = transactionContext.getSenderAddress();
			int receiverIndex = receiverInfo.indexOf(":");
			int senderIndex = senderInfo.indexOf(":");
			String senderQualifier = senderInfo.substring(0, senderIndex);
			String senderAddress= senderInfo.substring(senderIndex + 1);
			String receiverQualifier = receiverInfo.substring(0, receiverIndex);
			String receiverAddress = receiverInfo.substring(receiverIndex + 1);
			echo("receiverAddress: " + receiverAddress + ", receiverQualifier: " + receiverQualifier + ", senderAddress: " + senderAddress + ", senderQualifier: " + senderQualifier );


			//CMD REST Call 1 - buid Look Up By Addr
			//http://qtotcra.qa.gxsonline.net:8080/communitymasterdata/rest/v1/resolver/rootParentsByAddresses?senderAddress=ADHUBMDCS&senderQualifier=MS&receiverAddress=ADPARTMDCS&receiverQualifier=MS
			echo("Start CMD Call 1 - buid Look Up By Addr");

			String buidLookUpByAddr = "/resolver/rootParentsByAddresses?senderAddress=" + senderAddress + "&senderQualifier=" + senderQualifier
					+ "&receiverAddress=" + receiverAddress + "&receiverQualifier=" + receiverQualifier;
			String senderAddressBuIdExpected = "GC22698817YV";
			String receiverAddressBuIdExpected = "GC28464997QM";


			http(httpActionBuilder -> httpActionBuilder
					.client(cmdClient)
					.send()
					.get(buidLookUpByAddr)
					.header(CmdConstants.IM_PRINCIPAL_TYPE, CmdConstants.CMD_PRINCIPAL_TYPE)
					.header(CmdConstants.IM_COMMUNITY_ID, CmdConstants.CMD_COMMUNITY_ID)
					.header(CmdConstants.IM_SERVICE_INSTANCE_ID, CmdConstants.CMD_INSTANCE_ID)
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType("application/json"));

			http(httpActionBuilder -> httpActionBuilder
					.client(cmdClient)
					.receive()
					.response(HttpStatus.OK)
					.messageType(MessageType.JSON)
					.extractFromPayload("$.senderAddressBuId", "senderAddressBuIdReceived")
					.extractFromPayload("$.receiverAddressBuId", "receiverAddressBuIdReceived")
					.validate("$.senderAddressBuId", senderAddressBuIdExpected)
					.validate("$.receiverAddressBuId", receiverAddressBuIdExpected));

			echo("senderAddressBuIdReceived - ${senderAddressBuIdReceived};   receiverAddressBuIdReceived - ${receiverAddressBuIdReceived} ");
			echo("Finish CMD Call 1 - buid Look Up By Addr");

			//CMD REST Call 2 - Company Name Lookup By Sender BUID
			//http://qtotcra.qa.gxsonline.net:8080/communitymasterdata/rest/v1/businessUnits/GC22698817YV
			echo("Start CMD Call 2 - Company Name Lookup By Sender BUID");

			String companyNameLookup = "/businessUnits/" + senderAddressBuIdExpected;
			String companyNameExpected = "AD-TGMSHUBCOMP";

			http(httpActionBuilder -> httpActionBuilder
					.client(cmdClient)
					.send()
					.get(companyNameLookup)
					.header(CmdConstants.IM_PRINCIPAL_TYPE, CmdConstants.CMD_PRINCIPAL_TYPE)
					.header(CmdConstants.IM_COMMUNITY_ID, CmdConstants.CMD_COMMUNITY_ID)
					.header(CmdConstants.IM_SERVICE_INSTANCE_ID, CmdConstants.CMD_INSTANCE_ID)
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType("application/json"));

			http(httpActionBuilder -> httpActionBuilder
					.client(cmdClient)
					.receive()
					.response(HttpStatus.OK)
					.messageType(MessageType.JSON)
					.extractFromPayload("$.name", "companyNameReceived")
					.validate("$.name", companyNameExpected));

			echo("companyNameReceived - ${companyNameReceived}");

			echo("Finish CMD Call 2 - Company Name Lookup By BUID");

			//CMD REST Call 3 - Company Name Lookup By Receiver BUID
			//http://qtotcra.qa.gxsonline.net:8080/communitymasterdata/rest/v1/businessUnits/GC22698817YV
			echo("Start CMD REST Call 3 - Company Name Lookup By Receiver BUID");

			String companyNameLookup2 = "/businessUnits/" + receiverAddressBuIdExpected;
			String companyNameExpected2 = "AD-TGMSPARTCOMP1";

			http(httpActionBuilder -> httpActionBuilder
					.client(cmdClient)
					.send()
					.get(companyNameLookup2)
					.header(CmdConstants.IM_PRINCIPAL_TYPE, CmdConstants.CMD_PRINCIPAL_TYPE)
					.header(CmdConstants.IM_COMMUNITY_ID, CmdConstants.CMD_COMMUNITY_ID)
					.header(CmdConstants.IM_SERVICE_INSTANCE_ID, CmdConstants.CMD_INSTANCE_ID)
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType("application/json"));

			http(httpActionBuilder -> httpActionBuilder
					.client(cmdClient)
					.receive()
					.response(HttpStatus.OK)
					.messageType(MessageType.JSON)
					.extractFromPayload("$.name", "companyNameReceived2")
					.validate("$.name", companyNameExpected2));

			echo("companyNameReceived2 - ${companyNameReceived2}");

			echo("Finish CMD REST Call 3 - Company Name Lookup By Receiver BUID");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ValidationException("this test failed : {} ", e);

		}

	}


	@Test
	@CitrusTest 	
	public void process_FileIntrospectedEventIT() {

		try { 
			String testFilePath = "src/test/resources/testfiles/fileIntrospected.txt";

			String eventJsonString = new String(Files.readAllBytes(Paths.get(testFilePath)));
			System.out.println("fileIntrospectedEventJsonString 1: " + eventJsonString);

			String transactionId = UUID.randomUUID().toString();

			echo("transactionId: " + transactionId);

			String payloadId = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
			echo("payloadId: " + payloadId);

			eventJsonString = TestHelper.setVariableValues(eventJsonString, transactionId, payloadId);

			System.out.println("fileIntrospectedEventJsonString 2: " + eventJsonString);
			ObjectMapper mapper = new ObjectMapper(); 
			FileIntrospectedEvent envelope = mapper.readValue(eventJsonString, FileIntrospectedEvent.class);
			System.out.println("Envelope Send:");
			System.out.println(TestHelper.toString(envelope));
			send(sendMessageBuilder -> {

				sendMessageBuilder.endpoint(citrusKafkaEndpoint).message( new
						KafkaMessage(envelope).topic("visibility.introspection.file").messageKey(transactionId));

			});

			receive(receiveMessageBuilder -> {
				receiveMessageBuilder.endpoint(fileKafkaEndpoint).messageType(MessageType.BINARY)
				.message(new KafkaMessage(envelope).messageKey(transactionId)).validationCallback(
						new JsonMappingValidationCallback<FileIntrospectedEvent>(FileIntrospectedEvent.class,
								mapper) {

							@Override
							public void validate(FileIntrospectedEvent payload, Map<String, Object> headers, TestContext context) {
								System.out.println("Payload Receive:");
								System.out.println(TestHelper.toString(payload));
								Assert.assertNotNull(payload);
								Assert.assertNotNull("missing transaction Id in the payload: ", payload.getTransactionId());
								Assert.assertNotNull("missing citrus_kafka_messageKey: ", String.valueOf(headers.get("citrus_kafka_messageKey")));
								Assert.assertEquals(String.valueOf(headers.get("citrus_kafka_messageKey")), payload.getTransactionId(), "transactionid is not matching with kafka key");
								// Compare all properties
								String resultStr = TestHelper.haveSamePropertyValues(envelope, payload);
								echo("Validation Result: " + resultStr.toString());
								Boolean result = resultStr.isEmpty()? true: false;								
								Assert.assertEquals(Boolean.TRUE.toString(), result.toString(), "FileIntrospectedEvent properies not matched: " + resultStr);
							}
						});
			});

		}catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace(); }

	}


	@Test
	@CitrusTest 	
	public void process_EnvelopeIntrospectedEventIT() {

		try { 

			String testFilePath = "src/test/resources/testfiles/envelopeIntrospected.txt";

			String eventJsonString = new String(Files.readAllBytes(Paths.get(testFilePath)));
			System.out.println("envelopeIntrospectedEventJsonString 1: " + eventJsonString);

			String transactionId = UUID.randomUUID().toString();
			echo("transactionId: " + transactionId);

			String payloadId = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
			echo("payloadId: " + payloadId);

			eventJsonString = TestHelper.setVariableValues(eventJsonString, transactionId, payloadId);

			System.out.println("envelopeIntrospectedEventJsonString 2: " + eventJsonString);

			ObjectMapper mapper = new ObjectMapper(); 
			EnvelopeEvent envelope = mapper.readValue(eventJsonString, EnvelopeEvent.class);

			send(sendMessageBuilder -> {

				sendMessageBuilder.endpoint(citrusKafkaEndpoint).message( new
						KafkaMessage(envelope).topic("visibility.introspection.envelope").messageKey(transactionId));

			});

			receive(receiveMessageBuilder -> {
				receiveMessageBuilder.endpoint(envelopeKafkaEndpoint).messageType(MessageType.BINARY)
				.message(new KafkaMessage(envelope).messageKey(transactionId)).validationCallback(
						new JsonMappingValidationCallback<EnvelopeEvent>(EnvelopeEvent.class, mapper) {

							@Override
							public void validate(EnvelopeEvent payload, Map<String, Object> headers, TestContext context) {
								Assert.assertNotNull(payload);
								Assert.assertNotNull("missing transaction Id in the payload: ", payload.getTransactionId());
								Assert.assertNotNull("missing citrus_kafka_messageKey: ", String.valueOf(headers.get("citrus_kafka_messageKey")));
								Assert.assertEquals(String.valueOf(headers.get("citrus_kafka_messageKey")), payload.getTransactionId(), "transactionid is not matching with kafka key");
								// Compare all properties
								String resultStr = TestHelper.haveSamePropertyValues(envelope, payload);
								echo("Validation Result: " + resultStr.toString());
								Boolean result = resultStr.isEmpty()? true: false;								
								Assert.assertEquals(Boolean.TRUE.toString(), result.toString(), "FileIntrospectedEvent properies not matched: " + resultStr);
							}
						});
			});


		}catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace(); }

	}



	@Test
	@CitrusTest
	public void process_DocumentIntrospectedEventIT() {

		try { 

			String testFilePath = "src/test/resources/testfiles/documentIntrospected.txt";

			String eventJsonString = new String(Files.readAllBytes(Paths.get(testFilePath)));
			System.out.println("documentIntrospectedEventJsonString 1: " + eventJsonString);

			String transactionId = UUID.randomUUID().toString();

			echo("transactionId: " + transactionId);

			String payloadId = "Q14E-201912000000000" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 12);
			echo("payloadId: " + payloadId);

			eventJsonString = TestHelper.setVariableValues(eventJsonString, transactionId, payloadId);

			System.out.println("documentIntrospectedEventJsonString 2: " + eventJsonString);

			ObjectMapper mapper = new ObjectMapper(); 
			DocumentEvent envelope = mapper.readValue(eventJsonString, DocumentEvent.class);

			send(sendMessageBuilder -> {

				sendMessageBuilder.endpoint(citrusKafkaEndpoint).message( new
						KafkaMessage(envelope).topic("visibility.introspection.document").messageKey(transactionId));

			});

			receive(receiveMessageBuilder -> {
				receiveMessageBuilder.endpoint(documentKafkaEndpoint).messageType(MessageType.BINARY)
				.message(new KafkaMessage(envelope).messageKey(transactionId)).validationCallback(
						new JsonMappingValidationCallback<DocumentEvent>(DocumentEvent.class, mapper) {

							@Override
							public void validate(DocumentEvent payload, Map<String, Object> headers, TestContext context){
								Assert.assertNotNull(payload);
								Assert.assertNotNull("missing transaction Id in the payload: ", payload.getTransactionId());
								Assert.assertNotNull("missing citrus_kafka_messageKey: ", String.valueOf(headers.get("citrus_kafka_messageKey")));
								Assert.assertEquals(String.valueOf(headers.get("citrus_kafka_messageKey")), payload.getTransactionId(), "transactionid is not matching with kafka key");
								// Compare all properties
								String resultStr = TestHelper.haveSamePropertyValues(envelope, payload);
								echo("Validation Result: " + resultStr.toString());
								Boolean result = resultStr.isEmpty()? true: false;								
								Assert.assertEquals(Boolean.TRUE.toString(), result.toString(), "FileIntrospectedEvent properies not matched: " + resultStr);
							}
						});
			});
		}catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace(); }

	}

	@Test
	@CitrusTest
	public void process_ContentErrorEventIT() {

		try { 

			String testFilePath = "src/test/resources/testfiles/contentError.txt";

			String eventJsonString = new String(Files.readAllBytes(Paths.get(testFilePath)));
			System.out.println("contentErrorEventJsonString 1: " + eventJsonString);

			String transactionId = UUID.randomUUID().toString();

			echo("transactionId: " + transactionId);

			eventJsonString = TestHelper.setVariableValues(eventJsonString, transactionId, "");

			System.out.println("contentErrorEventJsonString 2: " + eventJsonString);

			ObjectMapper mapper = new ObjectMapper(); 
			ContentErrorEvent envelope = mapper.readValue(eventJsonString, ContentErrorEvent.class);

			send(sendMessageBuilder -> {
				sendMessageBuilder.endpoint(citrusKafkaEndpoint).message( new
						KafkaMessage(envelope).topic("visibility.introspection.contenterror").messageKey(transactionId));

			});

			receive(receiveMessageBuilder -> {
				receiveMessageBuilder.endpoint(contentErrorKafkacEndpoint)
				.messageType( MessageType.BINARY)
				.message(new  KafkaMessage(envelope).messageKey(transactionId))
				.validationCallback( new JsonMappingValidationCallback<ContentErrorEvent>(ContentErrorEvent.class, mapper) {

					@Override 
					public void validate(ContentErrorEvent payload, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(payload);
						Assert.assertNotNull("missing transaction Id in the payload: ", payload.getTransactionId());
						Assert.assertNotNull("missing citrus_kafka_messageKey: ", String.valueOf(headers.get("citrus_kafka_messageKey")));
						Assert.assertEquals(String.valueOf(headers.get("citrus_kafka_messageKey")), payload.getTransactionId(), "transactionid is not matching with kafka key");

						// Compare all properties
						String resultStr = TestHelper.haveSamePropertyValues(envelope, payload);
						echo("Validation Result: " + resultStr.toString());
						Boolean result = resultStr.isEmpty()? true: false;								
						Assert.assertEquals(Boolean.TRUE.toString(), result.toString(), "FileIntrospectedEvent properies not matched: " + resultStr);
					} 
				}); 
			});

		}catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace(); }

	}


	public String writeJson(ReceiveCompletedEvent receiveEvent) throws IOException {

		abstract class IgnoreSchemaProperty
		{
			// You have to use the correct package for JsonIgnore,
			// fasterxml or codehaus
			@JsonIgnore abstract void getSchema();
		}


		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
		om.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
		om.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		om.addMixIn(ReceiveCompletedEvent.class, IgnoreSchemaProperty.class);

		String controlFileString = om.writer().writeValueAsString(receiveEvent);

		return controlFileString;
	}

	public String convertToJson(ReceiveCompletedEvent record) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			NoWrappingJsonEncoder jsonEncoder = new NoWrappingJsonEncoder(record.getSchema(), outputStream);
			DatumWriter<GenericRecord> writer = record instanceof SpecificRecord ?
					new SpecificDatumWriter<>(record.getSchema()) :
						new GenericDatumWriter<>(record.getSchema());
					writer.write(record, jsonEncoder);
					jsonEncoder.flush();
					return outputStream.toString();
		} catch (IOException e) {
			throw new ValidationException("Failed to convert to JSON.", e);
		}
	}
}
