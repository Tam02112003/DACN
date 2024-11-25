package DACN.DACN;


public enum Provider {
    LOCAL("Local"),
    GOOGLE("Google");

    public final String value;
    // Constructor cho enum
    Provider(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}