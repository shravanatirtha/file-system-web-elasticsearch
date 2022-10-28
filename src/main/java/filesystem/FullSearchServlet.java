package filesystem;

//Steps to generate dll
//gcc -I"C:\Program Files\Java\jdk1.8.0_333\include" -I"C:\Program Files\Java\jdk1.8.0_333\include\win32" -shared -o FileShow.dll FileSystem.c

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/FullSearchServlet")
public class FullSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static int offset = 0;
	public static int count;
	public static int prev;

	// es connectivity variables
	private static final String HOST = "localhost";
	private static final int PORT_ONE = 9200;
	private static final int PORT_TWO = 9201;
	private static final String SCHEME = "http";
	private static RestHighLevelClient restHighLevelClient;
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static final String INDEX = "directory";
	private static final String TYPE = "info";

	// establish connection
	private static synchronized RestHighLevelClient makeConnection() {

		if (restHighLevelClient == null) {
			restHighLevelClient = new RestHighLevelClient(
					RestClient.builder(new HttpHost(HOST, PORT_ONE, SCHEME), new HttpHost(HOST, PORT_TWO, SCHEME)));
		}

		return restHighLevelClient;
	}

	// close connection
	private static synchronized void closeConnection() throws IOException {
		restHighLevelClient.close();
		restHighLevelClient = null;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		int offset = 0;
		String path = request.getParameter("path");
		String dir = ShowServlet.dir;
		System.out.println(path);
		int temp = 10; // Integer.parseInt(request.getParameter("temp"));
		System.out.println(temp + " temp");
		FullSearchServlet.offset += temp;
		count = 0;
		if (FullSearchServlet.offset < 0)
			FullSearchServlet.offset = 0;
		try {

			makeConnection();
			String path1 = "";
			String path2 = "";
			String val = "";
			// print on web
			out.println("<html>");
			out.println("<head><title>File Explorer</title>");
			out.println(
					"<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css' integrity='sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm' crossorigin='anonymous' />");
			out.println("</head>");
			out.println("<body>");
			out.println("<div align='center'>");
			out.println("<h1>File Explorer</h1>");
			out.println("<hr />");
			out.println("</div>");
			// navigate feature
			out.println(" <form  action='ShowServlet' method='get'>");
			// home
			String parameter = URLEncoder.encode(path, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
			out.println("<a href='index.jsp' class='btn btn-outline-primary btn-md' >Home</a>");
			out.println(
					"<button onclick='javascript:history.back()' class='btn btn-outline-primary btn-md' ><-</button>");
			out.println(
					"<button onclick='javascript:history.forward()' class='btn btn-outline-primary btn-md' >-></button>");

			out.println("<a href='ShowServlet?path=" + parameter + "&temp=0' >" + path + "</a");
			out.println("</form>");
			out.println(" <form  action='FullSearchServlet' method='get'>");
			out.println("<div align='center'>");
			out.println("<input  type='text' autocomplete='off' name='path' required placeholder='Enter path'/>");
			out.println("<input type='submit' value='Search' />");
			out.println("</div>");
			out.println("</form>");
			out.println(" <form  action='SearchServlet' method='get'>");
			out.println("<div style='float:right'>");
			out.println(
					"<input  type='text' autocomplete='off' name='path' required placeholder='Enter directory name'/>");
			out.println("<input type='submit' value='Search' />");
			out.println("</div>");
			out.println("</form>");
			out.println("<hr />");
			// table utility
			out.println("\n\n");

			out.println(
					"<table id='example' style='border: 1px solid black;border-collapse: collapse;width: 50%>;width: 100%; border-collapse: collapse; text-align: left;'");
			// overflow-x:auto;padding: 6px;
			out.println(" <tr>");
			out.println("<th>Parent path</th>");
			out.println(" <form action='SortServlet' method='get'>");
			out.println("<th>Directory name");
			out.println(" <select name='dname' id='dname' onchange='this.form.submit()'>");
			out.println("    <option value='asc'>v</option>");
			out.println("    <option value='asc'>A-Z</option>");
			out.println("   <option value='desc'>Z-A</option>");
			out.println(" </select>");
			out.println("</th>");
			out.println("</form>");
			out.println("<th>Subdirectories</th>");
			out.println("<th>Files</th>");
			out.println(" <th>Size in bytes</th>");
			out.println(" <th>Total size</th>");
			out.println("<th>Last sync time</th>");
			out.println(" <th>Version</th>");
			out.println(" </tr>");
			out.println(" <form action='ShowServlet' method='get'>");

			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			SearchRequest searchRequest = new SearchRequest(INDEX);
			BoolQueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("parent_path.keyword", path));
			sourceBuilder.query(qb);
			searchRequest.source(sourceBuilder);
			SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			ArrayList<Directory> log = new ArrayList<Directory>();
			Map<String, Object> map = null;
			SearchHit[] searchHit = searchResponse.getHits().getHits();
			String parent_path = "", directory_name = "", size_bytes = "", sync_time = "";
			int subdirectories = 0, files = 0, size = 0, version = 0;
			for (SearchHit hit : searchHit) {
				map = hit.getSourceAsMap();
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getKey() == "parent_path") {
						parent_path = (String) entry.getValue();
					} else if (entry.getKey() == "directory_name") {
						directory_name = (String) entry.getValue();
					} else if (entry.getKey() == "subdirectories") {
						subdirectories = (int) entry.getValue();
					} else if (entry.getKey() == "files") {
						files = (int) entry.getValue();
					} else if (entry.getKey() == "size_bytes") {
						size_bytes = (String) entry.getValue();
					} else if (entry.getKey() == "size") {
						size = (int) entry.getValue();
					} else if (entry.getKey() == "sync_time") {
						sync_time = (String) entry.getValue();
					} else if (entry.getKey() == "version") {
						version = (int) entry.getValue();
					}
				}
				log.add(new Directory(parent_path, directory_name, subdirectories, files, size_bytes, size, sync_time,
						version));
				if (log.size() > 0) {
					count++;
					path1 = parent_path;
					ShowServlet.dir = parent_path;
					path2 = directory_name;
					val = path1 + "\\" + path2;
					String result = URLEncoder.encode(val, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!")
							.replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")")
							.replaceAll("\\%7E", "~");
					String param1 = URLEncoder.encode(path1, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!")
							.replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")")
							.replaceAll("\\%7E", "~");
					String param2 = URLEncoder.encode(path2, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!")
							.replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")")
							.replaceAll("\\%7E", "~");
					out.println(" <tr>");
					out.println(" <td> " + parent_path + "</a></td>");
					out.println(
							" <td><a href='ShowServlet?path=" + result + "&temp=0' >" + directory_name + "</a></td>");
					out.println(" <td>" + subdirectories + "</td>");
					out.println(" <td>" + files + "</td>");
					out.println(" <td>" + size_bytes + "</td>");
					out.println(" <td>" + size + "</td>");
					out.println(" <td>" + sync_time + "</td>");
					out.println("</form>");
					out.println(" <form action='VersionServlet' method='get'>");
					out.println(" <td><a href=" + "VersionServlet?path1=" + param1 + "&path2=" + param2 + ">" + version
							+ "</td>");
					out.println("</form>");
					out.println(" </tr>");
				}

			}
			System.out.println(count + " count");
			if (count == 0) {
				count = prev * -1;
			}
			prev = count;
			// if(count<10) ShowServlet.offset=0;
			out.println("</table>");
			out.println("<div style='float:right;'>");
			System.out.println(ShowServlet.offset + " before");

			System.out.println(ShowServlet.offset + " after");
			out.println("<form action='ShowServlet' method='get'>");
			out.println(
					"<a href='ShowServlet?path=" + path1 + "&temp=-10' class='btn btn-outline-primary btn-md' ><-</a>");
			out.println(
					"<a href='ShowServlet?path=" + path1 + "&temp=10' class='btn btn-outline-primary btn-md' >-></a>");
			out.println("</form>");
			out.println("</div>");

			out.println(
					"<script src='https://code.jquery.com/jquery-3.2.1.slim.min.js' integrity='sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN' crossorigin='></script>");
			out.println(
					"<script src='https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js' integrity='sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q' crossorigin='></script>");
			out.println(
					"<script src='https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js integrity='sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl' crossorigin='></script>");
			// out.println("<script>function goBack(){window.history.go(-1)}function
			// goForward(){window.history.go(1)}</script>");
			out.println("</body>");
			out.println("</html>");

			closeConnection();

		} catch (NullPointerException e) {
			System.out.println("Invalid Path");
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
		}
	}

}