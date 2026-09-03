package detective.ui;

import detective.logic.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AWT-only interface for CASEFILE: 47.
 *
 * Layout goals:
 * - No unnecessary vertical scrolling on normal screens.
 * - Content expands to use the available window.
 * - Navigation always stays fixed at the bottom.
 * - Cards use the available space instead of leaving large blank areas.
 * - Long evidence/deduction pages scroll only when actually required.
 */
public class GameWindow extends Frame {

    private final Game game;
    private final GameCanvas canvas;
    private final ScrollPane scrollPane;
    private final NavCanvas navCanvas;

    private final Panel bottomBar;
    private final Panel accusationPanel;

    private final TextArea reasoning =
            new TextArea("", 5, 70, TextArea.SCROLLBARS_VERTICAL_ONLY);

    private final Choice accusationChoice = new Choice();

    private final Button submit =
            new Button("SUBMIT ACCUSATION");

    private final Button backToCase =
            new Button("BACK TO CASE");

    public GameWindow(Game game) {
        super("CASEFILE: 47 — The Last Night at MIT University");

        this.game = game;

        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 0));

        scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.setBackground(Theme.BG);

        canvas = new GameCanvas();
        scrollPane.add(canvas);

        add(scrollPane, BorderLayout.CENTER);

        bottomBar = new Panel(new BorderLayout(0, 0));
        bottomBar.setBackground(Theme.TOP);

        accusationPanel = new Panel(new BorderLayout(10, 8));
        accusationPanel.setBackground(Theme.TOP);
        accusationPanel.setVisible(false);

        navCanvas = new NavCanvas();

        bottomBar.add(accusationPanel, BorderLayout.NORTH);
        bottomBar.add(navCanvas, BorderLayout.SOUTH);

        add(bottomBar, BorderLayout.SOUTH);

        styleNativeControls();

        submit.addActionListener(e -> finishAccusation());
        backToCase.addActionListener(e -> navigate("CASE"));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(1360, 820);
        setMinimumSize(new Dimension(1120, 720));
        setLocationRelativeTo(null);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                EventQueue.invokeLater(GameWindow.this::resizeCanvas);
            }
        });

        EventQueue.invokeLater(this::resizeCanvas);
    }

    private void styleNativeControls() {

        reasoning.setBackground(Theme.INPUT);
        reasoning.setForeground(Theme.TEXT);
        reasoning.setFont(Theme.BODY);

        accusationChoice.setBackground(Theme.INPUT);
        accusationChoice.setForeground(Theme.TEXT);
        accusationChoice.setFont(Theme.BODY);

        submit.setBackground(Theme.GOLD);
        submit.setForeground(Color.WHITE);
        submit.setFont(Theme.BODY_BOLD);

        backToCase.setBackground(Theme.CARD);
        backToCase.setForeground(Theme.TEXT);
        backToCase.setFont(Theme.BODY_BOLD);
    }

    private void navigate(String screen) {

        canvas.screen = screen;
        canvas.message = "";

        if ("ACCUSE".equals(screen)) {
            setupAccusation();
        } else {
            removeAccusationPanel();
        }

        resizeCanvas();

        canvas.repaint();
        navCanvas.repaint();

        EventQueue.invokeLater(() -> {
            scrollPane.validate();

            if (scrollPane.getVAdjustable() != null) {
                scrollPane.getVAdjustable().setValue(0);
            }

            if (scrollPane.getHAdjustable() != null) {
                scrollPane.getHAdjustable().setValue(0);
            }
        });
    }

    private void resizeCanvas() {

        if (canvas == null || scrollPane == null) {
            return;
        }

        int viewportW = scrollPane.getWidth();
        int viewportH = scrollPane.getHeight();

        if (viewportW <= 0) {
            viewportW = getWidth() - 20;
        }

        if (viewportH <= 0) {
            viewportH = getHeight() - 100;
        }

        int width = Math.max(980, viewportW - 4);

        int requiredHeight = contentHeight();

        /*
         * For normal pages use exactly the available viewport height.
         * Long pages are allowed to become larger and therefore scroll.
         */
        int height = Math.max(viewportH, requiredHeight);

        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setSize(width, height);

        scrollPane.validate();
    }

    private int contentHeight() {

        switch (canvas.screen) {

            case "HOME":
                return 620;

            case "CASE":
                return 620;

            case "SUSPECTS":
                return 650;

            case "WITNESSES":
                return 620;

            case "EXPLORE":
                return 620;

            case "EVIDENCE":
                return Math.max(
                        620,
                        145 + ((game.getEvidence().size() + 1) / 2) * 106 + 55
                );

            case "TIMELINE":
                return Math.max(
                        620,
                        145 + game.getTimeline().size() * 70 + 45
                );

            case "DEDUCTION":
                return 620;

            case "ACCUSE":
                return 600;

            case "ENDING":
                return 610;

            default:
                return 620;
        }
    }

    private void setupAccusation() {

        accusationChoice.removeAll();

        for (String name : game.getSuspectNames()) {
            accusationChoice.add(name);
        }

        reasoning.setText("");

        accusationPanel.removeAll();

        accusationPanel.setBackground(Theme.TOP);
        accusationPanel.setPreferredSize(new Dimension(0, 154));
        accusationPanel.setMinimumSize(new Dimension(0, 154));
        accusationPanel.setVisible(true);

        Panel title = new Panel(new BorderLayout(12, 0));
        title.setBackground(Theme.TOP);

        Label question =
                new Label("FINAL ACCUSATION  •  WHO IS RESPONSIBLE?");

        question.setForeground(Theme.GOLD);
        question.setFont(Theme.SMALL_BOLD);

        title.add(question, BorderLayout.WEST);
        title.add(accusationChoice, BorderLayout.CENTER);

        Panel actions =
                new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 4));

        actions.setBackground(Theme.TOP);

        actions.add(backToCase);
        actions.add(submit);

        accusationPanel.add(title, BorderLayout.NORTH);
        accusationPanel.add(reasoning, BorderLayout.CENTER);
        accusationPanel.add(actions, BorderLayout.SOUTH);

        accusationPanel.setVisible(true);

        bottomBar.validate();
        validate();

        EventQueue.invokeLater(this::resizeCanvas);
    }

    private void removeAccusationPanel() {

        accusationPanel.setVisible(false);

        bottomBar.validate();
        validate();

        EventQueue.invokeLater(this::resizeCanvas);
    }

    private void finishAccusation() {

        String text = reasoning.getText().trim();

        if (text.length() < 20) {

            canvas.message =
                    "Write at least a short evidence-based explanation before submitting.";

            canvas.repaint();
            return;
        }

        String selected = accusationChoice.getSelectedItem();

        if (selected == null || selected.isEmpty()) {

            canvas.message = "Select a suspect before submitting.";

            canvas.repaint();
            return;
        }

        game.finishAccusation(selected);

        navigate("ENDING");
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    private class NavCanvas extends Canvas
            implements MouseListener, MouseMotionListener {

        private final String[] labels = {
                "CASE",
                "SUSPECTS",
                "WITNESSES",
                "EXPLORE",
                "EVIDENCE",
                "TIMELINE",
                "DEDUCTION",
                "ACCUSE"
        };

        private int hover = -1;

        NavCanvas() {

            setBackground(Theme.TOP);

            setPreferredSize(new Dimension(100, 62));

            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public void update(Graphics g) {
            paint(g);
        }

        @Override
        public void paint(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            g.setColor(Theme.TOP);
            g.fillRect(0, 0, w, h);

            int gap = 8;
            int buttonH = 40;
            int side = 22;

            int buttonW =
                    Math.max(
                            82,
                            (w - side * 2 - gap * 7) / 8
                    );

            int x = side;
            int y = 10;

            for (int i = 0; i < labels.length; i++) {

                boolean active =
                        labels[i].equals(canvas.screen);

                boolean hovered =
                        i == hover;

                Theme.button(
                        g,
                        x,
                        y,
                        buttonW,
                        buttonH,
                        labels[i],
                        active,
                        hovered
                );

                x += buttonW + gap;
            }

            g.dispose();
        }

        private int buttonAt(int px, int py) {

            int gap = 8;
            int buttonH = 40;
            int side = 22;

            int buttonW =
                    Math.max(
                            82,
                            (getWidth() - side * 2 - gap * 7) / 8
                    );

            int x = side;
            int y = 10;

            for (int i = 0; i < labels.length; i++) {

                if (
                        px >= x &&
                                px <= x + buttonW &&
                                py >= y &&
                                py <= y + buttonH
                ) {
                    return i;
                }

                x += buttonW + gap;
            }

            return -1;
        }

        @Override
        public void mousePressed(MouseEvent e) {

            int index = buttonAt(
                    e.getX(),
                    e.getY()
            );

            if (index >= 0) {
                navigate(labels[index]);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

            int next = buttonAt(
                    e.getX(),
                    e.getY()
            );

            if (next != hover) {

                hover = next;
                repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {

            hover = -1;
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}
    }

    // ============================================================
    // MAIN GAME CANVAS
    // ============================================================

    private class GameCanvas extends Canvas
            implements MouseListener,
            MouseMotionListener,
            MouseWheelListener {

        String screen = "HOME";

        String message = "";

        String locationResult =
                "Select a location to investigate it.";

        String selectedLocationName =
                "Investigation notes";

        String selectedSuspect =
                "Manjunath S";

        int hoverButton = -1;

        final List<RectButton> buttons =
                new ArrayList<>();

        Image imran;
        Image nitheesh;
        Image manjunath;
        Image om;
        Image aaradhya;
        Image maniappa;
        Image yashwanth;
        Image ramesh;
        Image rahul;

        GameCanvas() {

            setBackground(Theme.BG);

            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);

            imran = ImageLoader.load("imran_khan.png");
            nitheesh = ImageLoader.load("nitheesh_reedy.png");
            manjunath = ImageLoader.load("manjunath.png");
            om = ImageLoader.load("om_prakesh.png");
            aaradhya = ImageLoader.load("aaradhya_rao.png");
            maniappa = ImageLoader.load("maniappa.png");
            yashwanth = ImageLoader.load("yashwanth.png");
            ramesh = ImageLoader.load("ramesh_patil.png");
            rahul = ImageLoader.load("rahul_mehta.png");
        }

        @Override
        public void update(Graphics g) {
            paint(g);
        }

        @Override
        public void paint(Graphics graphics) {

            Graphics2D g =
                    (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            g.setColor(Theme.BG);
            g.fillRect(0, 0, w, h);

            buttons.clear();

            drawTop(g, w);

            switch (screen) {

                case "HOME":
                    drawHome(g, w, h);
                    break;

                case "CASE":
                    drawCase(g, w, h);
                    break;

                case "SUSPECTS":
                    drawSuspects(g, w, h);
                    break;

                case "WITNESSES":
                    drawWitnesses(g, w, h);
                    break;

                case "EXPLORE":
                    drawExplore(g, w, h);
                    break;

                case "EVIDENCE":
                    drawEvidence(g, w, h);
                    break;

                case "TIMELINE":
                    drawTimeline(g, w, h);
                    break;

                case "DEDUCTION":
                    drawDeduction(g, w, h);
                    break;

                case "ACCUSE":
                    drawAccuse(g, w, h);
                    break;

                case "ENDING":
                    drawEnding(g, w, h);
                    break;
            }

            if (!message.isEmpty()) {

                int my =
                        Math.max(
                                535,
                                h - 42
                        );

                Theme.badge(
                        g,
                        message,
                        28,
                        my,
                        Math.min(w - 56, 760),
                        Theme.RED
                );
            }

            g.dispose();
        }

        private void drawTop(Graphics2D g, int w) {

            g.setColor(Theme.TOP);
            g.fillRect(0, 0, w, 68);

            g.setColor(Theme.GOLD);
            g.fillRect(0, 0, 5, 68);

            Theme.label(
                    g,
                    "CASEFILE: 47",
                    28,
                    29,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.label(
                    g,
                    "THE LAST NIGHT AT MIT UNIVERSITY",
                    28,
                    49,
                    Theme.SMALL,
                    Theme.MUTED
            );

            Theme.label(
                    g,
                    "INVESTIGATOR  IMRAN KHAN",
                    w - 215,
                    29,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.label(
                    g,
                    "11:47 PM  •  ACTIVE CASE",
                    w - 215,
                    49,
                    Theme.SMALL,
                    Theme.MUTED
            );
        }

        // ========================================================
        // HOME
        // ========================================================

        private void drawHome(
                Graphics2D g,
                int w,
                int h
        ) {

            int margin = 28;
            int top = 92;
            int bottom = h - 24;

            Theme.section(
                    g,
                    "Investigator",
                    "Detective Imran Khan",
                    margin,
                    top
            );

            int leftW =
                    Math.min(
                            315,
                            (int) (w * 0.27)
                    );

            int cardTop = top + 48;
            int cardH = bottom - cardTop;

            Theme.card(
                    g,
                    margin,
                    cardTop,
                    leftW,
                    cardH
            );

            Theme.imageCover(
                    g,
                    imran,
                    margin + 16,
                    cardTop + 16,
                    leftW - 32,
                    Math.min(265, cardH - 150)
            );

            int textY =
                    cardTop + Math.min(300, cardH - 120);

            Theme.label(
                    g,
                    "IMRAN KHAN",
                    margin + 16,
                    textY,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.label(
                    g,
                    "ENGINEERING & DIGITAL EVIDENCE",
                    margin + 16,
                    textY + 22,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.wrap(
                    g,
                    "You notice patterns other investigators miss. This case will test that skill.",
                    margin + 16,
                    textY + 52,
                    leftW - 32,
                    18
            );

            int rightX =
                    margin + leftW + 20;

            int rightW =
                    w - rightX - margin;

            Theme.section(
                    g,
                    "Case briefing",
                    "The Last Night at MIT University",
                    rightX,
                    top
            );

            int briefingY = cardTop;
            int briefingH = Math.min(
                    255,
                    cardH - 115
            );

            Theme.panel(
                    g,
                    rightX,
                    briefingY,
                    rightW,
                    briefingH
            );

            Theme.label(
                    g,
                    "11:47 PM",
                    rightX + 24,
                    briefingY + 42,
                    Theme.DISPLAY_SMALL,
                    Theme.GOLD
            );

            Theme.label(
                    g,
                    "THE CALL",
                    rightX + 24,
                    briefingY + 72,
                    Theme.SMALL_BOLD,
                    Theme.MUTED
            );

            int cy = briefingY + 103;

            cy = Theme.wrap(
                    g,
                    "A brilliant final-year engineering student is found dead in the basement parking area. His phone is missing. His research files have been deleted. Five people had reasons to hate him.",
                    rightX + 24,
                    cy,
                    rightW - 48,
                    20
            );

            Theme.label(
                    g,
                    "YOUR INVESTIGATION",
                    rightX + 24,
                    cy + 18,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.wrap(
                    g,
                    "Search the campus. Interview witnesses. Interrogate suspects. Connect clues. Rebuild the timeline. Then make an accusation you can defend.",
                    rightX + 24,
                    cy + 47,
                    rightW - 48,
                    20
            );

            int stripY =
                    briefingY + briefingH + 14;

            int gap = 12;

            int tileW =
                    (rightW - gap) / 2;

            infoTile(
                    g,
                    rightX,
                    stripY,
                    tileW,
                    82,
                    "VICTIM",
                    "Nitheesh Reedy",
                    "22  •  Computer Engineering"
            );

            infoTile(
                    g,
                    rightX + tileW + gap,
                    stripY,
                    tileW,
                    82,
                    "CRIME SCENE",
                    "Basement Parking",
                    "CCTV gap  •  Missing phone"
            );

            addButton(
                    g,
                    "BEGIN CASE",
                    rightX,
                    stripY + 96,
                    205,
                    42,
                    "CASE"
            );
        }

        // ========================================================
        // CASE
        // ========================================================

        private void drawCase(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "Case file",
                    "The Last Night at MIT University",
                    28,
                    92
            );

            int top = 145;
            int bottom = h - 24;

            int leftW =
                    Math.min(
                            340,
                            (int) (w * 0.28)
                    );

            int cardH =
                    bottom - top;

            Theme.card(
                    g,
                    28,
                    top,
                    leftW,
                    cardH
            );

            int imageH =
                    Math.min(
                            215,
                            cardH - 190
                    );

            Theme.imageCover(
                    g,
                    nitheesh,
                    44,
                    top + 16,
                    leftW - 32,
                    imageH
            );

            int textY =
                    top + imageH + 48;

            Theme.label(
                    g,
                    "NITHEESH REEDY",
                    44,
                    textY,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.label(
                    g,
                    "22  •  COMPUTER ENGINEERING",
                    44,
                    textY + 22,
                    Theme.SMALL,
                    Theme.MUTED
            );

            Theme.badge(
                    g,
                    "VICTIM",
                    44,
                    textY + 36,
                    72,
                    Theme.RED
            );

            Theme.wrap(
                    g,
                    "Brilliant programmer and researcher. Recently became secretive about a project he believed could prove serious irregularities.",
                    44,
                    textY + 76,
                    leftW - 32,
                    18
            );

            int rightX =
                    28 + leftW + 20;

            int rightW =
                    w - rightX - 28;

            Theme.panel(
                    g,
                    rightX,
                    top,
                    rightW,
                    cardH
            );

            Theme.label(
                    g,
                    "DETECTIVE JOURNAL",
                    rightX + 22,
                    top + 32,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.label(
                    g,
                    "IMRAN KHAN",
                    rightX + 22,
                    top + 60,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.divider(
                    g,
                    rightX + 22,
                    top + 76,
                    rightW - 44
            );

            int yy = top + 108;

            for (String line : game.getJournal()) {

                g.setFont(Theme.BODY);
                g.setColor(Theme.TEXT);

                yy = Theme.wrap(
                        g,
                        "•  " + line,
                        rightX + 24,
                        yy,
                        rightW - 48,
                        20
                ) + 7;

                if (yy > top + cardH - 24) {
                    break;
                }
            }
        }

        // ========================================================
        // SUSPECTS
        // ========================================================

        private void drawSuspects(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "People of interest",
                    "Five suspects. Five different motives.",
                    28,
                    92
            );

            int top = 145;

            int gap = 10;

            int cw =
                    (w - 56 - gap * 4) / 5;

            int ch = 310;

            Suspect[] suspects =
                    game.getSuspects()
                            .toArray(new Suspect[0]);

            Image[] images = {
                    manjunath,
                    om,
                    aaradhya,
                    maniappa,
                    yashwanth
            };

            for (int i = 0;
                 i < suspects.length && i < images.length;
                 i++) {

                Suspect s = suspects[i];

                int x =
                        28 + i * (cw + gap);

                boolean selected =
                        s.getName().equals(selectedSuspect);

                if (selected) {
                    Theme.selectedCard(
                            g,
                            x,
                            top,
                            cw,
                            ch
                    );
                } else {
                    Theme.card(
                            g,
                            x,
                            top,
                            cw,
                            ch
                    );
                }

                Theme.imageCover(
                        g,
                        images[i],
                        x + 9,
                        top + 9,
                        cw - 18,
                        122
                );

                Theme.label(
                        g,
                        s.getName(),
                        x + 11,
                        top + 153,
                        Theme.H2,
                        Theme.TEXT
                );

                Theme.label(
                        g,
                        s.getRole(),
                        x + 11,
                        top + 174,
                        Theme.SMALL,
                        Theme.MUTED
                );

                Theme.label(
                        g,
                        "MOTIVE",
                        x + 11,
                        top + 202,
                        Theme.SMALL_BOLD,
                        Theme.GOLD
                );

                g.setFont(Theme.SMALL);
                g.setColor(Theme.TEXT);

                Theme.wrap(
                        g,
                        s.getMotive(),
                        x + 11,
                        top + 224,
                        cw - 22,
                        15
                );

                addButton(
                        g,
                        s.isInterviewed()
                                ? "INTERVIEW AGAIN"
                                : "INTERVIEW",
                        x + 11,
                        top + ch - 47,
                        cw - 22,
                        34,
                        "INTERVIEW:" + s.getName()
                );
            }

            int panelY =
                    top + ch + 14;

            int panelH =
                    Math.max(
                            150,
                            h - panelY - 20
                    );

            Theme.panel(
                    g,
                    28,
                    panelY,
                    w - 56,
                    panelH
            );

            Suspect selected =
                    findSuspect(selectedSuspect);

            if (selected == null) {
                return;
            }

            Theme.label(
                    g,
                    "INTERROGATION ROOM",
                    48,
                    panelY + 29,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.label(
                    g,
                    selected.getName(),
                    48,
                    panelY + 55,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.label(
                    g,
                    selected.getRole(),
                    48,
                    panelY + 75,
                    Theme.SMALL,
                    Theme.MUTED
            );

            int yy = panelY + 102;

            for (String line :
                    selected.getDialogue()) {

                Theme.label(
                        g,
                        "SUSPECT",
                        48,
                        yy,
                        Theme.SMALL_BOLD,
                        Theme.MUTED
                );

                g.setFont(Theme.BODY);
                g.setColor(Theme.TEXT);

                yy =
                        Theme.wrap(
                                g,
                                "\"" + line + "\"",
                                120,
                                yy,
                                w - 168,
                                18
                        ) + 7;

                if (yy >
                        panelY + panelH - 18) {
                    break;
                }
            }
        }

        // ========================================================
        // WITNESSES
        // ========================================================

        private void drawWitnesses(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "Witness room",
                    "Two people saw part of the final night.",
                    28,
                    92
            );

            Witness[] witnesses =
                    game.getWitnesses()
                            .toArray(new Witness[0]);

            Image[] images = {
                    ramesh,
                    rahul
            };

            int top = 145;
            int gap = 14;

            int cw =
                    (w - 56 - gap) / 2;

            int ch =
                    Math.max(
                            390,
                            h - top - 24
                    );

            for (int i = 0;
                 i < witnesses.length && i < 2;
                 i++) {

                int x =
                        28 + i * (cw + gap);

                Witness witness =
                        witnesses[i];

                Theme.card(
                        g,
                        x,
                        top,
                        cw,
                        ch
                );

                int imageW =
                        Math.min(
                                180,
                                cw / 3
                        );

                Theme.imageCover(
                        g,
                        images[i],
                        x + 16,
                        top + 16,
                        imageW,
                        220
                );

                int textX =
                        x + imageW + 28;

                int textW =
                        cw - imageW - 44;

                Theme.label(
                        g,
                        witness.getName(),
                        textX,
                        top + 45,
                        Theme.H1,
                        Theme.TEXT
                );

                Theme.label(
                        g,
                        witness.getRole(),
                        textX,
                        top + 69,
                        Theme.SMALL,
                        Theme.MUTED
                );

                Theme.badge(
                        g,
                        witness.isInterviewed()
                                ? "INTERVIEWED"
                                : "NOT INTERVIEWED",
                        textX,
                        top + 84,
                        112,
                        witness.isInterviewed()
                                ? Theme.GREEN
                                : Theme.GOLD
                );

                Theme.label(
                        g,
                        "STATEMENT",
                        textX,
                        top + 136,
                        Theme.SMALL_BOLD,
                        Theme.GOLD
                );

                int yy =
                        top + 166;

                if (!witness.isInterviewed()) {

                    Theme.wrap(
                            g,
                            "The witness statement is locked until you conduct the interview.",
                            textX,
                            yy,
                            textW,
                            18
                    );

                } else {

                    for (String statement :
                            witness.getStatements()) {

                        g.setColor(Theme.TEXT);
                        g.setFont(Theme.BODY);

                        yy =
                                Theme.wrap(
                                        g,
                                        "•  " + statement,
                                        textX,
                                        yy,
                                        textW,
                                        19
                                ) + 8;
                    }
                }

                addButton(
                        g,
                        witness.isInterviewed()
                                ? "QUESTION AGAIN"
                                : "INTERVIEW WITNESS",
                        x + 16,
                        top + ch - 50,
                        cw - 32,
                        38,
                        "WITNESS:" + i
                );
            }
        }

        // ========================================================
        // EXPLORE
        // ========================================================

        private void drawExplore(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "Campus investigation",
                    "Search locations for physical clues.",
                    28,
                    92
            );

            int top = 145;

            int leftW =
                    Math.min(
                            380,
                            (int) (w * 0.30)
                    );

            int panelH =
                    h - top - 24;

            Theme.panel(
                    g,
                    28,
                    top,
                    leftW,
                    panelH
            );

            Theme.label(
                    g,
                    "LOCATIONS",
                    48,
                    top + 30,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            List<Location> locations =
                    game.getLocations();

            int bw = 154;
            int bh = 44;
            int gx = 10;
            int gy = 9;

            int bx = 46;
            int by = top + 52;

            for (int i = 0;
                 i < locations.size();
                 i++) {

                int col = i % 2;
                int row = i / 2;

                addButton(
                        g,
                        locations.get(i).getName(),
                        bx + col * (bw + gx),
                        by + row * (bh + gy),
                        bw,
                        bh,
                        "LOCATION:" + i
                );
            }

            Theme.badge(
                    g,
                    searchedLocations()
                            + " / "
                            + locations.size()
                            + " LOCATIONS SEARCHED",
                    46,
                    top + panelH - 38,
                    Math.min(
                            240,
                            leftW - 36
                    ),
                    Theme.BLUE
            );

            int rightX =
                    28 + leftW + 20;

            int rightW =
                    w - rightX - 28;

            Theme.panel(
                    g,
                    rightX,
                    top,
                    rightW,
                    panelH
            );

            Theme.label(
                    g,
                    "FIELD NOTES",
                    rightX + 24,
                    top + 32,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.label(
                    g,
                    "SELECTED LOCATION",
                    rightX + 24,
                    top + 59,
                    Theme.SMALL,
                    Theme.MUTED
            );

            Theme.label(
                    g,
                    selectedLocationName,
                    rightX + 24,
                    top + 88,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.divider(
                    g,
                    rightX + 24,
                    top + 105,
                    rightW - 48
            );

            g.setFont(Theme.BODY);
            g.setColor(Theme.TEXT);

            Theme.wrap(
                    g,
                    locationResult,
                    rightX + 24,
                    top + 140,
                    rightW - 48,
                    21
            );

            Theme.badge(
                    g,
                    "EVIDENCE UPDATES AUTOMATICALLY",
                    rightX + 24,
                    top + panelH - 38,
                    225,
                    Theme.BLUE
            );
        }

        // ========================================================
        // EVIDENCE
        // ========================================================

        private void drawEvidence(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "Evidence locker",
                    "Every discovered clue stays in the case file.",
                    28,
                    92
            );

            List<String> evidence =
                    new ArrayList<>(
                            game.getEvidence()
                    );

            int top = 145;

            if (evidence.isEmpty()) {

                Theme.panel(
                        g,
                        28,
                        top,
                        w - 56,
                        155
                );

                Theme.label(
                        g,
                        "LOCKER EMPTY",
                        52,
                        top + 38,
                        Theme.SMALL_BOLD,
                        Theme.GOLD
                );

                Theme.wrap(
                        g,
                        "Explore the campus or interview witnesses to collect evidence.",
                        52,
                        top + 75,
                        w - 104,
                        20
                );

                return;
            }

            int gap = 12;

            int cw =
                    (w - 56 - gap) / 2;

            int ch = 98;

            for (int i = 0;
                 i < evidence.size();
                 i++) {

                int col = i % 2;
                int row = i / 2;

                int x =
                        28 + col * (cw + gap);

                int y =
                        top + row * (ch + gap);

                Theme.card(
                        g,
                        x,
                        y,
                        cw,
                        ch
                );

                Theme.badge(
                        g,
                        "CLUE "
                                + String.format(
                                "%02d",
                                i + 1
                        ),
                        x + 14,
                        y + 13,
                        68,
                        Theme.GOLD
                );

                g.setFont(Theme.BODY_BOLD);
                g.setColor(Theme.TEXT);

                Theme.wrap(
                        g,
                        evidence.get(i),
                        x + 96,
                        y + 34,
                        cw - 112,
                        18
                );
            }
        }

        // ========================================================
        // TIMELINE
        // ========================================================

        private void drawTimeline(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "Timeline",
                    "Reconstruct Nitheesh's final night.",
                    28,
                    92
            );

            int top = 145;

            int lineX = 76;

            g.setColor(Theme.BORDER);
            g.fillRect(
                    lineX,
                    top + 12,
                    2,
                    Math.max(
                            100,
                            h - top - 42
                    )
            );

            int y = top;

            for (TimelineEvent event :
                    game.getTimeline()) {

                g.setColor(Theme.GOLD);

                g.fillOval(
                        lineX - 7,
                        y + 17,
                        14,
                        14
                );

                Theme.card(
                        g,
                        108,
                        y,
                        w - 136,
                        58
                );

                Theme.label(
                        g,
                        event.getTime(),
                        128,
                        y + 36,
                        Theme.H2,
                        Theme.GOLD
                );

                Theme.wrap(
                        g,
                        event.getEvent(),
                        245,
                        y + 35,
                        w - 270,
                        18
                );

                y += 70;
            }
        }

        // ========================================================
        // DEDUCTION
        // ========================================================

        private void drawDeduction(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "Deduction board",
                    "Turn separate clues into a coherent theory.",
                    28,
                    92
            );

            int top = 145;
            int bottom = h - 24;

            int gap = 14;

            int leftW =
                    Math.min(
                            300,
                            (int) (w * 0.23)
                    );

            int rightW =
                    Math.min(
                            280,
                            (int) (w * 0.22)
                    );

            int centerX =
                    28 + leftW + gap;

            int centerW =
                    w
                            - 56
                            - leftW
                            - rightW
                            - gap * 2;

            int panelH =
                    bottom - top;

            // LEFT: DISCOVERED CLUES

            Theme.panel(
                    g,
                    28,
                    top,
                    leftW,
                    panelH
            );

            Theme.label(
                    g,
                    "DISCOVERED CLUES",
                    48,
                    top + 30,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            List<String> evidence =
                    new ArrayList<>(game.getEvidence());

            if (evidence.isEmpty()) {

                Theme.wrap(
                        g,
                        "No clues discovered yet. Explore locations and interview people.",
                        48,
                        top + 70,
                        leftW - 40,
                        18
                );

            } else {

                int yy = top + 56;

                for (String clue : evidence) {

                    Theme.card(
                            g,
                            44,
                            yy,
                            leftW - 32,
                            54
                    );

                    Theme.wrap(
                            g,
                            clue,
                            56,
                            yy + 21,
                            leftW - 56,
                            15
                    );

                    yy += 62;

                    if (yy >
                            top + panelH - 60) {
                        break;
                    }
                }
            }

            // CENTER: CONNECTIONS

            Theme.panel(
                    g,
                    centerX,
                    top,
                    centerW,
                    panelH
            );

            Theme.label(
                    g,
                    "CONNECTIONS",
                    centerX + 20,
                    top + 30,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            int yy = top + 56;

            for (String connection :
                    game.getConnections()) {

                Theme.card(
                        g,
                        centerX + 18,
                        yy,
                        centerW - 36,
                        48
                );

                Theme.label(
                        g,
                        "→",
                        centerX + 32,
                        yy + 30,
                        Theme.H2,
                        Theme.GOLD
                );

                g.setFont(Theme.BODY_BOLD);
                g.setColor(Theme.TEXT);

                Theme.wrap(
                        g,
                        connection,
                        centerX + 60,
                        yy + 29,
                        centerW - 78,
                        17
                );

                yy += 56;
            }

            int buttonW =
                    Math.max(
                            120,
                            (centerW - 50) / 2
                    );

            int buttonsY =
                    bottom - 96;

            addButton(
                    g,
                    "RESEARCH → PASSWORD",
                    centerX + 18,
                    buttonsY,
                    buttonW,
                    36,
                    "CONNECT:0"
            );

            addButton(
                    g,
                    "THREAT → OM",
                    centerX + 30 + buttonW,
                    buttonsY,
                    buttonW,
                    36,
                    "CONNECT:1"
            );

            addButton(
                    g,
                    "CCTV → BASEMENT",
                    centerX + 18,
                    buttonsY + 45,
                    buttonW,
                    36,
                    "CONNECT:2"
            );

            addButton(
                    g,
                    "ACCESS → MANIAPPA",
                    centerX + 30 + buttonW,
                    buttonsY + 45,
                    buttonW,
                    36,
                    "CONNECT:3"
            );

            // RIGHT: THEORY

            int rightX =
                    centerX + centerW + gap;

            Theme.panel(
                    g,
                    rightX,
                    top,
                    rightW,
                    panelH
            );

            Theme.label(
                    g,
                    "CURRENT THEORY",
                    rightX + 20,
                    top + 30,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.divider(
                    g,
                    rightX + 20,
                    top + 48,
                    rightW - 40
            );

            g.setFont(Theme.BODY);
            g.setColor(Theme.TEXT);

            Theme.wrap(
                    g,
                    game.getTheory(),
                    rightX + 20,
                    top + 80,
                    rightW - 40,
                    20
            );

            Theme.badge(
                    g,
                    game.getConnections().isEmpty()
                            ? "BUILD YOUR THEORY"
                            : "THEORY DEVELOPING",
                    rightX + 20,
                    bottom - 42,
                    rightW - 40,
                    game.getConnections().isEmpty()
                            ? Theme.GOLD
                            : Theme.GREEN
            );
        }

        // ========================================================
        // ACCUSATION REVIEW
        // ========================================================

        private void drawAccuse(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "Final accusation",
                    "Review the case before you submit.",
                    28,
                    92
            );

            int top = 145;
            int bottom = h - 20;

            int leftW =
                    Math.min(
                            315,
                            (int) (w * 0.25)
                    );

            int cardH =
                    bottom - top;

            Theme.card(
                    g,
                    28,
                    top,
                    leftW,
                    cardH
            );

            int imageH =
                    Math.min(
                            260,
                            cardH - 180
                    );

            Theme.imageCover(
                    g,
                    imran,
                    44,
                    top + 16,
                    leftW - 32,
                    imageH
            );

            int textY =
                    top + imageH + 48;

            Theme.label(
                    g,
                    "DETECTIVE IMRAN KHAN",
                    44,
                    textY,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.label(
                    g,
                    "FINAL REVIEW",
                    44,
                    textY + 23,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.wrap(
                    g,
                    "Your conclusion should explain motive, opportunity, evidence and why the other suspects are less likely.",
                    44,
                    textY + 55,
                    leftW - 32,
                    18
            );

            int rightX =
                    28 + leftW + 20;

            int rightW =
                    w - rightX - 28;

            Theme.panel(
                    g,
                    rightX,
                    top,
                    rightW,
                    cardH
            );

            Theme.label(
                    g,
                    "CASE READINESS",
                    rightX + 24,
                    top + 32,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            int rowY =
                    top + 60;

            statRow(
                    g,
                    rightX + 24,
                    rowY,
                    "Evidence collected",
                    String.valueOf(
                            game.getEvidence().size()
                    ),
                    Theme.BLUE,
                    rightW - 48
            );

            statRow(
                    g,
                    rightX + 24,
                    rowY + 46,
                    "Evidence connections",
                    String.valueOf(
                            game.getConnections().size()
                    ),
                    Theme.GOLD,
                    rightW - 48
            );

            statRow(
                    g,
                    rightX + 24,
                    rowY + 92,
                    "Locations searched",
                    String.valueOf(
                            searchedLocations()
                    ),
                    Theme.GREEN,
                    rightW - 48
            );

            statRow(
                    g,
                    rightX + 24,
                    rowY + 138,
                    "Suspects interviewed",
                    String.valueOf(
                            interviewedSuspects()
                    ),
                    Theme.RED,
                    rightW - 48
            );

            Theme.label(
                    g,
                    "SUBMIT BELOW",
                    rightX + 24,
                    rowY + 194,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.wrap(
                    g,
                    "Choose the suspect and write your reasoning in the accusation panel below.",
                    rightX + 24,
                    rowY + 225,
                    rightW - 48,
                    19
            );
        }

        // ========================================================
        // ENDING
        // ========================================================

        private void drawEnding(
                Graphics2D g,
                int w,
                int h
        ) {

            Theme.section(
                    g,
                    "Case conclusion",
                    "CASEFILE 47",
                    28,
                    92
            );

            int top = 145;
            int bottom = h - 20;

            int leftW = 300;

            int cardH =
                    bottom - top;

            Theme.card(
                    g,
                    28,
                    top,
                    leftW,
                    cardH
            );

            Theme.imageCover(
                    g,
                    imran,
                    44,
                    top + 16,
                    leftW - 32,
                    Math.min(
                            270,
                            cardH - 150
                    )
            );

            Theme.label(
                    g,
                    "IMRAN KHAN",
                    44,
                    top + 312,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.label(
                    g,
                    "CASE CLOSED / REVIEW",
                    44,
                    top + 336,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            int rightX =
                    28 + leftW + 20;

            int rightW =
                    w - rightX - 28;

            Theme.panel(
                    g,
                    rightX,
                    top,
                    rightW,
                    cardH
            );

            Theme.label(
                    g,
                    game.getEndingTitle(),
                    rightX + 24,
                    top + 40,
                    Theme.H1,
                    Theme.GOLD
            );

            Theme.divider(
                    g,
                    rightX + 24,
                    top + 62,
                    rightW - 48
            );

            g.setFont(Theme.BODY);
            g.setColor(Theme.TEXT);

            Theme.wrap(
                    g,
                    game.getEndingText(),
                    rightX + 24,
                    top + 98,
                    rightW - 48,
                    21
            );

            int buttonY =
                    bottom - 44;

            addButton(
                    g,
                    "PLAY CASE AGAIN",
                    rightX + 24,
                    buttonY,
                    190,
                    40,
                    "RESET"
            );

            addButton(
                    g,
                    "RETURN TO CASE",
                    rightX + 226,
                    buttonY,
                    190,
                    40,
                    "CASE"
            );
        }

        // ========================================================
        // HELPERS
        // ========================================================

        private void infoTile(
                Graphics2D g,
                int x,
                int y,
                int w,
                int h,
                String label,
                String value,
                String sub
        ) {

            Theme.card(
                    g,
                    x,
                    y,
                    w,
                    h
            );

            Theme.label(
                    g,
                    label,
                    x + 14,
                    y + 22,
                    Theme.SMALL_BOLD,
                    Theme.GOLD
            );

            Theme.label(
                    g,
                    value,
                    x + 14,
                    y + 48,
                    Theme.H2,
                    Theme.TEXT
            );

            Theme.label(
                    g,
                    sub,
                    x + 14,
                    y + 69,
                    Theme.SMALL,
                    Theme.MUTED
            );
        }

        private void statRow(
                Graphics2D g,
                int x,
                int y,
                String label,
                String value,
                Color accent,
                int rowW
        ) {

            Theme.card(
                    g,
                    x,
                    y,
                    rowW,
                    38
            );

            Theme.label(
                    g,
                    label,
                    x + 12,
                    y + 25,
                    Theme.BODY,
                    Theme.TEXT
            );

            g.setFont(Theme.BODY_BOLD);

            FontMetrics fm =
                    g.getFontMetrics();

            int valueX =
                    x
                            + rowW
                            - 14
                            - fm.stringWidth(value);

            Theme.label(
                    g,
                    value,
                    valueX,
                    y + 25,
                    Theme.BODY_BOLD,
                    accent
            );
        }

        private void addButton(
                Graphics2D g,
                String label,
                int x,
                int y,
                int w,
                int h,
                String action
        ) {

            int index =
                    buttons.size();

            boolean active =
                    action.equals(screen);

            boolean hovered =
                    index == hoverButton;

            Theme.button(
                    g,
                    x,
                    y,
                    w,
                    h,
                    label,
                    active,
                    hovered
            );

            buttons.add(
                    new RectButton(
                            x,
                            y,
                            w,
                            h,
                            action
                    )
            );
        }

        private int searchedLocations() {

            int count = 0;

            for (Location location :
                    game.getLocations()) {

                if (location.isSearched()) {
                    count++;
                }
            }

            return count;
        }

        private int interviewedSuspects() {

            int count = 0;

            for (Suspect suspect :
                    game.getSuspects()) {

                if (suspect.isInterviewed()) {
                    count++;
                }
            }

            return count;
        }

        private Suspect findSuspect(
                String name
        ) {

            for (Suspect suspect :
                    game.getSuspects()) {

                if (suspect.getName().equals(name)) {
                    return suspect;
                }
            }

            return null;
        }

        // ========================================================
        // EVENT HANDLING
        // ========================================================

        private void handle(String action) {

            message = "";

            if (action.equals("CASE")) {
                navigate("CASE");
                return;
            }

            if (action.equals("RESET")) {

                game.reset();

                locationResult =
                        "Select a location to investigate it.";

                selectedLocationName =
                        "Investigation notes";

                selectedSuspect =
                        "Manjunath S";

                navigate("CASE");

                return;
            }

            if (action.equals("EXPLORE")) {
                navigate("EXPLORE");
                return;
            }

            if (action.equals("EVIDENCE")) {
                navigate("EVIDENCE");
                return;
            }

            if (action.equals("TIMELINE")) {
                navigate("TIMELINE");
                return;
            }

            if (action.equals("DEDUCTION")) {
                navigate("DEDUCTION");
                return;
            }

            if (action.equals("ACCUSE")) {
                navigate("ACCUSE");
                return;
            }

            if (action.equals("SUSPECTS")) {
                navigate("SUSPECTS");
                return;
            }

            if (action.equals("WITNESSES")) {
                navigate("WITNESSES");
                return;
            }

            if (action.startsWith("INTERVIEW:")) {

                selectedSuspect =
                        action.substring(10);

                Suspect suspect =
                        findSuspect(selectedSuspect);

                if (suspect != null) {
                    game.interview(suspect);
                }

                repaint();

                return;
            }

            if (action.startsWith("WITNESS:")) {

                int index =
                        Integer.parseInt(
                                action.substring(8)
                        );

                if (
                        index >= 0 &&
                                index < game.getWitnesses().size()
                ) {

                    Witness witness =
                            game.getWitnesses()
                                    .get(index);

                    game.interview(witness);

                    if (index == 0) {

                        game.addEvidence(
                                "Witness saw person near basement"
                        );

                        game.addEvidence(
                                "Dark clothing description"
                        );

                    } else {

                        game.addEvidence(
                                "Rahul found the body"
                        );
                    }
                }

                repaint();

                return;
            }

            if (action.startsWith("LOCATION:")) {

                int index =
                        Integer.parseInt(
                                action.substring(9)
                        );

                if (
                        index >= 0 &&
                                index < game.getLocations().size()
                ) {

                    Location location =
                            game.getLocations()
                                    .get(index);

                    locationResult =
                            game.explore(location);

                    selectedLocationName =
                            location.getName();
                }

                repaint();

                return;
            }

            if (action.startsWith("CONNECT:")) {

                int index =
                        Integer.parseInt(
                                action.substring(8)
                        );

                switch (index) {

                    case 0:
                        game.connect(
                                "Deleted research file → Password access"
                        );
                        break;

                    case 1:
                        game.connect(
                                "Threatening message → Om's motive"
                        );
                        break;

                    case 2:
                        game.connect(
                                "CCTV timestamp → Basement movement"
                        );
                        break;

                    case 3:
                        game.connect(
                                "Hostel access anomaly → Maniappa"
                        );
                        break;
                }

                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

            for (RectButton button :
                    buttons) {

                if (button.contains(
                        e.getX(),
                        e.getY()
                )) {

                    handle(button.action);
                    return;
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

            int old =
                    hoverButton;

            hoverButton = -1;

            for (int i = 0;
                 i < buttons.size();
                 i++) {

                if (buttons.get(i).contains(
                        e.getX(),
                        e.getY()
                )) {

                    hoverButton = i;
                    break;
                }
            }

            if (old != hoverButton) {
                repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {

            hoverButton = -1;
            repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

            hoverButton = -1;
        }

        @Override
        public void mouseDragged(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        private class RectButton {

            final int x;
            final int y;
            final int w;
            final int h;
            final String action;

            RectButton(
                    int x,
                    int y,
                    int w,
                    int h,
                    String action
            ) {

                this.x = x;
                this.y = y;
                this.w = w;
                this.h = h;
                this.action = action;
            }

            boolean contains(
                    int px,
                    int py
            ) {

                return px >= x
                        && px <= x + w
                        && py >= y
                        && py <= y + h;
            }
        }
    }
}