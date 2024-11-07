package com.fcm.service;

import com.fcm.entity.calenderEntity;
import com.fcm.repository.calenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class calendarService {

    @Autowired
    private calenderRepository repository;

    public void save(calenderEntity savedEntity) {
        repository.save(savedEntity);
    }

    public List<?> getAllEvents() {
        return repository.findAll();
    }

    public List<?> findAllCalenderByuserId(Long userId) {
        return repository.findAllByuserId(userId);
    }
}
