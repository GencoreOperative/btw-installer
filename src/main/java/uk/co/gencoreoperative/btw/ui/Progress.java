package uk.co.gencoreoperative.btw.ui;

import static uk.co.gencoreoperative.btw.ui.Strings.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;

import uk.co.gencoreoperative.btw.command.AbstractCommand;
import uk.co.gencoreoperative.btw.command.CommandManager;

public class Progress extends JDialog implements Observer {
    private final DialogFactory dialogFactory;
    private final DefaultListModel<AbstractCommand> model = new DefaultListModel<>();

    private final Action closeAction = new AbstractAction() {
        {
            putValue(Action.NAME, BUTTON_CLOSE.getText());
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Progress.this.setVisible(false);
            Progress.this.dispose();
            System.exit(0);
        }
    };

    /**
     * Allows the user to start the patching process.
     *
     * Note: Because the command framework does not yet support reset, we
     * will disable the button to prevent the user re-trying.
     */
    private final Action patchAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            processCommands();
            setEnabled(false);
        }
    };

    private Set<AbstractCommand> commands;
    private String successMessage;
    private String successTitle;

    public Progress() {
        dialogFactory = new DialogFactory(this);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAction.actionPerformed(null);
            }
        });

        getRootPane().registerKeyboardAction(
                closeAction,
                (KeyStroke) closeAction.getValue(Action.ACCELERATOR_KEY),
                JComponent.WHEN_FOCUSED);

        setLayout(new BorderLayout());
        add(centerLayout(), BorderLayout.CENTER);
        add(getBottomLayout(), BorderLayout.SOUTH);
    }

    private JComponent centerLayout() {
        JList<AbstractCommand> list = new JList<>(model);
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

    public void addItem(AbstractCommand command) {
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
            AbstractCommand command = model.getElementAt(ii);
            complete = complete && command.isSuccess();
            failed = failed || !command.isSuccess();
        }
        if (complete || failed) {
            patchAction.setEnabled(false);
        }
    }

    /**
     * Given an {@link AbstractCommand}, render this to a JLabel suitable for display to the user.
     *
     * The command has a textual description and a state which will be of interest for display.
     *
     * @param command Non null {@link AbstractCommand} to render.
     */
    private JLabel render(AbstractCommand command) {
        final Icons icon;
        if (command.isProcessed()) {
            if (command.isSuccess()) {
                icon = Icons.TICK;
            } else {
                icon = Icons.ERROR;
            }
        } else {
            icon = Icons.QUESTION;
        }
        return new JLabel(command.getDescription(), icon.getIcon(), SwingConstants.LEADING);
    }

    public void setCommands(Set<AbstractCommand> commands) {
        this.commands = commands;
        commands.forEach(this::addItem);
    }

    public void setMainAction(String mainAction) {
        patchAction.putValue(Action.NAME, mainAction);
    }

    private void processCommands() {
        // The commands are organised in a chain, with the last depending on all previous.
        CommandManager manager = new CommandManager();
        try {
            manager.process(commands);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Optional<AbstractCommand> cancelled = commands.stream()
                .filter(AbstractCommand::isProcessed)
                .filter(AbstractCommand::isCancelled)
                .findFirst();
        Optional<AbstractCommand> failed = commands.stream()
                .filter(AbstractCommand::isProcessed)
                .filter(    c -> !c.isSuccess()).findFirst();
        if (cancelled.isPresent()) {
            dialogFactory.information(CANCELLED_DETAIL.getText());
        } else if (failed.isPresent()) {
            dialogFactory.failed(failed.get());
        } else {
            dialogFactory.success(successTitle, successMessage);
        }
    }

    public void start() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setSuccessMessage(String title, String message) {
        this.successMessage = message;
        this.successTitle = title;
    }
}
