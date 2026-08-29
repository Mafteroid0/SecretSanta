package ru.mafteroid.secretsanta.parser;

import org.springframework.stereotype.Component;
import ru.mafteroid.secretsanta.dto.ImportedWishListItem;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GiftsterParser implements WishListParser {

    private static final Pattern ITEMS_PATTERN = Pattern.compile(
            "window\\.listItemsJson\\s*=\\s*JSON\\.parse\\((" +
                    "\"(?:\\\\.|[^\"\\\\])*\"" +
                    ")\\)"
    );
    private final ObjectMapper objectMapper;

    public GiftsterParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(URI uri) {
        String host = uri.getHost();
        String path = uri.getPath();

        return host != null
                && path != null
                && (host.equalsIgnoreCase("giftster.com")
                || host.equalsIgnoreCase("www.giftster.com"))
                && path.startsWith("/gift/public/");
    }

    @Override
    public List<ImportedWishListItem> parse(URI uri) {
        try {
            var document = Jsoup.connect(uri.toString())
                    .timeout(10_000)
                    .get();

            var script = document.getElementById("initial-list-data");

            if (script == null) {
                throw new IllegalArgumentException(
                        "Invalid Giftster wishlist"
                );
            }

            Matcher matcher =
                    ITEMS_PATTERN.matcher(script.data());

            if (!matcher.find()) {
                throw new IllegalArgumentException(
                        "Giftster items not found"
                );
            }

            String json = objectMapper.readValue(
                    matcher.group(1),
                    String.class
            );

            List<GiftsterItem> items = objectMapper.readValue(
                    json,
                    new TypeReference<>() {}
            );

            return items.stream()
                    .map(item -> new ImportedWishListItem(
                            item.title(),
                            item.description(),
                            item.link(),
                            String.valueOf(item.id())
                    ))
                    .toList();

        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to parse Giftster wishlist",
                    e
            );
        }
    }
    private record GiftsterItem(
            Long id,
            String title,
            String description,
            String link
    ) {}
}
