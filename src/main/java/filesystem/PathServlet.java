package filesystem;

//Steps to generate dll
//gcc -I"C:\Program Files\Java\jdk1.8.0_333\include" -I"C:\Program Files\Java\jdk1.8.0_333\include\win32" -shared -o FileShow.dll FileSystem.c

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import filesystem.Directory;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.core.TimeValue;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;

@WebServlet("/PathServlet")
public class PathServlet extends HttpServlet {
    static {
        System.loadLibrary("FileSys");
    }

    public native FileInfo fileSystem(String path);

    private native String[] filePath(String path, int dir_count);

    private static final long serialVersionUID = 1L;

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
        PathServlet fs = new PathServlet();
        // input parameter
        String path = request.getParameter("path");
        String filepath = path.toLowerCase();
        System.out.println(filepath);// console
        // native method call
        FileInfo fi = fs.fileSystem(filepath);
        int n = fi.getDirectory_count();
        String[] paths = fs.filePath(fi.getcFileName(), n);
        List list = new ArrayList<>();
       // Directory directory;
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
               

                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("parent_path", parent_path);
                dataMap.put("directory_name", dir_name);
                dataMap.put("subdirectories", fi.getDirectory_count());
                dataMap.put("files", fi.getFile_count());
                dataMap.put("size_bytes", size);
                dataMap.put("size", sizeInBytes);
                dataMap.put("sync_time", new java.util.Date().getTime());
                dataMap.put("version", 1);
                list.add(dataMap);
//                IndexRequest indexRequest = new IndexRequest(INDEX).source(dataMap);
//
//                IndexResponse res = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
//                System.out.println("Directory inserted successfully");
                
                
            }
            BulkRequest bulkRequest = new BulkRequest();
            list.forEach(directory -> {
                IndexRequest indexRequest = new IndexRequest(INDEX,TYPE).
                        source(objectMapper.convertValue(directory, Map.class));

                bulkRequest.add(indexRequest);
            });
//                restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            BulkProcessor.Listener listener = new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId, BulkRequest request) {
                    int numberOfActions = request.numberOfActions(); 
                    logger.debug("Executing bulk [{}] with {} requests",
//                            executionId, numberOfActions);
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request,
                        BulkResponse response) {
                    if (response.hasFailures()) { 
                        logger.warn("Bulk [{}] executed with failures", executionId);
                    } else {
                        logger.debug("Bulk [{}] completed in {} milliseconds",
                                executionId, response.getTook().getMillis());
                    }
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request,
                        Throwable failure) {
                    logger.error("Failed to execute bulk", failure); 
                }
            };
            BulkProcessor.Builder builder = BulkProcessor.builder(
                    (indexRequest, bulkListener) ->
                    restHighLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, bulkListener),listener);
            builder.setBulkActions(500); 
            builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB)); 
            builder.setConcurrentRequests(0); 
            builder.setFlushInterval(TimeValue.timeValueSeconds(10L)); 
            builder.setBackoffPolicy(BackoffPolicy
                    .constantBackoff(TimeValue.timeValueSeconds(1L), 3)); 
                System.out.println("Directory inserted successfully");
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
    

}
