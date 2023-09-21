package fr.joupi.api.visual.scoreboard.lines.animation;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Scroller implements LineAnimation {

    private final int delay;
    private final String[] texts;
    private int textIndex = 0;

    private final int shownLength;
    private int index;

    private boolean colorCode = false;
    private final List<ChatColor> previousCodes = new ArrayList<>();

    public Scroller(int delay, int shownLength, String... texts) {
        this.delay = delay;
        if (texts.length == 0) throw new IllegalArgumentException("At least 1 text must be provided.");
        this.texts = texts;
        if (shownLength > 32 || shownLength < 1) throw new IllegalArgumentException("Shown length must be at least 1 and at most 32.");
        this.shownLength = shownLength;
        index = -shownLength;
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    public String next() {
        String text = texts[textIndex];

        int from = Math.max(index, 0);
        int spaceDifference = -index;

        int to = index + shownLength;
        if (to < 0) to = 0;
        if (to > text.length()) to = text.length();

        String shownText = text.substring(from, to);
        if (spaceDifference > 0)
            shownText = new String(new char[spaceDifference]).replace('\0', ' ') + shownText;

        index++;
        if (index > text.length()) {
            index = -shownLength;
            textIndex++;

            if (textIndex == texts.length)
                textIndex = 0;
        }

        if (shownText.endsWith("§"))
            shownText = shownText.substring(0, shownText.length() - 1);

        if (colorCode) {
            ChatColor c = ChatColor.getByChar(shownText.charAt(0));

            if (c.isColor() || c == ChatColor.RESET)
                previousCodes.clear();

            if (!previousCodes.contains(c))
                previousCodes.add(c);
        }

        colorCode = shownText.startsWith("§");

        StringBuilder finalText = new StringBuilder();

        for (ChatColor color : previousCodes)
            finalText.append(color);

        finalText.append(shownText);

        return finalText.toString();
    }

    @Override
    public int getDelay() {
        return delay;
    }

}
