package com.github.wormangel.racionamento.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ImageService {
    @Cacheable("ogImage")
    public BufferedImage getOgImage(int days) throws IOException {
        log.info("Cache miss on ogImage cache for {} days. Regenerating the image...", days);
        // Generate the new image
        Resource resource = new ClassPathResource("static/img/ogimg.png");
        InputStream resourceInputStream = resource.getInputStream();
        BufferedImage image = ImageIO.read(resourceInputStream);
        Graphics graphics = image.getGraphics();

        // Setup anti-aliasing
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // What we want to write
        String text = days + (days > 10 ? " dias" : " dia");

        // Setup the font
        graphics.setFont(new Font("Arial", Font.BOLD, 160));
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(text, graphics);

        // Location to write - centered in the image
        int x = (image.getWidth() - (int) stringBounds.getWidth()) / 2;
        int y = ((image.getHeight() - (int) stringBounds.getHeight()) / 2) + (int) stringBounds.getHeight() - ((int) stringBounds.getHeight()) / 4;

        // Draw the outline - hackish way!
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x - 1, y - 1);
        graphics.drawString(text, x - 1, y + 1);
        graphics.drawString(text, x + 1, y - 1);
        graphics.drawString(text, x + 1, y + 1);
        graphics.drawString(text, x - 2, y - 2);
        graphics.drawString(text, x - 2, y + 2);
        graphics.drawString(text, x + 2, y - 2);
        graphics.drawString(text, x + 2, y + 2);
        graphics.drawString(text, x - 3, y - 3);
        graphics.drawString(text, x - 3, y + 3);
        graphics.drawString(text, x + 3, y - 3);
        graphics.drawString(text, x + 3, y + 3);

        // Draw the text
        graphics.setColor(Color.WHITE);
        graphics.drawString(text, x, y);

        return image;
    }
}
