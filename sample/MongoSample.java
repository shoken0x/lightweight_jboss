import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoSample {
  public static void main(String[] args) {
    try {
      // connect to the local database server        
      MongoClient mongoClient = new MongoClient();
      // get handle to "test"
      DB db = mongoClient.getDB("test");

      DBCollection coll = db.getCollection("users");
      DBCursor cur = coll.find();

      while(cur.hasNext()) {
              System.out.println(cur.next());
          }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
