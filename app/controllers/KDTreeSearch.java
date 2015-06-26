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

    /*
     * create KD Tree
     */
    public static int creatIndex() {
        KDTreeSearch.readPlacesInfo("data/zipcode-address.json");
        Logger.info("Read places info completed!");
        KDNode root = buildKDTree(0, PlaceNum, places);
        Logger.info("KD-Tree construction completed!");
        return 1;
    }

    public static KDNode buildKDTree(int depth, int size, PlaceInfo places[]) {
        KDNode node = new KDNode();

        for (int i = 0 ; i < size ; i++) node.places[i] = places[i];
        node.splitType = depth % 2;

        if (size == 1) return node;

        if (depth % 2 == 0) {
            Arrays.sort(places, 0, size, new Comparator<PlaceInfo>() {
                @Override
                public int compare(PlaceInfo o1, PlaceInfo o2) {
                    if (o1.lat > o2.lat) return -1;
                    else return 1;
                }
            });
            node.northEastLat = places[0].lat;
            node.southWestLat = places[size - 1].lat;

        } else {
            Arrays.sort(places, 0, size, new Comparator<PlaceInfo>() {
                @Override
                public int compare(PlaceInfo o1, PlaceInfo o2) {
                    if (o1.lng > o2.lng) return -1;
                    else return 1;
                }
            });
            node.northEastLng = places[size - 1].lng;
            node.southWestLng = places[0].lng;

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

}
