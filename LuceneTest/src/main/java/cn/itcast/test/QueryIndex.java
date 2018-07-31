package cn.itcast.test;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.hankcs.lucene.HanLPAnalyzer;

public class QueryIndex {
	//查询索引库的全部数据
	@Test
	public void queryAllIndex() {
		//不传递参数 搜索所有的数据
		Query query=new MatchAllDocsQuery();
		try {
			doQuery(query);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//通过词条查询索引库的数据 根据最小单元词条查询
	@Test
	public void queryIndexByTerm() {
		Query query=new TermQuery(new Term("fileName","全文检索"));
		try {
			doQuery(query);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//搜索文件大小1--50字节搜索出来
	@Test
	public void queryIndexBySize() {
		Query query=LongPoint.newRangeQuery("fileSize",1l, 50l);
		try {
			doQuery(query);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//组合查询
	@Test
	public void queryIndexByBoolean() {
	
		try {
			Query query1= new TermQuery(new Term("fileName","不明觉厉"));
			Query query2= new TermQuery(new Term("fileName","传智播客"));
//			设置query1的查询限制
			//Occuer MUST 表示query必须满足条件 MUST_NOT 不能不满足 SHOULD 可以满足
			BooleanClause bc1=new BooleanClause(query1,Occur.MUST);
			BooleanClause bc2=new BooleanClause(query2,Occur.MUST_NOT);
			BooleanQuery query=new BooleanQuery.Builder().add(bc1).add(bc2).build();
			doQuery(query);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//通过字符串搜索匹配的数据 搜索传智播客优秀企业 域字段解析查询
	@Test
	public void queryIndexByField() {
	
		try {
			String seacherStr="传智播客全文检索的意义";
			//创建分词解析的对象
			QueryParser parser=new QueryParser("fileName",new HanLPAnalyzer());
			Query query=parser.parse(seacherStr);
			doQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//通过字符串搜索匹配的数据 搜索传智播客优秀企业  多域字段解析查询
		@Test
		public void queryIndexByMultiField() {
		
			try {
				String seacherStr="传智播客公司";
				String[] fields=new String [] {"fileName","fileContent"};
				MultiFieldQueryParser parser=new MultiFieldQueryParser(fields,new HanLPAnalyzer());
				Query query=parser.parse(seacherStr);
				doQuery(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	//通用的查询方法
	private void doQuery(Query query) throws IOException {
		//创建查询使用的对象IndexSearch
		//IndexReader 参数为读取索引库
		IndexReader indexReader=DirectoryReader.open(FSDirectory.open(Paths.get("D:\\hanlp\\luceneIndex")));
		IndexSearcher searcher = new IndexSearcher(indexReader);
		
		//参数1为执行的query对象，参数2为返回的结果集数量
		TopDocs topDocs = searcher.search(query, 100);
		System.out.println("总命中文档数量为:"+topDocs.totalHits);
//		返回每个文档的分值,影响文档显示数据的排序和id数值
		ScoreDoc [] scoDocs=topDocs.scoreDocs;
		for (ScoreDoc sd : scoDocs) {
			System.out.println("当前文档的id为："+sd.doc);
			System.out.println("当前文档的得分为"+sd.score);
			//通过文档id提取具体的文档数据
			Document doc = searcher.doc(sd.doc);
			System.out.println("文件的名称："+doc.get("fileName"));
			System.out.println("文件的路径："+doc.get("filePath"));
			System.out.println("文件的大小："+doc.get("fileSize"));
			
		}
	}
}
