package ca.tnoah.bteterramapfabric.tile;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TileURLConverter {

    private final int defaultZoom;

    public TileURLConverter(int defaultZoom) {
        this.defaultZoom = defaultZoom;
    }

    public final String convertToUrl(String template, int tileX, int tileY, int relativeZoom) {
        return this.convert(template, tileX, tileY, defaultZoom + relativeZoom);
    }

    private String convert(String template, int tileX, int tileY, int absoluteZoom) {
        return replaceRandoms(template)
                .replace("{z}", absoluteZoom + "")
                .replace("{x}", tileX + "")
                .replace("{y}", tileY + "")
                .replace("{u}", tileToQuadKey(tileX, tileY, absoluteZoom));
    }

    private static String replaceRandoms(String url) {
        Matcher m = Pattern.compile("\\{random:([^{}]+)}").matcher(url);
        StringBuilder buffer = new StringBuilder();
        Random r = new Random();
        while(m.find()) {
            String[] randoms = m.group(1).split(",");
            m.appendReplacement(buffer, randoms[r.nextInt(randoms.length)]);
        }
        m.appendTail(buffer);
        return buffer.toString();
    }

    private static String tileToQuadKey(int tileX, int tileY, int absoluteZoom) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = absoluteZoom; i > 0; i--) {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0) digit++;
            if ((tileY & mask) != 0) digit+=2;
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

}
