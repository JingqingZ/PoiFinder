package controllers;

import play.*;
import play.mvc.*;

import views.html.*;
import org.json.JSONObject;

public class Application extends Controller {

    public static Result index() {
        //return ok(index.render("Your new application is ready."));
        // System.out.print("index page");
        return ok(index.render());
    }

    public static Result searchPoi(Double nelat, Double nelng, Double swlat, Double swlng) {
        return ok(new String("{'lat':" + nelat + ", 'lng':" + nelng + "}"));
    }

}
