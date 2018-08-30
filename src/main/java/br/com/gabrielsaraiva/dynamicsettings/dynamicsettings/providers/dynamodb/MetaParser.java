package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dynamodb;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Clazz;

class MetaParser {

    /** A MetaParser is used to tell what should be used
     * to parse data from a specific class (complex or simple one)
     *
     * For Example:
     *
     * if you have a String you will need Parser A
     * if you have an Integer you will need Parser B
     * ...
     * if you have a List of Strings, you will need Parser M
     * if you have a list of Integers, you will need Parser N
     * ...
     *
     * So, each sentense "if you have X you will need a Y" is a MetaParser object.
     *
     */

    private final Clazz clazz;
    private final Parser parser;

    MetaParser(Class c, Parser parser) {
        this.clazz = new Clazz(c);
        this.parser = parser;
    }

    Clazz getClazz() {
        return clazz;
    }

    Parser getParser() {
        return parser;
    }
}
