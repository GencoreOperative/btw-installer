package uk.co.gencoreoperative.btw.ui;

import static uk.co.gencoreoperative.btw.ui.Strings.TITLE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import uk.co.gencoreoperative.btw.Command;
import uk.co.gencoreoperative.btw.Main;

public class Progress extends JDialog implements Observer {
    private final DefaultListModel<Command> model = new DefaultListModel<>();

    private final Action closeAction = new AbstractAction() {
        {
            putValue(Action.NAME, Strings.BUTTON_CLOSE.getText());
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Progress.this.setVisible(false);
            Progress.this.dispose();
            System.exit(0);
        }
    };
    private final Action patchAction = new AbstractAction() {
        {
            putValue(Action.NAME, Strings.BUTTON_PATCH.getText());
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            main.start();
        }
    };
    private Main main;

    public Progress(Main main) {
        this.main = main;
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAction.actionPerformed(null);
            }
        });

        setTitle(TITLE.getText());

        setLayout(new BorderLayout());
        add(centerLayout(), BorderLayout.CENTER);
        add(getBottomLayout(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JComponent centerLayout() {
        JList<Command> list = new JList<>(model);
        list.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> render(value));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize((new Dimension(300, 150)));
        return scrollPane;
    }

    private JComponent getBottomLayout() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JButton(patchAction));
        panel.add(new JButton(closeAction));
        return panel;
    }

    public void addItem(Command command) {
        command.addObserver(this);
        model.addElement(command);
    }

    @Override
    public void update(Observable o, Object arg) {
        validate();
        repaint();

        boolean complete = true;
        boolean failed = false;
        for (int ii = 0; ii < model.getSize(); ii++) {
            Command command = model.getElementAt(ii);
            complete = complete && command.isProcessed() && command.isSuccessful();
            failed = failed || command.isProcessed() && !command.isSuccessful();
        }
        if (complete || failed) {
            patchAction.setEnabled(false);
        }
    }

    /**
     * Given an {@link Command}, render this to a JLabel suitable for display to the user.
     *
     * @param command Non null Command to render.
     */
    private JLabel render(Command command) {
        final Icons icon;
        if (command.isProcessed()) {
            if (command.isSuccessful()) {
                icon = Icons.TICK;
            } else {
                icon = Icons.ERROR;
            }
        } else {
            icon = Icons.QUESTION;
        }
        return new JLabel(command.getDescription(), icon.getIcon(), SwingConstants.LEADING);
    }

}
