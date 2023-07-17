package com.siwonkh.cleangpt_v1.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchSessionTokenDto {
    private int searchSessionId;

    public SearchSessionTokenDto (String serialized) throws JsonProcessingException {
        this.searchSessionId = new ObjectMapper()
                .readValue(serialized, SearchSessionTokenDto.class)
                .getSearchSessionId();
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
