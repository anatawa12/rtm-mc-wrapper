package java.lang;

public class Object {
    public native int hashCode();

    public boolean equals(Object obj) {
        return this == obj;
    }

    public String toString() {
        return "signature only";
    }
}
