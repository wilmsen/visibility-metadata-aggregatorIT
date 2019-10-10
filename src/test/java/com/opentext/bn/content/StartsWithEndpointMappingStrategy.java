package com.opentext.bn.content;

import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.endpoint.adapter.mapping.EndpointAdapterMappingStrategy;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class StartsWithEndpointMappingStrategy  implements EndpointAdapterMappingStrategy {

    private Map<String, EndpointAdapter> adapterMappings = new HashMap<String, EndpointAdapter>();

    @Override
    public EndpointAdapter getEndpointAdapter(String mappingKey) {
        for (String key : adapterMappings.keySet()) {
            if (mappingKey.startsWith(key)) {
                return adapterMappings.get(key);
            }
        }
        throw new CitrusRuntimeException("Unable to find matching endpoint adapter with mapping key '" + mappingKey + "'");
    }

    public void setAdapterMappings(Map<String, EndpointAdapter> mappings) {
        this.adapterMappings = mappings;
    }
}
