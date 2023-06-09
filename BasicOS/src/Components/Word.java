package Components;

public class Word {
    private String name;
    private Object value;

    public Word(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public void set(Object value) {
        this.value = value;
    }

    public Object get() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + ": " + value.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Word) {
            Word w = (Word) obj;
            return w.getName().equals(name) && w.get().equals(value);
        } else {
            return false;
        }
    }

}
