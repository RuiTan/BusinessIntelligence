package top.guitoubing.bi.util;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import top.guitoubing.bi.entity.NodeEntity;

import java.util.ArrayList;
import java.util.HashMap;

public class MongoDriverInitialize {
    private static MongoClient mongoClient;

    private static MongoClient getMongoClient() {
        if (mongoClient == null){
            MongoCredential credential =  MongoCredential.createCredential(ConstantDefinition.mongoUser, ConstantDefinition.mongoDatabase, ConstantDefinition.mongoPassword.toCharArray());
            mongoClient = new MongoClient(new ServerAddress(ConstantDefinition.mongoIp, ConstantDefinition.mongoPort), credential, MongoClientOptions.builder().sslEnabled(false).build());
        }
        return mongoClient;
    }

    private static MongoCollection<Document> getMongoCollection(int type){
        switch (type){
            case ConstantDefinition.mongoSingleCollectionType:
                return getMongoClient().getDatabase(ConstantDefinition.mongoDatabase).getCollection(ConstantDefinition.mongoSingleCollection);
            case ConstantDefinition.mongoDoubleCollectionType:
                return getMongoClient().getDatabase(ConstantDefinition.mongoDatabase).getCollection(ConstantDefinition.mongoDoubleCollection);
            case ConstantDefinition.mongoAllMinPathsCollectionType:
                return getMongoClient().getDatabase(ConstantDefinition.mongoDatabase).getCollection(ConstantDefinition.mongoAllMinPathCollection);
            default:
                return getMongoClient().getDatabase(ConstantDefinition.mongoDatabase).getCollection(ConstantDefinition.mongoMinPathCollection);
        }
    }

    public static void addOne(String param, HashMap<String, ArrayList<NodeEntity>> hashMap, int type){
        MongoCollection<Document> collection = getMongoCollection(type);
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(hashMap);
        Document document = new Document("id", param).append("time", TimeUtils.getCurrentTime()).append("result", jsonObject.toJSONString());
        collection.insertOne(document);
    }

    public static String findOne(String param, int type){
        MongoCollection<Document> collection = getMongoCollection(type);
        MongoCursor<Document> cursor = collection.find(Filters.eq("id", param)).iterator();
        if (cursor.hasNext()){
            System.err.println("从MongoDB中查[" + "type:" + type + ",param:" + param + "]");
            JSONObject jsonObject = JSONObject.parseObject(cursor.next().get("result").toString());
            if (jsonObject != null) {
                return jsonObject.toJSONString();
            }
        }
        System.err.println("从Neo4j中查[" + "type:" + type + ",param:" + param + "]");
        return null;
    }

    public static ArrayList<HashMap<String, Object>> getHistory(int type){
        ArrayList<HashMap<String, Object> > result = new ArrayList<>();
        MongoCollection<Document> collection = getMongoCollection(type);
        FindIterable<Document> documents = collection.find().limit(ConstantDefinition.mongoMaxHistories).sort(new BasicDBObject("time", -1));
        for (Document document : documents){
            if (document.get("result") != null && !document.get("result").toString().equals("")){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", document.get("id"));
                hashMap.put("time", document.get("time"));
                hashMap.put("result", document.get("result"));
                result.add(hashMap);
            }
        }
        return result;
    }

}
