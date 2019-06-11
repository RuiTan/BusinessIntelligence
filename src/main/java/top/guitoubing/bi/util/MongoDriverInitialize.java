package top.guitoubing.bi.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import top.guitoubing.bi.entity.NodeEntity;
import top.guitoubing.bi.service.GraphService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MongoDriverInitialize {
    private static MongoClient mongoClient;

    private static MongoClient getMongoClient() {
        if (mongoClient == null){
            MongoCredential credential =  MongoCredential.createCredential(ConstantDefinition.mongoUser, ConstantDefinition.mongoDatabase, ConstantDefinition.mongoPassword.toCharArray());
            mongoClient = new MongoClient(new ServerAddress(ConstantDefinition.mongoIp, ConstantDefinition.mongoPort), credential, MongoClientOptions.builder().sslEnabled(false).build());
        }
        return mongoClient;
    }

    private static MongoCollection<Document> getMongoCollection(){
        return getMongoClient().getDatabase(ConstantDefinition.mongoDatabase).getCollection(ConstantDefinition.mongoCollection);
    }

    public static void addOne(String param, HashMap<String, ArrayList<NodeEntity>> hashMap){
        MongoCollection<Document> collection = getMongoCollection();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(hashMap);
        Document document = new Document("id", param).append("result", jsonObject.toJSONString());
        collection.insertOne(document);
    }

    public static String findOne(String param){
        System.err.println("从MongoDB中查");
        MongoCollection<Document> collection = getMongoCollection();
        MongoCursor<Document> cursor = collection.find(Filters.eq("id", param)).iterator();
        if (cursor.hasNext()){
            JSONObject jsonObject = JSONObject.parseObject(cursor.next().get("result").toString());
            if (jsonObject != null)
                return jsonObject.toJSONString();
        }
        return null;
    }

    public static void main(String[] args){
        addOne(ParamUtils.paramsToString(13941900, 63198, 13, 11), new GraphService().searchMinPath(13941900, 63198, 13, 11));
    }
}
