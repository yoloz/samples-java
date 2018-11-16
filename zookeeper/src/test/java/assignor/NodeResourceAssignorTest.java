package assignor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

/**
 *
 */
public class NodeResourceAssignorTest {
//    @Test
//    public void assign() throws Exception {
//    }

    public static void main(String[] args) {

        List<Integer> allNodes = new ArrayList<>(Arrays.asList(1, 2, 3, 4,5));
        Map<Integer, Float> resource = new HashMap<>();
        resource.put(1, (float) 1.2);
        resource.put(2, (float) 1.3);
        resource.put(3, (float) 1.4);
        resource.put(4, (float) 1.5);
        resource.put(5, (float) 1.2);
        if (resource.isEmpty()) {
            System.out.println(new Random().nextInt(allNodes.size()));
        } else {
            List<Integer> assignorNode = new ArrayList<>(resource.size());
            float min = Collections.min(resource.values());
            resource.forEach((k, v) -> {
                if (v == min) assignorNode.add(k);
            });
            System.out.println(Arrays.toString(assignorNode.toArray()));
        }
    }
}