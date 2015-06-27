package controllers;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

/**
 * Created by zhangyunyu940821 on 15/6/27.
 */
public class TagMap {
    public static Map<String, Integer> buildMap(PlaceInfoBucket results) {
        Map<String, Integer> tagmap = new HashMap<String, Integer>();
        for (int i = 0 ; i < results.size ; i++) {
            String[] tags = results.places[i].name.split(" ");
            for (int j = 0 ; j < tags.length ; j++) {
                System.out.println(tags[j]);
                if(tagmap.containsKey(tags[j])) {
                    Integer v = tagmap.get(tags[j]);
                    tagmap.put(tags[j], v + 1);
                } else {
                    tagmap.put(tags[j], 1);
                }
            }
        }
        // sort
        Map<String, Integer> sortedTagMap = sortByComparator(tagmap);

        return sortedTagMap;
    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
