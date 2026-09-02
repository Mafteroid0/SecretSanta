package ru.mafteroid.secretsanta.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageProcessor {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public byte[] process(MultipartFile file, int maxSize) {
        validate(file);

        BufferedImage source = read(file);
        BufferedImage resized = resize(source, maxSize);

        return encodeJpeg(resized);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image is too large");
        }

        String contentType = file.getContentType();

        if (!"image/jpeg".equals(contentType)
                && !"image/png".equals(contentType)) {
            throw new IllegalArgumentException("Unsupported image format");
        }
    }

    private BufferedImage read(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                throw new IllegalArgumentException("Invalid image");
            }

            return image;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read image", e);
        }
    }

    private BufferedImage resize(BufferedImage source, int maxSize) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        double scale = Math.min(
                1.0,
                (double) maxSize / Math.max(sourceWidth, sourceHeight)
        );

        int width = Math.max(1, (int) Math.round(sourceWidth * scale));
        int height = Math.max(1, (int) Math.round(sourceHeight * scale));

        BufferedImage result = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = result.createGraphics();

        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);

            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            graphics.drawImage(
                    source,
                    0,
                    0,
                    width,
                    height,
                    null
            );
        } finally {
            graphics.dispose();
        }

        return result;
    }

    private byte[] encodeJpeg(BufferedImage image) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, "jpg", output)) {
                throw new IllegalStateException("Cannot encode image");
            }

            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot encode image", e);
        }
    }
}
