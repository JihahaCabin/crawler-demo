package com.example.crawlerjddemo.service.impl;

import com.example.crawlerjddemo.dao.ItemDao;
import com.example.crawlerjddemo.pojo.Item;
import com.example.crawlerjddemo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    @Override
    @Transactional
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    public List<Item> findAll(Item item) {
        //声明查询条件
        Example<Item> example = Example.of(item);

        //根据查询条件，查询数据
        List<Item> list = itemDao.findAll(example);
        return list;
    }
}
