package com.example.crawlerjddemo.dao;

import com.example.crawlerjddemo.pojo.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDao extends JpaRepository<Item,Long> {
}
