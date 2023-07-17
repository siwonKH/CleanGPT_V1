package com.siwonkh.cleangpt_v1.dto;

import com.siwonkh.cleangpt_v1.entity.SearchSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchSessionDto {
    private SearchSession searchSession;

    private String searchSessionToken;

    private String creatorId;
}
