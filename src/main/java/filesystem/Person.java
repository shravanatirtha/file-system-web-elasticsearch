package filesystem;

public class Person {

    private String personId;
    private String name;

    public Person(String personId, String name) {
		this.name = name;
		this.personId = personId;
		
	}
    public Person() {
    	
    }

	public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Person{personId='%s', name='%s'}", personId, name);
    }
}

//package filesystem;
////Steps to generate dll
//
////gcc -I"C:\Program Files\Java\jdk1.8.0_333\include" -I"C:\Program Files\Java\jdk1.8.0_333\include\win32" -shared -o FileShow.dll FileSystem.c
//
////other imports
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.ElasticsearchException;
//import org.elasticsearch.action.get.GetRequest;
//import org.elasticsearch.action.get.GetResponse;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
//* Servlet implementation class ShowServlet
//*/
//@WebServlet("/ShowServlet")
//public class ShowServlet extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//	public static String dir = "";
//	public static int offset = 0;
//	public static int count;
//	public static int prev;
//	// es connectivity variables
//
//	private static final String HOST = "localhost";
//	private static final int PORT_ONE = 9200;
//	private static final int PORT_TWO = 9201;
//	private static final String SCHEME = "http";
//
//	private static RestHighLevelClient restHighLevelClient;
//	private static ObjectMapper objectMapper = new ObjectMapper();
//
//	private static final String INDEX = "persondata";
//	private static final String TYPE = "person";
//
//	// insert into es
//	private static synchronized RestHighLevelClient makeConnection() {
//
//		if (restHighLevelClient == null) {
//			restHighLevelClient = new RestHighLevelClient(
//					RestClient.builder(new HttpHost(HOST, PORT_ONE, SCHEME), new HttpHost(HOST, PORT_TWO, SCHEME)));
//		}
//
//		return restHighLevelClient;
//	}
//
//	private static synchronized void closeConnection() throws IOException {
//		restHighLevelClient.close();
//		restHighLevelClient = null;
//	}
//
//	private static Person insertPerson(Person person) {
//		person.setPersonId(UUID.randomUUID().toString());
//		Map<String, Object> dataMap = new HashMap<String, Object>();
//		dataMap.put("personId", person.getPersonId());
//		dataMap.put("name", person.getName());
//		IndexRequest indexRequest = new IndexRequest(INDEX).source(dataMap);
//		try {
//			IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
//		} catch (ElasticsearchException e) {
//			e.getDetailedMessage();
//		} catch (java.io.IOException ex) {
//			ex.getLocalizedMessage();
//		}
//		return person;
//	}
//
//	private static void getPersonByName(String name) throws IOException {
//		
//		
//		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//		SearchRequest searchRequest = new SearchRequest(INDEX);
//		BoolQueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("name.keyword", name));
//		sourceBuilder.query(qb);
//		searchRequest.source(sourceBuilder);
//		SearchResponse searchResponse = restHighLevelClient.search(searchRequest,
//				RequestOptions.DEFAULT);
//		
//		ArrayList<Person> log = new ArrayList<Person>();
//		Map<String, Object> map = null;
//		SearchHit[] searchHit = searchResponse.getHits().getHits();
//		String t1 = "", t2 = "";
//		for (SearchHit hit : searchHit) {
//			map = hit.getSourceAsMap();
//			for (Map.Entry<String, Object> entry : map.entrySet()) {
//				if (entry.getKey() == "name") {
//					t1 = (String) entry.getValue();
//				} else if (entry.getKey() == "personId") {
//					t2 = (String) entry.getValue();
//				}
//			}
//			log.add(new Person(t1, t2));
//			if(log.size()>0) {
//				System.out.println("name => "+t1 +" Person Id => "+ t2);
//			}
//			
//		}
//		
//	}
//
//	// get method
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		PrintWriter out = response.getWriter();
//		// input
//		String path = request.getParameter("path");
//		dir = path;
//		int temp = Integer.parseInt(request.getParameter("temp"));
//		System.out.println(temp + " temp");
//		// if(temp<0) temp=0;
//		ShowServlet.offset += temp;
//		count = 0;
//
//		// if(count<10)
//		if (ShowServlet.offset < 0)
//			ShowServlet.offset = 0;
//		try {
//
//			makeConnection();
//			System.out.println(path);
//			System.out.println("Inserting a new Person with name Shravana...");
//			Person person = new Person();
//			person.setName("Shravana");
//			person = insertPerson(person);
//			System.out.println("Person inserted --> " + person);
//
//			System.out.println("Getting Shravana...");
//			getPersonByName("Shravana");
//			
////			System.out.println("Person from DB  --> " + personFromDB);
//     
//			closeConnection();
//
//		}
//		catch (NullPointerException e) {
//			System.out.println("Invalid Path");
//		} finally {
//
//		}
//	}
//
//}
