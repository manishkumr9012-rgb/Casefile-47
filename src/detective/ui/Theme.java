package detective.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public final class Theme {

    private Theme() {
    }

    // ============================================================
    // COLOURS
    // ============================================================

    public static final Color BG =
            new Color(244, 242, 236);

    public static final Color TOP =
            new Color(24, 36, 52);

    public static final Color PANEL =
            new Color(255, 253, 249);

    public static final Color CARD =
            new Color(255, 254, 251);

    public static final Color CARD_HOVER =
            new Color(248, 244, 235);

    public static final Color INPUT =
            new Color(250, 249, 246);

    public static final Color BORDER =
            new Color(205, 201, 193);

    public static final Color BORDER_SOFT =
            new Color(225, 221, 214);

    public static final Color TEXT =
            new Color(39, 45, 54);

    public static final Color MUTED =
            new Color(105, 112, 121);

    public static final Color GOLD =
            new Color(190, 128, 25);

    public static final Color GOLD_DARK =
            new Color(154, 101, 14);

    public static final Color GOLD_SOFT =
            new Color(232, 218, 190);

    public static final Color BLUE =
            new Color(56, 112, 168);

    public static final Color GREEN =
            new Color(54, 137, 82);

    public static final Color RED =
            new Color(190, 65, 65);


    // ============================================================
    // FONTS
    // ============================================================

    public static final Font DISPLAY =
            new Font(
                    "SansSerif",
                    Font.BOLD,
                    30
            );

    public static final Font DISPLAY_SMALL =
            new Font(
                    "SansSerif",
                    Font.BOLD,
                    25
            );

    public static final Font H1 =
            new Font(
                    "SansSerif",
                    Font.BOLD,
                    23
            );

    public static final Font H2 =
            new Font(
                    "SansSerif",
                    Font.BOLD,
                    16
            );

    public static final Font BODY =
            new Font(
                    "SansSerif",
                    Font.PLAIN,
                    13
            );

    public static final Font BODY_BOLD =
            new Font(
                    "SansSerif",
                    Font.BOLD,
                    13
            );

    public static final Font SMALL =
            new Font(
                    "SansSerif",
                    Font.PLAIN,
                    10
            );

    public static final Font SMALL_BOLD =
            new Font(
                    "SansSerif",
                    Font.BOLD,
                    10
            );


    // ============================================================
    // PANEL
    // ============================================================

    public static void panel(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        if (w <= 0 || h <= 0) {
            return;
        }

        g.setColor(
                new Color(
                        0,
                        0,
                        0,
                        15
                )
        );

        g.fillRoundRect(
                x + 2,
                y + 3,
                w,
                h,
                12,
                12
        );

        g.setColor(PANEL);

        g.fillRoundRect(
                x,
                y,
                w,
                h,
                12,
                12
        );

        g.setColor(BORDER);

        g.drawRoundRect(
                x,
                y,
                w - 1,
                h - 1,
                12,
                12
        );
    }


    // ============================================================
    // CARD
    // ============================================================

    public static void card(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        if (w <= 0 || h <= 0) {
            return;
        }

        g.setColor(
                new Color(
                        0,
                        0,
                        0,
                        12
                )
        );

        g.fillRoundRect(
                x + 2,
                y + 3,
                w,
                h,
                10,
                10
        );

        g.setColor(CARD);

        g.fillRoundRect(
                x,
                y,
                w,
                h,
                10,
                10
        );

        g.setColor(BORDER_SOFT);

        g.drawRoundRect(
                x,
                y,
                w - 1,
                h - 1,
                10,
                10
        );
    }


    // ============================================================
    // SELECTED CARD
    // ============================================================

    public static void selectedCard(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h
    ) {

        card(
                g,
                x,
                y,
                w,
                h
        );

        g.setColor(GOLD);

        g.drawRoundRect(
                x + 1,
                y + 1,
                w - 3,
                h - 3,
                10,
                10
        );
    }


    // ============================================================
    // BUTTON
    // ============================================================

    /*
     * Old version compatibility.
     */
    public static void button(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h,
            String text,
            boolean active
    ) {

        button(
                g,
                x,
                y,
                w,
                h,
                text,
                active,
                false
        );
    }


    /*
     * New version used by GameWindow.
     */
    public static void button(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h,
            String text,
            boolean active,
            boolean hovered
    ) {

        if (w <= 0 || h <= 0) {
            return;
        }

        Color fill;

        if (active) {
            fill = GOLD;
        }
        else if (hovered) {
            fill = CARD_HOVER;
        }
        else {
            fill = CARD;
        }

        // shadow
        g.setColor(
                new Color(
                        0,
                        0,
                        0,
                        15
                )
        );

        g.fillRoundRect(
                x + 1,
                y + 2,
                w,
                h,
                8,
                8
        );

        // body
        g.setColor(fill);

        g.fillRoundRect(
                x,
                y,
                w,
                h,
                8,
                8
        );

        // border
        g.setColor(
                active
                        ? GOLD_DARK
                        : BORDER
        );

        g.drawRoundRect(
                x,
                y,
                w - 1,
                h - 1,
                8,
                8
        );

        // text
        g.setFont(BODY_BOLD);

        g.setColor(
                active
                        ? Color.WHITE
                        : TEXT
        );

        FontMetrics fm =
                g.getFontMetrics();

        int tx =
                x +
                        (w - fm.stringWidth(text)) / 2;

        int ty =
                y +
                        (h - fm.getHeight()) / 2 +
                        fm.getAscent();

        g.drawString(
                text,
                tx,
                ty
        );
    }


    // ============================================================
    // SMALL BUTTON
    // ============================================================

    public static void smallButton(
            Graphics2D g,
            int x,
            int y,
            int w,
            int h,
            String text
    ) {

        button(
                g,
                x,
                y,
                w,
                h,
                text,
                false,
                false
        );
    }


    // ============================================================
    // LABEL
    // ============================================================

    public static void label(
            Graphics2D g,
            String text,
            int x,
            int y,
            Font font,
            Color color
    ) {

        g.setFont(font);
        g.setColor(color);

        g.drawString(
                text,
                x,
                y
        );
    }


    // ============================================================
    // SECTION TITLE
    // ============================================================

    public static void section(
            Graphics2D g,
            String eyebrow,
            String title,
            int x,
            int y
    ) {

        label(
                g,
                eyebrow.toUpperCase(),
                x,
                y,
                SMALL_BOLD,
                GOLD
        );

        label(
                g,
                title,
                x,
                y + 31,
                H1,
                TEXT
        );
    }


    // ============================================================
    // DIVIDER
    // ============================================================

    public static void divider(
            Graphics2D g,
            int x,
            int y,
            int w
    ) {

        g.setColor(BORDER_SOFT);

        g.drawLine(
                x,
                y,
                x + w,
                y
        );
    }


    // ============================================================
    // BADGE
    // ============================================================

    public static void badge(
            Graphics2D g,
            String text,
            int x,
            int y,
            int w,
            Color color
    ) {

        if (w <= 0) {
            return;
        }

        g.setColor(
                new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        25
                )
        );

        g.fillRoundRect(
                x,
                y,
                w,
                22,
                11,
                11
        );

        g.setColor(color);

        g.drawRoundRect(
                x,
                y,
                w - 1,
                21,
                11,
                11
        );

        g.setFont(SMALL_BOLD);

        FontMetrics fm =
                g.getFontMetrics();

        int tx =
                x +
                        (w - fm.stringWidth(text)) / 2;

        int ty =
                y +
                        (22 - fm.getHeight()) / 2 +
                        fm.getAscent();

        g.drawString(
                text,
                tx,
                ty
        );
    }


    // ============================================================
    // TEXT WRAPPING
    // ============================================================

    public static int wrap(
            Graphics2D g,
            String text,
            int x,
            int y,
            int maxWidth,
            int lineHeight
    ) {

        if (text == null ||
                text.isEmpty()) {

            return y;
        }

        FontMetrics fm =
                g.getFontMetrics();

        int currentY = y;

        String[] paragraphs =
                text.split(
                        "\\n",
                        -1
                );

        for (String paragraph :
                paragraphs) {

            if (paragraph.trim().isEmpty()) {

                currentY += lineHeight;
                continue;
            }

            String[] words =
                    paragraph.trim()
                            .split("\\s+");

            String line = "";

            for (String word : words) {

                String candidate =
                        line.isEmpty()
                                ? word
                                : line + " " + word;

                if (
                        fm.stringWidth(candidate)
                                > maxWidth
                                && !line.isEmpty()
                ) {

                    g.drawString(
                            line,
                            x,
                            currentY
                    );

                    currentY += lineHeight;

                    line = word;

                }
                else {

                    line = candidate;
                }
            }

            if (!line.isEmpty()) {

                g.drawString(
                        line,
                        x,
                        currentY
                );

                currentY += lineHeight;
            }
        }

        return currentY;
    }


    // ============================================================
    // IMAGE
    // ============================================================

    public static void imageCover(
            Graphics2D g,
            Image image,
            int x,
            int y,
            int w,
            int h
    ) {

        if (w <= 0 || h <= 0) {
            return;
        }

        g.setColor(INPUT);

        g.fillRoundRect(
                x,
                y,
                w,
                h,
                10,
                10
        );

        Shape oldClip =
                g.getClip();

        g.clip(
                new RoundRectangle(
                        x,
                        y,
                        w,
                        h,
                        10
                )
        );

        if (image != null) {

            int iw =
                    image.getWidth(null);

            int ih =
                    image.getHeight(null);

            if (iw > 0 && ih > 0) {

                double scale =
                        Math.max(
                                (double) w / iw,
                                (double) h / ih
                        );

                int dw =
                        (int) (iw * scale);

                int dh =
                        (int) (ih * scale);

                int dx =
                        x + (w - dw) / 2;

                int dy =
                        y + (h - dh) / 2;

                g.drawImage(
                        image,
                        dx,
                        dy,
                        dw,
                        dh,
                        null
                );
            }
        }

        g.setClip(oldClip);

        g.setColor(BORDER);

        g.drawRoundRect(
                x,
                y,
                w - 1,
                h - 1,
                10,
                10
        );
    }


    // ============================================================
    // ROUNDED SHAPE
    // ============================================================

    private static final class RoundRectangle
            implements Shape {

        private final RoundRectangle2D shape;

        RoundRectangle(
                int x,
                int y,
                int w,
                int h,
                int arc
        ) {

            shape =
                    new RoundRectangle2D.Float(
                            x,
                            y,
                            w,
                            h,
                            arc,
                            arc
                    );
        }

        @Override
        public Rectangle2D getBounds2D() {
            return shape.getBounds2D();
        }

        @Override
        public Rectangle getBounds() {
            return shape.getBounds();
        }

        @Override
        public boolean contains(
                double x,
                double y
        ) {

            return shape.contains(
                    x,
                    y
            );
        }

        @Override
        public boolean contains(
                Point2D p
        ) {

            return shape.contains(p);
        }

        @Override
        public boolean intersects(
                double x,
                double y,
                double w,
                double h
        ) {

            return shape.intersects(
                    x,
                    y,
                    w,
                    h
            );
        }

        @Override
        public boolean intersects(
                Rectangle2D r
        ) {

            return shape.intersects(r);
        }

        @Override
        public boolean contains(
                double x,
                double y,
                double w,
                double h
        ) {

            return shape.contains(
                    x,
                    y,
                    w,
                    h
            );
        }

        @Override
        public boolean contains(
                Rectangle2D r
        ) {

            return shape.contains(r);
        }

        @Override
        public PathIterator getPathIterator(
                AffineTransform at
        ) {

            return shape.getPathIterator(at);
        }

        @Override
        public PathIterator getPathIterator(
                AffineTransform at,
                double flatness
        ) {

            return shape.getPathIterator(
                    at,
                    flatness
            );
        }
    }
}