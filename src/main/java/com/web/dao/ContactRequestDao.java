package com.web.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.entity.ContactRequestsEntity;

@Repository
public interface ContactRequestDao extends JpaRepository<ContactRequestsEntity, Long> {

}
