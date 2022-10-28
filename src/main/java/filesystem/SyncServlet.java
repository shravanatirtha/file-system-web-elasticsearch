package filesystem;
//Steps to generate dll

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

@WebServlet("/SyncServlet")
public class SyncServlet extends HttpServlet {
	static {
		System.loadLibrary("FileSys");
	}

	public native FileInfo fileSystem(String path);

	private native String[] filePath(String path, int dir_count);

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
		SyncServlet fs = new SyncServlet();
		// input parameter
		String path = request.getParameter("path");
		String filepath = path.toLowerCase();
		System.out.println(filepath);// console
		// native method call
		FileInfo fi = fs.fileSystem(filepath);
		int n = fi.getDirectory_count();
		String[] paths = fs.filePath(fi.getcFileName(), n);
		try {
			makeConnection();
			for (int i = 0; i < n; i++) {
				fi = fs.fileSystem(paths[i].toLowerCase());
				path = paths[i];
				int index = path.lastIndexOf("\\");
				String dir_name = path.substring(index + 1);
				String parent_path = path.substring(0, index);
				String size = "";
				int sizeInBytes = fi.getnFileSizeLow();

				int v = 1;

				if (sizeInBytes < 1024) {
					size = Integer.toString(sizeInBytes) + " Bytes";
				} else if (sizeInBytes >= 1024 && sizeInBytes < 1048576) {
					size = Integer.toString(sizeInBytes / 1024) + " KB";
				} else if (sizeInBytes >= 1048576 && sizeInBytes < 1073741824) {
					size = Integer.toString(sizeInBytes / 1048576) + " MB";
				} else if (sizeInBytes >= 1073741824) {
					size = Integer.toString(sizeInBytes / 1073741824) + " GB";
				}
				// System.out.println("File Size: " + size);

				SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
				SearchRequest searchRequest = new SearchRequest(INDEX);
				BoolQueryBuilder qb = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("parent_path.keyword", parent_path))
						.must(QueryBuilders.matchQuery("directory_name.keyword", dir_name));
				sourceBuilder.query(qb);
				searchRequest.source(sourceBuilder);
				SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
				ArrayList<Directory> log = new ArrayList<Directory>();
				Map<String, Object> map = null;
				SearchHit[] searchHit = searchResponse.getHits().getHits();
				String directory_name = "", size_bytes = "", sync_time = "";
				int subdirectories = 0, files = 0, version = 0;
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
							sizeInBytes = (int) entry.getValue();
						} else if (entry.getKey() == "sync_time") {
							sync_time = (String) entry.getValue();
						} else if (entry.getKey() == "version") {
							version = (int) entry.getValue();
						}
					}
					log.add(new Directory(parent_path, directory_name, subdirectories, files, size_bytes, sizeInBytes,
							sync_time, version));
					if (log.size() > 0) {
						String sizeByte = size_bytes;
						int sizenum = sizeInBytes;

						if (paths[i].equals(parent_path + "\\" + directory_name)) {

							System.out.println(subdirectories + " " + fi.getDirectory_count());
							System.out.println(files + " " + fi.getFile_count());
							System.out.println(sizeByte + " " + size);
							System.out.println(sizenum + " " + sizeInBytes);
							if ((fi.getDirectory_count() != subdirectories) || (fi.getFile_count() != files)
									|| (sizeByte.equals(size) == false) || (sizenum != sizeInBytes)) {

								version = getVersion(parent_path, dir_name, fi.getDirectory_count(), fi.getFile_count(),
										size, sizeInBytes);
								v = version;
								insert(parent_path, dir_name, fi, fi.getDirectory_count(), fi.getFile_count(),
										sizeInBytes, size, version + 1);
							}
						} else {
							insert(parent_path, dir_name, fi, fi.getDirectory_count(), fi.getFile_count(), sizeInBytes,
									size, v);
						}

					} else {

						insert(parent_path, dir_name, fi, fi.getDirectory_count(), fi.getFile_count(), sizeInBytes,
								size, v);
					}
				}
			}
			closeConnection();

		} catch (NullPointerException e) {
			System.out.println("Invalid Path");
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
		}
		response.sendRedirect("index.jsp");
	}

	public static void insert(String path, String name, FileInfo fi, int subdirectories, int files, int sizeInBytes,
			String size, int version) {

		try {
			makeConnection();

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("parent_path", path);
			dataMap.put("directory_name", name);
			dataMap.put("subdirectories", Integer.toString(subdirectories));
			dataMap.put("files", Integer.toString(files));
			dataMap.put("size_bytes", size);
			dataMap.put("size", Integer.toString(sizeInBytes));
			dataMap.put("sync_time", new java.util.Date().getTime());
			dataMap.put("version", Integer.toString(version));
			IndexRequest indexRequest = new IndexRequest(INDEX).source(dataMap);

			IndexResponse res = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
			System.out.println("Directory inserted successfully");
			// con.close();
		} catch (NullPointerException e) {
			System.out.println("Invalid Path");
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
		}

	}

	public static int getVersion(String path, String dir_name, int subdirectories, int files, String sizeByte,
			int size) {
		int version = 0;
		try {
			makeConnection();
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			SearchRequest searchRequest = new SearchRequest(INDEX);
			BoolQueryBuilder qb = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("parent_path.keyword", path))
					.must(QueryBuilders.matchQuery("directory_name.keyword", dir_name));
			sourceBuilder.query(qb);
			searchRequest.source(sourceBuilder);
			SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			ArrayList<Directory> log = new ArrayList<Directory>();
			Map<String, Object> map = null;
			SearchHit[] searchHit = searchResponse.getHits().getHits();
			String directory_name = "", size_bytes = "", sync_time = "";
			int sub=0, fi=0, si=0, v=0;
			for (SearchHit hit : searchHit) {
				map = hit.getSourceAsMap();
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getKey() == "path") {
						path = (String) entry.getValue();
					} else if (entry.getKey() == "directory_name") {
						directory_name = (String) entry.getValue();
					} else if (entry.getKey() == "subdirectories") {
						sub = (int) entry.getValue();
					} else if (entry.getKey() == "files") {
						files = (int) entry.getValue();
					} else if (entry.getKey() == "size_bytes") {
						size_bytes = (String) entry.getValue();
					} else if (entry.getKey() == "size") {
						si = (int) entry.getValue();
					} else if (entry.getKey() == "sync_time") {
						sync_time = (String) entry.getValue();
					} else if (entry.getKey() == "version") {
						v = (int) entry.getValue();
					}
				}
				log.add(new Directory(path, directory_name, subdirectories, files, size_bytes, si, sync_time,
						version));
			if (log.size() > 0) {
			
				if (sub == subdirectories && fi == files && sizeByte.equals(size_bytes)
						&& si == size)
					version = 0;
				else
					version = v;

			}
		}
			// con.close();
		} catch (NullPointerException e) {
			System.out.println("Invalid Path");
		} catch (ElasticsearchException e) {
			e.getDetailedMessage();
		} catch (java.io.IOException ex) {
			ex.getLocalizedMessage();
		}
		return version;
	}

}
