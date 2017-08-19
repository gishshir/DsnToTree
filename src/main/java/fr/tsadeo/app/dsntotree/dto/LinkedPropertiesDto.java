package fr.tsadeo.app.dsntotree.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinkedPropertiesDto {

    private final Map<String, String> map = new LinkedHashMap<String, String>();

    public void setProperty(String key, String value) {
        this.map.put(key, value);
    }

    public String getProperty(String key) {
        return this.map.get(key);
    }

    public int size() {
        return this.map.size();
    }

    public List<String> listPropertyNames() {
        return new ArrayList<String>(this.map.keySet());
    }
}
