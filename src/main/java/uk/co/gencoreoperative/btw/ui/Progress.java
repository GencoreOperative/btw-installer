package uk.co.gencoreoperative.btw.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import static java.text.MessageFormat.format;

public class Progress extends JDialog implements Observer {
    private final DefaultListModel<Item> model = new DefaultListModel<>();
    private final Action closeAction = new AbstractAction() {
        {
            putValue(Action.NAME, Strings.CLOSE.getText());
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Progress.this.setVisible(false);
            Progress.this.dispose();
            System.exit(0);
        }
    };

    public Progress() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAction.actionPerformed(null);
            }
        });

        setTitle(format("{0} - {1}", Strings.TITLE.getText(), Strings.VERSION.getText()));

        setLayout(new BorderLayout());
        add(centerLayout(), BorderLayout.CENTER);
        add(getBottomLayout(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JComponent centerLayout() {
        JList<Item> list = new JList<>(model);
        list.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> render(value));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize((new Dimension(300, 150)));
        return scrollPane;
    }

    private JComponent getBottomLayout() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JButton(closeAction));
        return panel;
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

    /**
     * Given an {@link Item}, render this to a JLabel suitable for display to the user.
     *
     * @param item Non null Item to render.
     */
    private JLabel render(Item item) {
        final Icons icon;
        if (item.isProcessed()) {
            if (item.isSuccessful()) {
                icon = Icons.TICK;
            } else {
                icon = Icons.ERROR;
            }
        } else {
            icon = Icons.QUESTION;
        }
        return new JLabel(item.getDescription(), icon.getIcon(), SwingConstants.LEADING);
    }

}
