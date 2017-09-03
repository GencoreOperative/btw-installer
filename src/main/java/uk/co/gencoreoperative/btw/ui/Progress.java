package uk.co.gencoreoperative.btw.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static java.text.MessageFormat.format;

public class Progress extends JDialog implements Observer {
    private final DefaultListModel<Item> model;
    private final Image question;

    public Progress() {
        try {
            question = ImageIO.read(Process.class.getResource("/black-question-mark-ornament_2753.png"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JList<Item> list = new JList<>();
        model = new DefaultListModel<>();
        list.setModel(model);
        list.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            render(label, value);
            return label;
        });
        list.setVisibleRowCount(-1);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize((new Dimension(300, 300)));

        add(scrollPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public void addItem(Item item) {
        item.addObserver(this);
        model.addElement(item);
    }

    public static void main(String... args) {
        Progress progress = new Progress();
        progress.addItem(new Item("badgers"));
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    private void render(JLabel label, Item item) {
        final String emoji;
        if (!item.isProcessed()) {
            emoji = "-";
        } else if (item.isSuccessful()) {
            emoji = "OK";
        } else {
            emoji = "FAIL";
        }
        label.setText(format("{0} {1}", emoji, item.getDescription()));
        ImageIcon icon = new ImageIcon(question);
        label.setIcon(icon);
    }

}
