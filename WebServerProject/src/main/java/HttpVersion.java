import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1", EnumSet.noneOf(Feature.class)),
    HTTP_1_0("HTTP/1.0",
            EnumSet.of(Feature.CHUNKING,
            Feature.PERSISTENCE,
            Feature.HOST_HEADER_REQUIRED));

    public enum Feature {
        CHUNKING, PERSISTENCE, HOST_HEADER_REQUIRED
    }
    private final String LITERAL;
    private final EnumSet<Feature> features;
    private static final Map<String, HttpVersion> BY_LITERAL = new HashMap<>();
    static {
        for (HttpVersion httpVersion : values()) {
            BY_LITERAL.put(httpVersion.LITERAL, httpVersion);
        }
    }

    HttpVersion (String LITERAL, EnumSet<Feature> features) {
        this.features = features;
        this.LITERAL = LITERAL;
    }

    public String getLITERAL() {
        return LITERAL;
    }
    public static HttpVersion fromLiteral(String literal) {
        return BY_LITERAL.get(literal);
    }

    public boolean allows(Feature feature) {
        return features.contains(feature);
    }
}
