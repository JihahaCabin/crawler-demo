package com.example.crawlerjddemo.task;

import com.example.crawlerjddemo.pojo.Item;
import com.example.crawlerjddemo.service.ItemService;
import com.example.crawlerjddemo.utils.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ItemTask {

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private ItemService itemService;

    private static final ObjectMapper mapper = new ObjectMapper();

    /*    //当下载任务完成后，间隔一定时间 100s，进行下一次任务
        @Scheduled(fixedDelay = 100*1000)*/
    public void itemTask() throws Exception {
        //声明需要解析的初始地址
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&s=116&click=0&page=";
        //按照页码，对手机的搜索结果进行遍历解析
        for (int i = 1; i < 10; i += 2) {
            String html = httpUtils.doGetHtml(url + i);
            //解析页面，获取商品数据并存储
            this.parse(html);
        }

        System.out.println("手机页面抓取成功");
    }

    //解析页面，获取商品数据并存储
    private void parse(String html) throws JsonProcessingException {

        //解析html,获取Document
        Document document = Jsoup.parse(html);
        //获取spu信息
        Elements spuEles = document.select("div#J_goodsList > ul > li");

        for (Element spuEle : spuEles) {
            //获取spu
            if (spuEle.attr("data-spu").equals("")) {
                continue;
            }
            long spu = Long.parseLong(spuEle.attr("data-spu"));

            //获取sku
            Elements skuEles = spuEle.select("li.ps-item");
            for (Element skuEle : skuEles) {
                //获取sku
                long sku = Long.parseLong(skuEle.select("[data-sku]").attr("data-sku"));

                //根据sku,查询商品数据
                Item item = new Item();
                item.setSku(sku);
                List<Item> list = itemService.findAll(item);
                //如果商品存在，则进行下一个循环
                if (list.size() > 0) {
                    continue;
                }

                item.setSpu(spu);

                //获取商品详情url
                String itemUrl = "https://item.jd.com/" + sku + ".html";
                item.setUrl(itemUrl);

                String picUrl = "https:" + skuEle.select("img[data-sku]").first().attr("data-lazy-img");
                picUrl = picUrl.replace("/n7/", "/n1/");
                String picName = this.httpUtils.doGetImage(picUrl);
                item.setPic(picName);

                //获取商品价格
                String priceJson = this.httpUtils.doGetHtml("https://p.3.cn/prices/mgets?skuIds=J_" + sku);
                double price = mapper.readTree(priceJson).get(0).get("p").asDouble();
                item.setPrice(price);


                String itemInfo = this.httpUtils.doGetHtml(item.getUrl());
                Document itemDoc = Jsoup.parse(itemInfo);
                String title = itemDoc.select("div.sku-name").text();
                item.setTitle(title);

                item.setCreated(new Date());
                item.setUpdated(item.getCreated());

                //保存数据到数据库
                this.itemService.save(item);

            }
        }

    }
}
