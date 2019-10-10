/*
 * Copyright 2006-2017 the original author or authors.
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

import com.consol.citrus.context.TestContextFactory;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.endpoint.adapter.RequestDispatchingEndpointAdapter;
import com.consol.citrus.endpoint.adapter.StaticEndpointAdapter;
import com.consol.citrus.endpoint.adapter.mapping.HeaderMappingKeyExtractor;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.http.server.HttpServer;
import com.consol.citrus.kafka.embedded.EmbeddedKafkaServer;
import com.consol.citrus.kafka.embedded.EmbeddedKafkaServerBuilder;
import com.consol.citrus.kafka.endpoint.KafkaEndpoint;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.validation.json.JsonTextMessageValidator;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import com.opentext.bn.content.avro.SchemaRegistryMapper;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class EndpointConfig implements WebMvcConfigurer {

	@Bean
	public HttpClient eventInjectorClient() {
		return CitrusEndpoints.http().client().requestUrl("http://localhost:9443").build();
	}

	@Bean
	public NamespaceContextBuilder namespaceContextBuilder() {
		NamespaceContextBuilder namespaceContextBuilder = new NamespaceContextBuilder();
		namespaceContextBuilder.setNamespaceMappings(Collections.singletonMap("xh", "http://www.w3.org/1999/xhtml"));
		return namespaceContextBuilder;
	}

	@Bean
	public EmbeddedKafkaServer embeddedKafkaServer() {
		return new EmbeddedKafkaServerBuilder().kafkaServerPort(9092).topics("visibility.platform.taskcompleted",
				"visibility.platform.taskerror", "visibility.platform.receivecompleted",
				"visibility.platform.receiveerror", "visibility.platform.deliverycompleted",
				"visibility.platform.deliveryreadyforpickup", "visibility.platform.deliveryerror",
				"visibility.platform.document", "visibility.platform.envelope", "visibility.platform.introspection",
				"visibility.platform.contenterror", "visibility.introspection.document",
				"visibility.introspection.envelope", "visibility.introspection.contenterror", "visibility.fgfa.status",
				"visibility.notificationrequest", "visibility.internal.facycle", "visibility.internal.fgfastatus")
				.build();
	}

	@Bean
	public HttpServer avroRegistryServer() {
		HttpServer server = new HttpServer();
		server.setPort(8081);
		server.setAutoStart(true);
		server.setEndpointAdapter(dispatchingEndpointAdapter(null, null));
		return server;
	}

	@Bean
	public RequestDispatchingEndpointAdapter dispatchingEndpointAdapter(
			@Autowired ApplicationContext applicationContext, @Autowired TestContextFactory testContextFactory) {
		RequestDispatchingEndpointAdapter dispatchingEndpointAdapter = new RequestDispatchingEndpointAdapter();
		dispatchingEndpointAdapter.setMappingKeyExtractor(mappingKeyExtractor());
		dispatchingEndpointAdapter.setMappingStrategy(mappingStrategy());
		dispatchingEndpointAdapter.setApplicationContext(applicationContext);
		dispatchingEndpointAdapter.setTestContextFactory(testContextFactory);
		return dispatchingEndpointAdapter;
	}

	@Bean
	public HeaderMappingKeyExtractor mappingKeyExtractor() {
		HeaderMappingKeyExtractor mappingKeyExtractor = new HeaderMappingKeyExtractor();
		mappingKeyExtractor.setHeaderName(HttpMessageHeaders.HTTP_REQUEST_URI);
		return mappingKeyExtractor;
	}

	@Bean
	public StartsWithEndpointMappingStrategy mappingStrategy() {
		StartsWithEndpointMappingStrategy mappingStrategy = new StartsWithEndpointMappingStrategy();

		Map<String, EndpointAdapter> mappings = new HashMap<>();

		// mappings.put("/subjects/todo.kafka.inbound-value/versions",
		// todoResponseAdapter());
		mappings.put("/subjects/", todoResponseAdapter());
		mappings.put("/schemas/ids", todoResponseAdapter2());

		mappingStrategy.setAdapterMappings(mappings);
		return mappingStrategy;
	}

	@Bean
	public EndpointAdapter todoResponseAdapter() {
		return new StaticEndpointAdapter() {
			@Override
			protected Message handleMessageInternal(Message message) {
				// HttpMessageHeaders.HTTP_REQUEST_URI
				if (message.getPayload() != null && String.class.isInstance(message.getPayload())) {
					return new DefaultMessage(SchemaRegistryMapper.getSchemaId((String) message.getPayload()),
							message.getHeaders());
				}
				throw new CitrusRuntimeException("Failed to read message payload empty");
			}
		};
	}

	@Bean
	public EndpointAdapter todoResponseAdapter2() {
		return new StaticEndpointAdapter() {
			@Override
			protected Message handleMessageInternal(Message message) {

				if (message.getPayload() != null && String.class.isInstance(message.getPayload())) {
					Map<String, Object> headerMap = new HashMap<>();
					if (message.getHeaders() != null) {
						headerMap.putAll(message.getHeaders());
					}
					headerMap.put("Content-Type", "application/vnd.schemaregistry.v1+json");
					return new DefaultMessage(
							SchemaRegistryMapper.getSchemaJsonString((String) headerMap.get("citrus_http_request_uri")),
							message.getHeaders());
				}
				throw new CitrusRuntimeException("Failed to read message payload empty");
			}
		};
	}

	@Bean
	public KafkaEndpoint receiveCompletedKafkaEndpoint() {
		Map<String, Object> props = new HashMap<>();
		props.put("schema.registry.url", "http://127.0.0.1:8081");
		props.put("specific.avro.reader", "true");
		props.put("value.subject.name.strategy",
				io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class.getName());
		return CitrusEndpoints.kafka().asynchronous().server("localhost:9092")
				.topic("visibility.platform.receivecompleted").keyDeserializer(StringDeserializer.class)
				.valueDeserializer(KafkaAvroDeserializer.class).offsetReset("earliest").consumerGroup("CitrusTest2")
				.consumerProperties(props).build();
	}

	@Bean
	public Map<String, Object> visibilityInjectorSenderProperties() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put("schema.registry.url", "http://127.0.0.1:8081");
		// props.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
		props.put("value.subject.name.strategy",
				io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class.getName());

		return props;
	}

		
	@Bean
	public KafkaEndpoint documentKafkaEndpoint() {
		Map<String, Object> props = new HashMap<>();
		props.put("schema.registry.url", "http://127.0.0.1:8081");
		props.put("specific.avro.reader", "true");
		props.put("value.subject.name.strategy",
				io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class.getName());
		return CitrusEndpoints.kafka().asynchronous().server("localhost:9092")
				.topic("visibility.introspection.document").keyDeserializer(StringDeserializer.class)
				.valueDeserializer(KafkaAvroDeserializer.class).offsetReset("earliest").consumerGroup("CitrusTestDOC")
				.consumerProperties(props).build();
	}
	
	
	@Bean
	public KafkaEndpoint envelopeKafkaEndpoint() {
		Map<String, Object> props = new HashMap<>();
		props.put("schema.registry.url", "http://127.0.0.1:8081");
		props.put("specific.avro.reader", "true");
		props.put("value.subject.name.strategy",
				io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class.getName());
		return CitrusEndpoints.kafka().asynchronous().server("localhost:9092")
				.topic("visibility.introspection.envelope").keyDeserializer(StringDeserializer.class)
				.valueDeserializer(KafkaAvroDeserializer.class).offsetReset("earliest").consumerGroup("CitrusTestIC")
				.consumerProperties(props).build();
	}
	
	@Bean
	public KafkaEndpoint contentErrorKafkacEndpoint() {
		Map<String, Object> props = new HashMap<>();
		props.put("schema.registry.url", "http://127.0.0.1:8081");
		props.put("specific.avro.reader", "true");
		props.put("value.subject.name.strategy",
				io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class.getName());
		return CitrusEndpoints.kafka().asynchronous().server("localhost:9092")
				.topic("visibility.introspection.contenterror").keyDeserializer(StringDeserializer.class)
				.valueDeserializer(KafkaAvroDeserializer.class).offsetReset("earliest").consumerGroup("CitrusTestError")
				.consumerProperties(props).build();
	}

	@Bean
	public KafkaEndpoint citrusKafkaEndpoint() {
		return CitrusEndpoints.kafka().asynchronous().producerProperties(visibilityInjectorSenderProperties()).build();
	}

}
