package controllers;

import java.lang.String;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Iterator;
import play.*;

/**
 * Created by zhangyunyu940821 on 15/6/26.
 */
public class KDTreeSearch{

    public static PlaceInfo places[];
    public static int PlaceNum = 0;
    public static KDNode root;

    /*
     * create KD Tree
     */
    public static int creatIndex() {
        KDTreeSearch.readPlacesInfo("data/zipcode-address.json");
        Logger.info("Read places info completed!");
        root = buildKDTree(0, PlaceNum, places);
        Logger.info("KD-Tree construction completed!");
        return 1;
    }

    public static KDNode buildKDTree(int depth, int size, PlaceInfo places[]) {
        KDNode node = new KDNode();

        node.places = new PlaceInfo[size];
        for (int i = 0 ; i < size ; i++) node.places[i] = places[i];
        node.splitType = depth % 2;

        if (size == 1) return node;

        if (depth % 2 == 0) {
            Arrays.sort(places, 0, size, new Comparator<PlaceInfo>() {
                @Override
                public int compare(PlaceInfo o1, PlaceInfo o2) {
                    if (o1.lat > o2.lat) return -1;
                    else if (o1.lat < o2.lat) return 1;
                    else return 0;
                }
            });
            node.northEastLat = places[0].lat;
            node.southWestLat = places[size - 1].lat;
            node.splitPos = places[size / 2].lat;
            node.northEastLng = -1000.0;
            node.southWestLng = 1000.0;
            for (int i = 0 ; i < size ; i++) {
                if (node.northEastLng > places[i].lng) node.northEastLng = places[i].lng;
                if (node.southWestLng < places[i].lng) node.southWestLng = places[i].lng;
            }

        } else {
            Arrays.sort(places, 0, size, new Comparator<PlaceInfo>() {
                @Override
                public int compare(PlaceInfo o1, PlaceInfo o2) {
                    if (o1.lng > o2.lng) return -1;
                    else if (o1.lng < o2.lng) return 1;
                    else return 0;
                }
            });
            node.northEastLng = places[0].lng;
            node.southWestLng = places[size - 1].lng;
            node.splitPos = places[size / 2].lng;
            node.northEastLat = -1000.0;
            node.southWestLat = 1000.0;
            for (int i = 0 ; i < size ; i++) {
                if (node.northEastLat > places[i].lat) node.northEastLat = places[i].lat;
                if (node.southWestLat < places[i].lat) node.southWestLat = places[i].lat;
            }

        }

        PlaceInfo leftplaces[] = new PlaceInfo[size / 2];
        PlaceInfo rightplaces[] = new PlaceInfo[(size + 1) / 2];
        for (int i = 0 ; i < size / 2 ; i++) leftplaces[i] = places[i];
        for (int i = size / 2 ; i < size ; i++) rightplaces[i - size / 2] = places[i];
        node.leftChild = buildKDTree(depth + 1, size / 2, leftplaces);
        node.rightChild = buildKDTree(depth + 1, (size + 1) / 2, rightplaces);

        return node;
    }

    private static int readPlacesInfo(String filename) {
        try {
            places = new PlaceInfo[110000];

            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line;

            PlaceNum = 0;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                //System.out.println(line);
                JSONObject json = new JSONObject(line);

                places[PlaceNum] = new PlaceInfo();
                places[PlaceNum].addr = json.getString("addr");
                JSONArray latlng = json.getJSONArray("latlng");
                places[PlaceNum].lat = latlng.getDouble(0);
                places[PlaceNum].lng = latlng.getDouble(1);
                places[PlaceNum].name = json.getString("name");

                PlaceNum ++;
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static PlaceInfoBucket search(KDNode node, Double nelat, Double nelng, Double swlat, Double swlng) {
        PlaceInfoBucket results = new PlaceInfoBucket();
        results.places = new PlaceInfo[node.places.length];
        results.size = 0;

        System.out.println(nelat.toString() + ' ' + nelng.toString() + ' ' +
                           swlat.toString() + ' ' + swlng.toString());
        System.out.println(new Integer(node.splitType).toString() + ' ' + node.splitPos.toString());

        if (node.leftChild == null) {
            if (node.places[0].lat < nelat &&
                    node.places[0].lat > swlat &&
                    node.places[0].lng < nelng &&
                    node.places[0].lng > swlng) {
                results.places[0] = node.places[0];
                results.size = 1;
            }
            return results;
        }

        if (node.splitType == 0) {
            if (nelat > node.splitPos) {
                if (nelat >= node.northEastLat &&
                        nelng >= node.northEastLng &&
                        swlng <= node.southWestLng){
                    System.out.println("lat left all");
                    for (int i = 0 ; i < node.leftChild.places.length ; i++) {
                        results.places[results.size++] = node.leftChild.places[i];
                    }
                } else {
                    System.out.println("lat left iter");
                    PlaceInfoBucket lr = search(node.leftChild, nelat, nelng, node.splitPos, swlng);
                    for (int i = 0 ; i < lr.size ; i++) {
                        results.places[results.size++] = lr.places[i];
                    }
                }
            }

            if (swlat < node.splitPos) {
                if (swlat <= node.southWestLat &&
                        nelng >= node.northEastLng &&
                        swlng <= node.southWestLng){
                    System.out.println("lat right all");
                    for (int i = 0 ; i < node.rightChild.places.length ; i++) {
                        results.places[results.size++] = node.rightChild.places[i];
                    }
                } else {
                    System.out.println("lat right iter");
                    PlaceInfoBucket rr = search(node.rightChild, node.splitPos, nelng, swlat, swlng);
                    for (int i = 0 ; i < rr.size ; i++) {
                        results.places[results.size++] = rr.places[i];
                    }
                }
            }

        } else {
            if (nelng > node.splitPos) {
                if (nelng >= node.northEastLng &&
                        nelat >= node.northEastLat &&
                        swlat <= node.southWestLat){
                    System.out.println("lng left all");
                    for (int i = 0 ; i < node.leftChild.places.length ; i++) {
                        results.places[results.size++] = node.leftChild.places[i];
                    }
                } else {
                    System.out.println("lng left iter");
                    PlaceInfoBucket lr = search(node.leftChild, nelat, nelng, swlat, node.splitPos);
                    for (int i = 0 ; i < lr.size ; i++) {
                        results.places[results.size++] = lr.places[i];
                    }
                }
            }

            if (swlng < node.splitPos) {
                if (swlng <= node.southWestLng &&
                        nelat >= node.northEastLat &&
                        swlat <= node.southWestLat){
                    System.out.println("lng right all");
                    for (int i = 0 ; i < node.rightChild.places.length ; i++) {
                        results.places[results.size++] = node.rightChild.places[i];
                    }
                } else {
                    System.out.println("lng right iter");
                    PlaceInfoBucket rr = search(node.rightChild, nelat, node.splitPos, swlat, swlng);
                    for (int i = 0 ; i < rr.size ; i++) {
                        results.places[results.size++] = rr.places[i];
                    }
                }
            }

        }

        System.out.println(results.size);
        return results;
    }

}
