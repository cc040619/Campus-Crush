package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.service.WeatherService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Override
    public Map<String, Object> getToday(Long coupleId) {
        Map<String, Object> weather = new HashMap<>();
        weather.put("temp", 26);
        weather.put("desc", "多云转晴");
        weather.put("tip", "适合出门走走哦~");
        return weather;
    }
}
