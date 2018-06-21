package com.itheima;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.junit.Test;
import org.omg.IOP.ComponentIdHelper;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class solrjTest {


    /**
     * 清页码跟价格
     */
    @Test
    public void testAdd() throws IOException, SolrServerException {
        //创建一个连接对象 参数指定的连接服务器地址，不写默认连接connection1
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        //2.建立文档对象
        SolrInputDocument document = new SolrInputDocument();
        //3.向文档中添加域
        document.addField("id","c0001");
        document.addField("title_ik","使用solrj添加的文档");
        document.addField("product_name","商品名称");
        solrServer.add(document);
        solrServer.commit();
    }

    /**
     * 根据id删除文档
     */
    @Test
    public void deleteDocumentById()  {
        //创建连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        try {
            //根据id删除文档，提交（commit）
            solrServer.deleteById("c0001");
            solrServer.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据查询删除
     */
    @Test
    public void deleteByQuery(){
        //创建连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        try {
            //条件根据查询删除
            solrServer.deleteByQuery("*:*");
            solrServer.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    /**
     * 简单查询
     */
    @Test
    public void  queryIndex(){
         SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
         //创建query对象
        SolrQuery query = new SolrQuery();
        //创建条件查询
        query.setQuery("*:*");
        try {
            QueryResponse queryResponse = solrServer.query(query);
            SolrDocumentList solrDocumentList = queryResponse.getResults();
            System.out.println("共查询到商品数：" + solrDocumentList.getNumFound());

            for (SolrDocument solrDocument : solrDocumentList){
                System.out.println(solrDocument.get("id"));
                System.out.println(solrDocument.get("product_name"));
                System.out.println(solrDocument.get("product_price"));
                System.out.println(solrDocument.get("product_catalog_name"));
                System.out.println(solrDocument.get("product_picture"));

            }

        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复杂查询
     */
    @Test
    public void complexQuery(){
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        SolrQuery solrQuery = new SolrQuery();
        //查询条件
        solrQuery.setQuery("钻石");
        //过滤条件
        solrQuery.setFilterQueries("product_catalog_name:幽默杂货");
        //排序条件
        solrQuery.setSort("product_price",SolrQuery.ORDER.asc);
        //分页处理
        solrQuery.setStart(0);
        solrQuery.setRows(10);
        //结果域中的列表
        solrQuery.setFields("id","product_name","product_price","product_catalog","product_picture");
        //设置默认搜索域
        solrQuery.set("df","product_keywords");
        //高亮显示
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre("<em>");
        solrQuery.setHighlightSimplePost("</em>");

        //执行查询
        try {
            QueryResponse queryResponse = solrServer.query(solrQuery);
            SolrDocumentList results = queryResponse.getResults();
            System.out.println("查询到的商品数量：" + results.getNumFound());
            for (SolrDocument result : results) {
                System.out.println(result.get("id"));
                //高亮显示
                String productName = "";
                Map<String, Map<String,List<String>>> highlight = queryResponse.getHighlighting();
                List<String> list = highlight.get(result.get("id")).get("product_name");
                if(null != list){
                    productName = list.get(0);
                } else {
                    productName = (String) result.get("product_name");
                }
                System.out.println(productName);
                System.out.println(result.get("product_price"));
                System.out.println(result.get("product_catalog_name"));
                System.out.println(result.get("product_picture"));

            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

    }
}
