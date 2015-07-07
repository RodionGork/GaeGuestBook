package guestbook;

import java.io.*;
import javax.servlet.http.*;
import java.util.*;

import com.google.appengine.api.datastore.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiServlet extends HttpServlet {
    
    private DatastoreService dataService = DatastoreServiceFactory.getDatastoreService();
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Query query = new Query("Record");
        query.addSort("timestamp");
        List<Map<String, Object>> res = new ArrayList<>();
        for (Entity entity : dataService.prepare(query).asIterable()) {
            Map<String, Object> record = new HashMap<>();
            record.put("text", entity.getProperty("text"));
            record.put("key", entity.getKey().getId());
            res.add(record);
        }
        resp.setContentType("text/plain");
        resp.getWriter().println(mapper.writeValueAsString(Collections.singletonMap("records", res)));
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        InputStream in = req.getInputStream();
        String data = new Scanner(in).useDelimiter("\\00").next();
        in.close();
        
        Entity entity = new Entity("Record");
        entity.setUnindexedProperty("text", data);
        entity.setProperty("timestamp", System.currentTimeMillis());
        Key key = dataService.put(entity);
        
        resp.setContentType("application/json");
        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("key", key.getId());
        resp.getWriter().println(mapper.writeValueAsString(res));
    }
    
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean success = true;
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Key key = KeyFactory.createKey("Record", id);
            dataService.delete(key);
        } catch (Exception e) {
            success = false;
        }
        resp.setContentType("application/json");
        resp.getWriter().println(mapper.writeValueAsString(Collections.singletonMap("ok", success)));
    }
}

