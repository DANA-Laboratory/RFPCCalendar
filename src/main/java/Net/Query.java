package Net;

/**
 * Created by AliReza on 9/11/2016.
 */
public class Query {
    public final static String FindAll = "all";
    public final static String FindTypes = "types";
    private final String queryString;
    private String parameter;
    public Query(String queryString, String parameter) {
        this.queryString = queryString;
        this.parameter = parameter;
    }
    public String toString(){
        return queryString;
    }
}
