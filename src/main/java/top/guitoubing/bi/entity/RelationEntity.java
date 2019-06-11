package top.guitoubing.bi.entity;

public class RelationEntity extends NodeEntity {

    public RelationEntity(){
        super();
    }

    /**
     * RelationEntity的相等需要起始节点、目标节点和关系类型全部相等，用于在List中添加时过滤相同节点
     * @param object 比较对象
     * @return 是否相等
     */

    @Override
    public boolean equals(Object object){
        if (object == null)
            return false;
        else if (!(object instanceof RelationEntity))
            return false;
        if (this.getProperties().get("label") != null && ((RelationEntity) object).getProperties().get("label") != null){
            return this.getProperties().get("target").equals(((RelationEntity) object).getProperties().get("target")) &
                    this.getProperties().get("source").equals(((RelationEntity) object).getProperties().get("source")) &
                    this.getProperties().get("label").equals(((RelationEntity) object).getProperties().get("label"));
        }
        else if (!this.getProperties().get("target").equals(((RelationEntity) object).getProperties().get("target")) ||
                !this.getProperties().get("source").equals(((RelationEntity) object).getProperties().get("source"))){
            return false;
        }else{
            if (!getProperties().keySet().containsAll(((RelationEntity) object).getProperties().keySet()))
                return false;
            else {
                for (String key : getProperties().keySet()){
                    Object value = getProperties().get(key);
                    Object comparedValue = ((RelationEntity) object).getProperties().get(key);
                    if (!value.toString().equals(comparedValue.toString())){
                        return false;
                    }
                }
                return true;
            }
        }
    }
}
