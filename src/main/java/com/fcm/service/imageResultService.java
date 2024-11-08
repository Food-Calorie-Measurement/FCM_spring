package com.fcm.service;

import com.fcm.repository.imageResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class imageResultService {

    @Autowired
    private imageResultRepository repository;
}
