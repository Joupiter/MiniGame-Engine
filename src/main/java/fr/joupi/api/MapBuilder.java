package fr.joupi.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import javax.imageio.ImageIO;

@Getter
public class MapBuilder {

    private final List<Text> texts;
    private BufferedImage image;
    private final MapCursorCollection cursors;

    private boolean rendered, renderOnce;

    public MapBuilder() {
        this.cursors = new MapCursorCollection();
        this.texts = new ArrayList<>();
        this.rendered = false;
        this.renderOnce = true;
    }

    public MapBuilder setImage(BufferedImage image) {
        this.image = image;
        return this;
    }

    public MapBuilder setImage(String url) {
        try {
            this.image = ImageIO.read(new URL(url));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return this;
    }

    public MapBuilder addText(int x, int y, MapFont font, String text) {
        getTexts().add(new Text(x, y, font, text));
        return this;
    }

    @SuppressWarnings("deprecation")
    public MapBuilder addCursor(int x, int y, CursorDirection direction, CursorType type) {
        getCursors().addCursor(x, y, (byte) direction.getId(), (byte) type.getId());
        return this;
    }

    public MapBuilder setRenderOnce(boolean renderOnce) {
        this.renderOnce = renderOnce;
        return this;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(Material.MAP);
        MapView map = Bukkit.createMap(Bukkit.getWorlds().get(0));

        map.setScale(Scale.FARTHEST);
        map.getRenderers().forEach(map::removeRenderer);
        map.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                if (rendered && renderOnce)
                    return;

                if (player != null && player.isOnline()) {
                    if (image != null)
                        mapCanvas.drawImage(0, 0, image);

                    if (!texts.isEmpty())
                        texts.forEach(text -> mapCanvas.drawText(text.getX(), text.getY(), text.getFont(), text.getMessage()));

                    if (cursors.size() > 0)
                        mapCanvas.setCursors(cursors);

                    rendered = true;
                }
            }
        });

        item.setDurability(getMapId(map));
        return item;
    }

    @SuppressWarnings("deprecation")
    private short getMapId(MapView mapView) {
        try {
            return mapView.getId();
        } catch (NoSuchMethodError ex) {
            try {
                return (short) Class.forName("org.bukkit.map.MapView").getMethod("getId").invoke(mapView, new Object[0]);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
                e.printStackTrace();
                return -1;
            }
        }

    }

    @Getter
    @AllArgsConstructor
    public enum CursorDirection {

        SOUTH(0),
        SOUTH_WEST_SOUTH(1),
        SOUTH_WEST(2),
        SOUTH_WEST_WEST(3),
        WEST(4),
        NORTH_WEST_WEST(5),
        NORTH_WEST(6),
        NORTH_WEST_NORTH(7),
        NORTH(8),
        NORTH_EAST_NORTH(9),
        NORTH_EAST(10),
        NORTH_EAST_EAST(11),
        EAST(12),
        SOUTH_EAST_EAST(13),
        SOUTH_EAST(14),
        SOUTH_EAST_SOUTH(15);

        private final int id;

    }

    @Getter
    @AllArgsConstructor
    public enum CursorType {

        WHITE_POINTER(0),
        GREEN_POINTER(1),
        RED_POINTER(2),
        BLUE_POINTER(3),
        WHITE_CLOVER(4),
        RED_BOLD_POINTER(5),
        WHITE_DOT(6),
        LIGHT_BLUE_SQUARE(7);

        private final int id;

    }

}

@Getter
@Setter
@AllArgsConstructor
class Text {

    private int x;
    private int y;
    private MapFont font;
    private String message;

}