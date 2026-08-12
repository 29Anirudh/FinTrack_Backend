package com.financialtracker.backend.Models.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financialtracker.backend.Models.POJO.Notifications;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Integer> {

}
