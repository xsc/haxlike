package haxlike.nodes;

import fj.data.List;

class Printer {

    public static String indent(String s) {
        final String pad = "  ";
        return String.join(
            "\n",
            List.arrayList(s.split("\n")).map(line -> pad + line).toJavaList()
        );
    }

    private Printer() {}
}
