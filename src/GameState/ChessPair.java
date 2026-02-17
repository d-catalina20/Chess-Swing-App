package GameState;

public class ChessPair<K extends Comparable<K>, V> implements Comparable <ChessPair<K, V>> {
    private K key;
    private V value;

    public ChessPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Key: " + key + ", Value: " + value;
    }

    @Override
    public int compareTo(ChessPair<K, V> o) {
        return this.key.compareTo(o.key);
    }
}
