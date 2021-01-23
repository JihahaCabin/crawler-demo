package com.example.crawlerwebmagic.test;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class JobProcessor implements PageProcessor {

    /**
     * 负责解析页面
     *
     * @param page
     */
    @Override
    public void process(Page page) {
        //解析返回的数据page,并且把解析的结果放到ResultItem中
        page.putField("div", page.getHtml().css("div.cw-icon a").all());

    }


    private Site site = Site.me();

    /**
     * @return
     */
    @Override
    public Site getSite() {
        return site;
    }


    //主函数执行爬虫
    public static void main(String[] args) {
        Spider.create(new JobProcessor())
                .addUrl("https://kuaibao.jd.com/") //设置要爬取的页面
                .run();
    }
}
