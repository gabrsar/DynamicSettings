package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dynamodb;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Clazz;

class ClazzParser {

    private final Clazz clazz;
    private final Parser parser;

    ClazzParser(Class c, Parser parser) {
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
