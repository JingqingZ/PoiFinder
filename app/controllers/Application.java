package controllers;

import play.*;
import play.mvc.*;

import java.lang.Math;
import views.html.*;
import org.json.JSONObject;

public class Application extends Controller {

    public static Result index() {
        // return ok(index.render("Your new application is ready."));
        // System.out.print("index page");
        return ok(index.render());
    }

    public static Result searchPoi(Double nelat, Double nelng, Double swlat, Double swlng) {
        System.out.println("------------------------");
        PlaceInfoBucket results = KDTreeSearch.search(KDTreeSearch.root, nelat, nelng, swlat, swlng);
        String str = "";
        for (int i = 0 ; i < Math.min(results.size, 10) ; i++) {
            if (i > 0)
                str += ",";
            str += "{";
            str += "\"lat\":";
            str += results.places[i].lat.toString();
            str += ", \"lng\":";
            str += results.places[i].lng.toString();
            str += ", \"name\": \"";
            str += results.places[i].name;
            str += "\", \"addr\": \"";
            str += results.places[i].addr;
            str += "\"}";
        }
        //System.out.println(str);
        return ok(new String("{\"results\": [" + str + "]}"));
    }

}
