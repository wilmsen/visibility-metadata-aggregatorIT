package com.opentext.bn.content.avro;

import com.opentext.bn.converters.avro.entity.*;
import org.apache.avro.Schema;
import scala.Int;

import java.util.ArrayList;

public class SchemaRegistryMapper {

    private static ArrayList<String> schemaList = new ArrayList<>();

    private static void addSchema(Schema schema) {
        schemaList.add("{\"schema\":\"" + schema.toString().replaceAll("\"", "\\\\\"") + "\"}");
    }
    static {
        addSchema(ContentErrorEvent.SCHEMA$);
        addSchema(DeliveryCompletedEvent.SCHEMA$);
        addSchema(DeliveryErrorEvent.SCHEMA$);
        addSchema(DeliveryReadyForPickupEvent.SCHEMA$);
        addSchema(DocumentEvent.SCHEMA$);
        addSchema(EnvelopeEvent.SCHEMA$);
        addSchema(FgFaStatusEvent.SCHEMA$);
        addSchema(ReceiveCompletedEvent.SCHEMA$);
        addSchema(ReceiveErrorEvent.SCHEMA$);
        addSchema(TaskCompletedEvent.SCHEMA$);
    }
    public static String getSchemaId(String jsonSchemaString) {

        if (jsonSchemaString == null || jsonSchemaString.length() == 0) {
            throw new RuntimeException("SCHEMA REGISTRY OFFSET REQUEST WITH AN EMPTY SCHEMA");
        }
        for (int idx = 0; idx < schemaList.size(); idx++) {
            if (schemaList.get(idx).equals(jsonSchemaString)) {

                return "{\"id\":" + (idx+1) + "}";
            }
        }
        schemaList.add(jsonSchemaString);

        return "{\"id\":" + schemaList.size() + "}";
    }
    public static String getSchemaJsonString(String requestUrlIndex) {

        int    idx = 0;
        String temp = requestUrlIndex;

        int offset = temp.lastIndexOf("/") + 1;
        if (offset > 0) {
            temp = temp.substring(offset);
            idx = Integer.parseInt(temp.trim());
        }
        if (idx < 1) {
            throw new RuntimeException("SCHEMA REGISTRY OFFSET ["+ requestUrlIndex + "] must be greater than zero ");
        }

        if (idx > schemaList.size()+1) {
            throw new RuntimeException("SCHEMA REGISTRY OFFSET ["+ idx + "] greater than highest offset [" + (schemaList.size()+1) + "] ");
        }

        return schemaList.get(idx-1);
    }
}
