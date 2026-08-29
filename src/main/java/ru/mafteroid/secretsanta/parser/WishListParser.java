package ru.mafteroid.secretsanta.parser;

import ru.mafteroid.secretsanta.dto.ImportedWishListItem;

import java.net.URI;
import java.util.List;

public interface WishListParser {
    boolean supports(URI uri);
    List<ImportedWishListItem> parse(URI uri);
}
