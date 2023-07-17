package com.siwonkh.cleangpt_v1.repository;

import com.siwonkh.cleangpt_v1.entity.SearchSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchSessionRepository extends JpaRepository<SearchSession, Integer> {

}
