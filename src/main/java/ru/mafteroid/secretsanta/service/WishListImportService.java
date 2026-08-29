package ru.mafteroid.secretsanta.service;

import org.springframework.stereotype.Service;
import ru.mafteroid.secretsanta.dto.ImportedWishListItem;
import ru.mafteroid.secretsanta.dto.WishlistItemResponse;
import ru.mafteroid.secretsanta.parser.WishListParser;
import ru.mafteroid.secretsanta.parser.WishListParserRegistry;

import java.net.URI;
import java.util.List;

@Service
public class WishListImportService {

    private final WishListParserRegistry wishListParserRegistry;
    private final WishListService wishListService;

    public WishListImportService(
            WishListParserRegistry wishListParserRegistry,
            WishListService wishListService
    ){
        this.wishListParserRegistry = wishListParserRegistry;
        this.wishListService = wishListService;
    }

    public List<WishlistItemResponse> importWishlist(
            String username,
            String url
    ){
        URI uri = URI.create(url);
        WishListParser wishListParser = wishListParserRegistry.getParser(uri);
        List<ImportedWishListItem> items = wishListParser.parse(uri);
        return wishListService.importItems(username, items);

    }
}
