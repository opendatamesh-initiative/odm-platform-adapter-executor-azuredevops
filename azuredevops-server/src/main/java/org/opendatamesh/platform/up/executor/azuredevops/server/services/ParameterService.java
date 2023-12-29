package org.opendatamesh.platform.up.executor.azuredevops.server.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ParameterService {

    private ObjectMapper parameterMapper = ObjectMapperFactory.JSON_MAPPER;

    private static final Logger logger = LoggerFactory.getLogger(ParameterService.class);

    protected Map<String, String> extractParamsFromContext(
            Map<String, String> params,
            Map<String, Object> context
    ) {

        ObjectNode jsonContext = parameterMapper.valueToTree(context);

        if(jsonContext != null) {
            String paramVal;
            for (String param : params.keySet()) {
                paramVal = params.get(param);
                if(paramVal.startsWith("${")) {
                    String[] paramTree = paramNameToParamTree(paramVal);
                    JsonNode subContext = jsonContext.deepCopy();
                    Boolean paramFoundFlag = true;
                    for(String node : paramTree) {
                        try {
                            subContext = subContext.get(node);
                        } catch (Exception e) {
                            logger.warn("Impossible to extract parameter from context. Skipped. ", e);
                            paramFoundFlag = false;
                            break;
                        }
                    }
                    String paramNewVal = subContext != null ? subContext.toString() : null;
                    if(paramFoundFlag) {
                        params.put(param, paramNewVal);
                    }
                }
            }
        }

        return params;
    }

    private String[] paramNameToParamTree(String parameterName) {
        return parameterName
                .replace("${","")
                .replace("}","")
                .split("\\.");
    }

}
