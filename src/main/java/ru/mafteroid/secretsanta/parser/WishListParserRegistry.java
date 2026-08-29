package ru.mafteroid.secretsanta.parser;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
public class WishListParserRegistry {
    private final List<WishListParser> parsers;

    public WishListParserRegistry(List<WishListParser> parsers) {
        this.parsers = parsers;
    }

    public WishListParser getParser(URI uri) {
        return parsers.stream().filter(parser -> parser.supports(uri)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No parser found for " + uri.getHost()));
    }
}
