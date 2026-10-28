package com.dong.service;

import com.dong.entity.ClientBusiness;

import java.util.List;

public interface ClientBusinessService {

    List<ClientBusiness> findAll();
    List<ClientBusiness> findByUserId(Integer userId);
}