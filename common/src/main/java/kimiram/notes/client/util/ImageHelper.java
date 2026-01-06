package kimiram.notes.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import kimiram.notes.Image;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static kimiram.notes.Constants.LOGGER;
import static kimiram.notes.Constants.MOD_ID;

public class ImageHelper {
    private static final Identifier DEFAULT_IMAGE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/default_image.png");

    private static final Map<String, Identifier> loadingImages = new HashMap<>();
    private static final Map<String, DownloadedImage> downloadedImages = new HashMap<>();
    private static final Map<String, LoadedImage> loadedImages = new HashMap<>();
    private static int cnt = 0;

    public static void downloadImage(String url) {
        if (!loadingImages.containsKey(url) && !loadedImages.containsKey(url)) {
            Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, "image" + cnt);
            cnt++;
            loadingImages.put(url, id);
            Thread downloadThread = new Thread(() -> {
                try {
                    URI uri = new URI(url);
                    URL imageURL = uri.toURL();
                    BufferedImage bufferedImage = ImageIO.read(imageURL);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", baos);
                    downloadedImages.put(url, new DownloadedImage(id, baos.toByteArray()));
                } catch (Exception e) {
                    loadingImages.remove(url);
                    LOGGER.warn("Failed to download image because: " + e);
                }
            });
            downloadThread.start();
        }
    }

    public static void loadImage(String url) {
        if (downloadedImages.containsKey(url)) {
            try {
                Identifier id = downloadedImages.get(url).id();
                byte[] bytes = downloadedImages.get(url).bytes();
                downloadedImages.remove(url);

                NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(bytes));
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                DynamicTexture texture = new DynamicTexture(nativeImage::toString, nativeImage);
                textureManager.register(id, texture);

                int width = nativeImage.getWidth(), height = nativeImage.getHeight();
                if (width > 120 || height > 160) {
                    if (3 * height < 4 * width) {
                        height = 120 * height / width;
                        width = 120;
                    } else {
                        width = 160 * width / height;
                        height = 160;
                    }
                }

                loadingImages.remove(url);
                loadedImages.put(url, new LoadedImage(id, width, height));
            } catch (Exception e) {
                loadingImages.remove(url);
                LOGGER.warn("Failed to load image because: " + e);
            }
        }
    }

    public static Identifier getImageID(String url) {
        if (loadedImages.containsKey(url)) {
            return loadedImages.get(url).id();
        }
        loadImage(url);
        return DEFAULT_IMAGE;
    }

    public static Image getImage(String url) {
        if (loadedImages.containsKey(url)) {
            LoadedImage image = loadedImages.get(url);
            return new Image(url, 0, 0, image.width, image.height);
        } else {
            return new Image(url, 0, 0, 120, 120);
        }
    }

    private record DownloadedImage(Identifier id, byte[] bytes) {
    }

    private record LoadedImage(Identifier id, int width, int height) {
    }
}
