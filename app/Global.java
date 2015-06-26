/**
 * Created by zhangyunyu940821 on 15/6/26.
 */
import play.*;
import controllers.KDTreeSearch;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        Logger.info("Application open...");
        KDTreeSearch.creatIndex();
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application st...");
    }

}
