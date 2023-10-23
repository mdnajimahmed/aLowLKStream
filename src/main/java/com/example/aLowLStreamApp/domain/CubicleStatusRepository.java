package com.example.aLowLStreamApp.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CubicleStatusRepository extends CrudRepository<CubicleStatus, String> {
}