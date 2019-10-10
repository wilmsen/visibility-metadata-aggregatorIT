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
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.kafka.endpoint.KafkaEndpoint;
import com.consol.citrus.kafka.message.KafkaMessage;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opentext.bn.converters.avro.entity.ReceiveCompletedEvent;

public class EventInjectorIT extends TestNGCitrusTestRunner {

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

	@Test
	@CitrusTest
		public void process_ReceiveCompletedIT() {

		try {

			//String receiveCompletedFilepath = "src/test/resources/testfiles/receiveCompleted001.txt";
			String controlFile = "src/test/resources/testfiles/controlFile.txt";
			String receiveCompletedJsonString;

			variable("transactionId", UUID.randomUUID().toString());
			
				
			receiveCompletedJsonString = new String(Files.readAllBytes(Paths.get(controlFile)));


			ObjectMapper mapper = new ObjectMapper();
			ReceiveCompletedEvent receiveCompleteEvent = mapper.readValue(receiveCompletedJsonString,
					ReceiveCompletedEvent.class);

			send(sendMessageBuilder -> {

				sendMessageBuilder.endpoint(citrusKafkaEndpoint).message(new KafkaMessage(receiveCompleteEvent)
						.topic("visibility.platform.receivecompleted").messageKey(receiveCompleteEvent.getTransactionContext().getTransactionId()));

			});

			// sleep(60000);

			receive(receiveMessageBuilder -> {
				receiveMessageBuilder.endpoint(receiveCompletedKafkaEndpoint)
						// .header("KafkaMessageHeaders.TOPIC",
						// "visibility.platform.receivecompleted")
						// .header("KafkaMessageHeaders.OFFSET",
						// Matchers.greaterThanOrEqualTo(0))
			
				   			         
						.validationCallback(new JsonMappingValidationCallback<ReceiveCompletedEvent>(
								ReceiveCompletedEvent.class, mapper) {

							@Override
							public void validate(ReceiveCompletedEvent payload, Map<String, Object> headers,
									TestContext context) {
								// TODO Auto-generated method stub

								String receiveCompletedResponse = null;
								
								

								// mapper.enable(SerializationFeature.INDENT_OUTPUT);
								  receiveCompletedResponse = convertToJson(payload);
								  System.out.println("receiveCompletedResponse : {} " + receiveCompletedResponse);

								try {
									JSONAssert.assertEquals(receiveCompletedJsonString, receiveCompletedResponse,
											JSONCompareMode.LENIENT);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									throw new ValidationException("this test failed : {} ", e);
								}
								
								

								// Assert.assertNotNull("payload is null : ", payload);
								// Assert.assertNotNull("missing transaction Id in the payload : ",
								// payload.getTransactionContext().getTransactionId());
								// Assert.assertEquals("transactionid is not matching with kafka key : ",
								// payload.getTransactionContext().getTransactionId(),
								// headers.get("citrus_kafka_messageKey"));

								// Assert.assertEquals(receiveCompleteEvent, payload);

							}

						});

			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ValidationException("this test failed : {} ", e);

		}

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
