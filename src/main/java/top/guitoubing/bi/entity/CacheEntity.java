package top.guitoubing.bi.entity;

public class CacheEntity {
    private int id;
    private String label;
    private String name;
    private String uri;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public CacheEntity(int id, String label, String name, String uri) {
        this.id = id;
        this.label = label;
        this.name = name;
        this.uri = uri;
    }
}
