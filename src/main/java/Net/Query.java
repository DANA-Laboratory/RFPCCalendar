package Net;

/**
 * Created by AliReza on 9/11/2016.
 */
public class Query {
    public final static Query FindAll = new Query("all");
    public final static Query FindTypes = new Query("types");
    private final String queryString;
    public Query(String queryString) {
        this.queryString = queryString;
    }
    public String toString(){
        return queryString;
    }
}
