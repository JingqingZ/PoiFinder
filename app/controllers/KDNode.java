package controllers;

/**
 * Created by zhangyunyu940821 on 15/6/26.
 */
public class KDNode {
    public PlaceInfo places[];
    public int splitType;
    public Double splitPos;
    public Double northEastLat;
    public Double northEastLng;
    public Double southWestLat;
    public Double southWestLng;
    public KDNode leftChild;
    public KDNode rightChild;

    public KDNode() {
        leftChild = null;
        rightChild = null;
    }
}
