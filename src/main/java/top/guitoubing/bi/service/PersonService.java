package top.guitoubing.bi.service;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import top.guitoubing.bi.util.DriverInitialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonService {

    private DriverInitialize driverInitialize = new DriverInitialize();
    private Session session = driverInitialize.getSession();

    public static void main(String[] args){
        PersonService personService = new PersonService();
        StatementResult result = personService.session.run("MATCH p=(n:ns8__Person) RETURN p LIMIT 25");
        while (result.hasNext()){
            Record record = result.next();
            Path path = record.get("p").asPath();
            Iterable<Node> nodes = path.nodes();
            for (Node node : nodes){
                System.out.println(node.id());
            }
        }
        personService.driverInitialize.closeDriverAndSession();
    }



}
