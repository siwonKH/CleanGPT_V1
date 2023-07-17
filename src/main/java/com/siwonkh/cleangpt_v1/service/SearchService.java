package com.siwonkh.cleangpt_v1.service;

import com.siwonkh.cleangpt_v1.dto.SearchSessionDto;
import com.siwonkh.cleangpt_v1.dto.SearchSessionTokenDto;
import com.siwonkh.cleangpt_v1.entity.SearchSession;
import com.siwonkh.cleangpt_v1.repository.SearchSessionRepository;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("searchService")
public class SearchService {
    @Autowired
    private SearchSessionRepository searchSessionRepository;

    @Autowired
    private SearchTokenService searchTokenService;

    public SearchSessionDto createSearchSession(SearchSessionDto searchSessionDto) throws JoseException {
        SearchSession searchSession = new SearchSession();
        searchSession.setCreatorId(searchSessionDto.getCreatorId());
        searchSessionDto.setSearchSession(searchSessionRepository.save(searchSession));

        SearchSessionTokenDto searchSessionTokenDto = new SearchSessionTokenDto();
        searchSessionTokenDto.setSearchSessionId(searchSession.getId());
        searchSessionDto.setSearchSessionToken(searchTokenService.sign(searchSessionTokenDto.toString()));

        return searchSessionDto;
    }

    public SearchSession getSearchSessionByToken(String token) throws Exception {
        SearchSessionTokenDto searchSessionTokenDto = new SearchSessionTokenDto(searchTokenService.verify(token));
        int sessionId = searchSessionTokenDto.getSearchSessionId();

        return searchSessionRepository.findById(sessionId).orElseThrow();
    }


}
