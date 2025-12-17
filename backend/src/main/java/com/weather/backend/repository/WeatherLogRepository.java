package com.weather.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weather.backend.entity.WeatherLog;

@Repository
public interface WeatherLogRepository extends JpaRepository<WeatherLog, Long> {

}