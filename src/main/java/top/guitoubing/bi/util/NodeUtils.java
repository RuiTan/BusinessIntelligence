package top.guitoubing.bi.util;

import javafx.util.Pair;

import java.util.HashMap;

public class NodeUtils {

    private static HashMap<Integer, Pair<String, String>> nodeTypes = null;

    public static HashMap<Integer, Pair<String, String>> getNodeTypes(){
        if (nodeTypes == null){
            nodeTypes = new HashMap<>();
            initNodeTypes();
        }
        return nodeTypes;
    }

    // ibm机器节点label名称
//    private static void initNodeTypes() {
//        nodeTypes.put(1, new Pair<>("ns0__Quote","ns1__hasName"));
//        nodeTypes.put(2, new Pair<>("ns4__Organization","ns6__organization-name"));
//        nodeTypes.put(3, new Pair<>("ns0__Instrument","ns1__hasName"));
//        nodeTypes.put(4, new Pair<>("ns0__AssetClass","rdfs__label"));
//        nodeTypes.put(5, new Pair<>("ns2__Currency","skos__prefLabel"));
//        nodeTypes.put(6, new Pair<>("ns2__CurrencySubunit","skos__prefLabel"));
//        nodeTypes.put(7, new Pair<>("ns3__Activity","skos__prefLabel"));
//        nodeTypes.put(8, new Pair<>("ns3__BusinessSector","skos__prefLabel"));
//        nodeTypes.put(9, new Pair<>("ns3__EconomicSector","skos__prefLabel"));
//        nodeTypes.put(10, new Pair<>("ns3__Industry","skos__prefLabel"));
//        nodeTypes.put(11, new Pair<>("ns8__AcademicQualification","ns8__fromInstitutionName"));
//        nodeTypes.put(12, new Pair<>("ns8__Officership","ns8__hasReportedTitle"));
//        nodeTypes.put(13, new Pair<>("ns8__Person","ns6__given-name"));
//        nodeTypes.put(14, new Pair<>("ns8__TenureInOrganization",""));
//        nodeTypes.put(15, new Pair<>("ns3__IndustryGroup","skos__prefLabel"));
//        nodeTypes.put(16, new Pair<>("ns8__Major","skos__prefLabel"));
//        nodeTypes.put(17, new Pair<>("ns8__AcademicDegree","skos__prefLabel"));
//        nodeTypes.put(18, new Pair<>("ns8__OfficerRole","skos__prefLabel"));
//        nodeTypes.put(19, new Pair<>("ns8__DirectorRole","skos__prefLabel"));
//        nodeTypes.put(20, new Pair<>("ns8__Directorship","ns8__hasReportedTitle"));
//    }

    // 宿舍服务器节点label名称
    private static void initNodeTypes() {
        nodeTypes.put(1, new Pair<>("ns4__Quote","ns1__hasName"));
        nodeTypes.put(2, new Pair<>("ns7__Organization","ns6__organization-name"));
        nodeTypes.put(3, new Pair<>("ns4__Instrument","ns1__hasName"));
        nodeTypes.put(4, new Pair<>("ns4__AssetClass","rdfs__label"));
        nodeTypes.put(5, new Pair<>("ns6__Currency","skos__prefLabel"));
        nodeTypes.put(6, new Pair<>("ns6__CurrencySubunit","skos__prefLabel"));
        nodeTypes.put(7, new Pair<>("ns5__Activity","skos__prefLabel"));
        nodeTypes.put(8, new Pair<>("ns5__BusinessSector","skos__prefLabel"));
        nodeTypes.put(9, new Pair<>("ns5__EconomicSector","skos__prefLabel"));
        nodeTypes.put(10, new Pair<>("ns5__Industry","skos__prefLabel"));
        nodeTypes.put(11, new Pair<>("ns0__AcademicQualification","ns8__fromInstitutionName"));
        nodeTypes.put(12, new Pair<>("ns0__Officership","ns8__hasReportedTitle"));
        nodeTypes.put(13, new Pair<>("ns0__Person","ns6__given-name"));
        nodeTypes.put(14, new Pair<>("ns0__TenureInOrganization",""));
        nodeTypes.put(15, new Pair<>("ns5__IndustryGroup","skos__prefLabel"));
        nodeTypes.put(16, new Pair<>("ns0__Major","skos__prefLabel"));
        nodeTypes.put(17, new Pair<>("ns0__AcademicDegree","skos__prefLabel"));
        nodeTypes.put(18, new Pair<>("ns0__OfficerRole","skos__prefLabel"));
        nodeTypes.put(19, new Pair<>("ns0__DirectorRole","skos__prefLabel"));
        nodeTypes.put(20, new Pair<>("ns0__Directorship","ns8__hasReportedTitle"));
    }

    public static Pair<String, String> getTypeFromKey(int key){
        return getNodeTypes().get(key);
    }


}
