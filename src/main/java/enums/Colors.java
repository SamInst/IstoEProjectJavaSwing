package enums;

public enum Colors {
    BLUE("#424B98"),
    DARK_BLUE("#343B73"),

    GREEN("#429834"),
    DARK_GREEN("#277C19");

    private final String code;

    Colors(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
