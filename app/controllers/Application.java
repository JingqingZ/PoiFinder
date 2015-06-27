package controllers;

import play.*;
import play.mvc.*;

import java.util.Map;
import java.lang.Math;
import views.html.*;
import org.json.JSONObject;

public class Application extends Controller {

    public static Result index() {
        // return ok(index.render("Your new application is ready."));
        // System.out.print("index page");
        return ok(index.render());
    }

    public static Result searchPoi(Double nelat, Double nelng, Double swlat, Double swlng, Integer k) {
        System.out.println("------------------------");
        PlaceInfoBucket results = KDTreeSearch.search(KDTreeSearch.root, nelat, nelng, swlat, swlng);
        Map<String, Integer> tagMap = TagMap.buildMap(results);

        String tagstr = "";
        //int k = 10;
        int curk = 0;
        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            if (curk > 0)
                tagstr += ",";
            tagstr += "{";
            tagstr += "\"name\": \"";
            tagstr += entry.getKey();
            tagstr += "\", \"weight\":";
            tagstr += entry.getValue().toString();
            tagstr += "}";
            if (++curk >= k)
                break;
        }
        System.out.println(tagstr);
        String str = "";
        for (int i = 1 ; i <= Math.min(results.size, 30) ; i++) {
            int r = (32767 * i) % results.size;
            if (i > 1)
                str += ",";
            str += "{";
            str += "\"lat\":";
            str += results.places[r].lat.toString();
            str += ", \"lng\":";
            str += results.places[r].lng.toString();
            str += ", \"name\": \"";
            str += results.places[r].name;
            str += "\", \"addr\": \"";
            str += results.places[r].addr;
            str += "\"}";
        }
        //System.out.println(str);
        return ok("{\"tags\": [" + tagstr + "], \"results\": [" + str + "]}");
    }

}
