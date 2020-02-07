package org.activiti.app.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SSO身份认证
 * @author wangjia
 * @date 2019-7-2
 *
 */
@Service
public class SsoService {
	
	@Autowired
	Environment environment;
    
	/**
	 * 获取SSO REST 服务地址
	 * @return
	 */
    private  String getSSO_URL() {
    	return MessageFormat.format("{0}:{1}/{2}/", environment.getRequiredProperty("SSO.rest.host"),
    			environment.getRequiredProperty("SSO.rest.port"),
    			environment.getRequiredProperty("SSO.rest.restroot"));
    }
    /**
     * 拼接url
     * @param url
     * @return
     */
	private  String getUrl(String url) {
		return getSSO_URL() + url;
	}
	/**
	 * 拼接url，带参数
	 * @param url 
	 * @param args 参数
	 * @return
	 */
	private  String getUrl(String url, Object... args) {
		return getSSO_URL() + MessageFormat.format(url, args);
	}
	
	/**
	 * 获取身份认证的token
	 * @return
	 */
	public  String getToken()  {
		 Map<String,String> personMap = new HashMap<String,String>();
	        personMap.put("account", environment.getRequiredProperty("SSO.rest.user"));
	        personMap.put("password", environment.getRequiredProperty("SSO.rest.password"));
	        List<NameValuePair> list = new LinkedList<NameValuePair>();
	        for(Entry<String,String> entry:personMap.entrySet()){
	            list.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
	        }
	        HttpPost httpPost = new HttpPost(getUrl(environment.getRequiredProperty("SSO.rest.url.login")));
	        UrlEncodedFormEntity formEntity;
			
	        try{
	        	formEntity = new UrlEncodedFormEntity(list,"utf-8");
		        httpPost.setEntity(formEntity);
		        HttpClient httpClient = HttpClients.createDefault();
		        HttpResponse httpresponse = null;
		        httpresponse = httpClient.execute(httpPost);
		        HttpEntity httpEntity = httpresponse.getEntity();
		        String response = EntityUtils.toString(httpEntity, "utf-8");
		        
		        ObjectMapper objectMapper = new ObjectMapper();
		        JsonNode jsonNode = objectMapper.readTree(response).findPath("Data").get("Accesstoken");
		        
		        String token = jsonNode.traverse(objectMapper).readValueAs(String.class);
		        return token;
	        }catch (UnsupportedEncodingException e1) {
	        	System.out.println("UnsupportedEncodingException��uri{},exception{}");
			}catch(ClientProtocolException e){
	                System.out.println(e.getMessage());
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
	       return null;
	}
	
	/**
	 * GET方法获取REST
	 * @param url
	 * @param params
	 * @return
	 */
	public JsonNode getRequest(String url, JSONObject params)  {
		//String token = getToken();
		
		if(params!=null) {
			url = url + "?";
			for (String key : params.keySet()) {
				try {
					url += key + "=" + URLEncoder.encode(params.getString(key), "UTF-8") +"&";
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
        HttpGet httpGet = new HttpGet(url);
		
        try{
	        //httpGet.setHeader("Authorization", token);
	        HttpClient httpClient = HttpClients.createDefault();
	        HttpResponse httpresponse = null;
	        httpresponse = httpClient.execute(httpGet);
	        HttpEntity httpEntity = httpresponse.getEntity();
	        String response = EntityUtils.toString(httpEntity, "utf-8");
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(response);//.findPath("Data");
	        return jsonNode;
        }catch (UnsupportedEncodingException e1) {
        	System.out.println("UnsupportedEncodingException��uri{},exception{}");
		}catch(ClientProtocolException e){
                System.out.println(e.getMessage());
       }catch(IOException e){
           System.out.println(e.getMessage());
       }
       return null;
	}
	
	/**
	 * POST方法获取REST
	 * @param url
	 * @param params
	 * @return
	 */
	public  JsonNode postRequest(String url, JSONObject params)  {
		//String token = getToken();
		//Map<String,String> personMap = new HashMap<String,String>();
        //personMap.put("Authorization",token);
        //for(Entry<String,String> entry:personMap.entrySet()){
        //	params.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        //}
        HttpPost httpPost = new HttpPost(url);
        //UrlEncodedFormEntity formEntity;
		
        try{
        	StringEntity postingString = new StringEntity(params.toString());// json传递
        	//formEntity = new UrlEncodedFormEntity(params,"utf-8");
        	//formEntity.setContentType("application/json");
	        httpPost.setEntity(postingString);
	        HttpClient httpClient = HttpClients.createDefault();
	        HttpResponse httpresponse = null;
	        httpresponse = httpClient.execute(httpPost);
	        if (httpresponse.getStatusLine().getStatusCode() == 200) {
	        	HttpEntity httpEntity = httpresponse.getEntity();
		        String response = EntityUtils.toString(httpEntity, "utf-8");
		        ObjectMapper objectMapper = new ObjectMapper();
		        JsonNode jsonNode = objectMapper.readTree(response);
		        return jsonNode;
	        }
	        
        }catch (UnsupportedEncodingException e1) {
        	System.out.println("UnsupportedEncodingException��uri{},exception{}");
		}catch(ClientProtocolException e){
                System.out.println(e.getMessage());
       }catch(IOException e){
           System.out.println(e.getMessage());
       }
       return null;
	}
	
	
	public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page){
		JSONObject params = new JSONObject();
		params.put("query", CastUserQuery(query));
		if(page == null) {
			page = new Page(0,0);
		}
		params.put("page", JSONObject.toJSONString(page));
		JsonNode result = postRequest(environment.getRequiredProperty("SSO.rest.url.findUserByQueryCriteria"),params);
		JsonNode results = result.get("Entity");
		if(results.size()>0) {
			List<User> ulist = new ArrayList<User>();
			for (Iterator<JsonNode> iter = results.elements(); iter.hasNext();) {
				JsonNode r = iter.next();
				User u = parseUser(r);
				ulist.add(u);
			 }
			return ulist;
		}
		return null;
	}
	
	public int findUserCountByQueryCriteria(UserQueryImpl query){
		JSONObject params = new JSONObject();
		params.put("query", CastUserQuery(query));
		JsonNode result = postRequest(environment.getRequiredProperty("SSO.rest.url.findUserCountByQueryCriteria"),params);
		return result.get("Entity").asInt(); 
	}
	
	public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page){
		JSONObject params = new JSONObject();
		params.put("query", CastGroupQuery(query));
		if(page == null) {
			page = new Page(0,0);
		}
		params.put("page", JSONObject.toJSONString(page));
		JsonNode result = postRequest(environment.getRequiredProperty("SSO.rest.url.findGroupByQueryCriteria"),params);
		JsonNode results = result.get("Entity");
		if(results.size()>0) {
			List<Group> ulist = new ArrayList<Group>();
			for (Iterator<JsonNode> iter = results.elements(); iter.hasNext();) {
				JsonNode r = iter.next();
				Group u = parseGroup(r);
				ulist.add(u);
			 }
			return ulist;
		}
		return null;
	}
	
	public int findGroupCountByQueryCriteria(GroupQueryImpl query){
		JSONObject params = new JSONObject();
		params.put("query",  CastGroupQuery(query));
		JsonNode result = postRequest(environment.getRequiredProperty("SSO.rest.url.findGroupCountByQueryCriteria"),params);
		return result.get("Entity").asInt(); 
	}
	
	public List<Group> findGroupsByUser(String userId){
		JSONObject params = new JSONObject();
		params.put("userId", userId);
		JsonNode result = getRequest(environment.getRequiredProperty("SSO.rest.url.findGroupsByUser"),params);
		JsonNode results = result.get("Entity");
		if(results.size()>0) {
			List<Group> ulist = new ArrayList<Group>();
			for (Iterator<JsonNode> iter = results.elements(); iter.hasNext();) {
				JsonNode r = iter.next();
				Group u = parseGroup(r);
				ulist.add(u);
			 }
			return ulist;
		}
		return null;
	}
	
	private String CastUserQuery(UserQueryImpl query) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", query.getId() == null ? "" : query.getId());
		jsonObject.put("firstName", query.getFirstName()== null ? "" : query.getFirstName());
		jsonObject.put("firstNameLike", query.getFirstNameLike()== null ? "" : query.getFirstNameLike());
		jsonObject.put("lastName", query.getLastName()== null ? "" : query.getLastName());
		try {
			jsonObject.put("lastNameLike", query.getLastNameLike()== null ? "" : URLEncoder.encode(query.getLastNameLike(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonObject.put("email", query.getEmail()== null ? "" : query.getEmail());
		jsonObject.put("emailLike", query.getEmailLike()== null ? "" : query.getEmailLike());
		jsonObject.put("groupId", query.getGroupId()== null ? "" : query.getGroupId());
		jsonObject.put("procDefId", query.getProcDefId()== null ? "" : query.getProcDefId());
		return jsonObject.toJSONString();
	}
	
	private String CastGroupQuery(GroupQueryImpl query) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", query.getId()== null ? "" : query.getId());
		jsonObject.put("name", query.getName()== null ? "" : query.getName());
		jsonObject.put("nameLike", query.getNameLike()== null ? "" : query.getNameLike());
		jsonObject.put("type", query.getType()== null ? "" : query.getType());
		jsonObject.put("userId", query.getUserId()== null ? "" : query.getUserId());
		jsonObject.put("procDefId", query.getProcDefId()== null ? "" : query.getProcDefId());
		return jsonObject.toJSONString();
	}
	
	private User parseUser(JsonNode result) {
		String password = result.get("password").asText();
		String lastName = result.get("lastName").asText();//姓名
		String email = result.get("email").asText().equals("null") ? (lastName + "@email"):result.get("email").asText();
		String firstName = result.get("firstName").asText();//用户名 
		String userID= result.get("id").asText();
		User u = new com.pkpm.bpm.identity.User();
		u.setId(userID);
		u.setEmail(email);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setPassword(password);
		return u;
	}
	
	private Group parseGroup(JsonNode result) {
		String id = result.get("id").asText();
		String name = result.get("name").asText();
		String type = result.get("type").asText();
		Group u = new com.pkpm.bpm.identity.SSOGroup();
		u.setId(id);
		u.setName(name);
		u.setType(type);
		return u;
	}
	
	
}
