package uk.co.gencoreoperative.btw.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class Progress extends JDialog implements Observer {
    private final DefaultListModel<Item> model;
    private final ImageIcon question;
    private final ImageIcon tick;
    private final ImageIcon cross;

    public Progress() {
        try {
            question = new ImageIcon(ImageIO.read(Process.class.getResource("/black-question-mark-ornament_2753.png")));
            tick = new ImageIcon(ImageIO.read(Process.class.getResource("/white-heavy-check-mark_2705.png")));
            cross = new ImageIcon(ImageIO.read(Process.class.getResource("/cross-mark_274c.png")));
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

    @Override
    public void update(Observable o, Object arg) {
        validate();
        repaint();
    }

    private void render(JLabel label, Item item) {
        label.setText(item.getDescription());
        ImageIcon icon;
        if (!item.isProcessed()) {
            icon = question;
        } else if (item.isSuccessful()) {
            icon = tick;
        } else {
            icon = cross;
        }
        label.setIcon(icon);
    }

}
