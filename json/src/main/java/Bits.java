import java.nio.ByteBuffer;
import java.util.BitSet;

public class Bits {
    public static void main(String... args) {
        bits(-1);
        bits(0);
        bits(7);
    }

    private static void bits(int value) {
        System.out.println("value = " + value);
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.asIntBuffer().put(value);
        int cardinality = BitSet.valueOf(bb).cardinality();
        System.out.println("cardinality = " + cardinality);
    }
}