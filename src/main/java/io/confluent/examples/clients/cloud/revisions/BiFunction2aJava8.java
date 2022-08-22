package io.confluent.examples.clients.cloud.revisions;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BiFunction2aJava8 {

    public static void main(String[] args) {

        // Math.pow(a1, a2) returns Double
        BiFunction<Integer, Integer, Double> function = (a1,a2) -> BiFunctionJava8.exploreInference(a1,a2,Math::pow);

        // takes Double, returns String
        Function<Double, String> func2 = (input) -> "Result : " + input;

        String result = function.andThen(func2).apply(2, 4);

        System.out.println(result);
    }
}
