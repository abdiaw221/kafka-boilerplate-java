package io.confluent.examples.clients.cloud.revisions;

import java.util.function.BiFunction;
import java.util.function.Function;

public class App {

    public static void main(String[] args) {


        final String prefix = "Ababacar Diaw";


        System.out.println(process("Hello World New Revision", String::toLowerCase));

        System.out.println(process("Hello Lambda Expression", (String str) -> str.toLowerCase()));

        //Simplified Method without Type definition
        System.out.println(process("Hello Lambda Expression", str -> str.toLowerCase()));


        System.out.println(process("Hello Lambda Expression Type Inference", str -> {
            String lower = str.toLowerCase();
            // Execute some correction inside the method
            return prefix.concat(lower);
        }));
        System.out.println(process("Hello Lambda Expression", str -> prefix.concat(str)));
        System.out.println(process("Hello Lambda Expression Type Inference", str -> str.toUpperCase()));


        System.out.println(processFunctionalInterface("Hello Lambda Expression Functional Interface", (String str) -> str.toLowerCase()));
        System.out.println(processFunctionalInterface("Hello World Expression Functional Interface + Type Inference", String::toUpperCase));


        System.out.println(processFunctionalInterface("Hello BiFunctional Lambda ", 2, (str, i) -> str.substring(i)));
        System.out.println(processFunctionalInterface("Hello BiFunctional + Type inference ", 5, String::substring));

    }

    private static String process(String str, Processing processor) {
        return processor.process(str);
    }


    private static String processFunctionalInterface(String str, Function<String, String> processor) {
        return processor.apply(str);
    }

    private static String processFunctionalInterface(String str, int i, BiFunction<String, Integer, String> processor) {
        return processor.apply(str, i);
    }
}


// 1) str -> str.toLowerCase() --- String::toLowerCase
// 2) (str, i) -> str.substring(i) --- String::substring
// 3) number -> String.valueOf(number) --- String::valueOf
// 4) (originalString) -> new String(originalString) --- String::new
// 5) (someOtherString) -> someString.concat(someOtherString) --- someString::concat