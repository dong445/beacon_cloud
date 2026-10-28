package com.dong.service.impl;

import com.dong.entity.ClientBusiness;
import com.dong.mapper.ClientBusinessMapper;
import com.dong.service.ClientBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientBusinessServiceImpl implements ClientBusinessService {

    @Autowired
    private ClientBusinessMapper clientBusinessMapper;

    @Override
    public List<ClientBusiness> findAll() {
        return clientBusinessMapper.findAll();
    }

    @Override
    public List<ClientBusiness> findByUserId(Integer userId) {
        return clientBusinessMapper.findByUserId(String.valueOf(userId));
    }
}