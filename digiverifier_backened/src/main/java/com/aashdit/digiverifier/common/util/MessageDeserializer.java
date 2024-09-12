package com.aashdit.digiverifier.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aashdit.digiverifier.common.dto.EPFOResponseDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageDeserializer extends JsonDeserializer<List<EPFOResponseDto.EmploymentDetail>> {

    @Override
    public List<EPFOResponseDto.EmploymentDetail> deserialize(JsonParser jp, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();

        List<EPFOResponseDto.EmploymentDetail> messages = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode element : node) {
            	EPFOResponseDto.EmploymentDetail message = mapper.treeToValue(element, EPFOResponseDto.EmploymentDetail.class);
                messages.add(message);
            }
        } else if (node.isTextual()) {
        	EPFOResponseDto.EmploymentDetail message = new EPFOResponseDto.EmploymentDetail();
            message.setName(node.asText());
            messages.add(message);
        }

        return messages;
    }
}


