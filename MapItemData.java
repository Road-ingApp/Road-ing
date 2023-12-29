package com.example.roading;

public class MapItemData {
    private final String url;
    private final String node;
    private final String route;
    private final int map_ID;
    public MapItemData(int map_ID, String url, String node, String route) {
        this.map_ID = map_ID;
        this.url = url;
        this.node = node;
        this.route = route;
    }

    public int getMap_ID(){return map_ID;}
    public String getUrl() {
        return url;
    }
    public String getNode(){return node;}
    public String getRoute(){return route;}
}
