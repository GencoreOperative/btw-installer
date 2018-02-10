package uk.co.gencoreoperative.btw.ui;

import static java.text.MessageFormat.format;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.function.Predicate;

import uk.co.gencoreoperative.btw.command.AbstractCommand;

public class DialogFactory {

    private final JFrame parent;

    public DialogFactory(JFrame parent) {
        this.parent = parent;
    }

    public File requestFileLocation(Strings title, final File current, File defaultLocation, Predicate<File> selector) {
        File path = current;
        if (current == null) path = defaultLocation;

        JFileChooser chooser = getDefaultChooser(title.getText());
        chooser.setSelectedFile(path);
        chooser.ensureFileIsVisible(path);
        chooser.setFileFilter(getFilter("Zip Archives", selector));
        int result = chooser.showDialog(parent, Strings.BUTTON_DIALOG_SELECT.getText());
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public File requestFolderLocation(Strings title, final File current, File defaultLocation, Predicate<File> selector) {
        File path = current;
        if (current == null) path = defaultLocation;

        JFileChooser chooser = getDefaultChooser(title.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(path.getParentFile());
        chooser.setSelectedFile(path);
        chooser.ensureFileIsVisible(path);
        chooser.setFileFilter(getFilter("Folders", selector));
        int result = chooser.showDialog(parent, Strings.BUTTON_DIALOG_SELECT.getText());
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private FileFilter getFilter(final String description, final Predicate<File> selector) {
        return new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || selector.test(f);
            }

            @Override
            public String getDescription() {
                return description;
            }
        };
    }

    private JFileChooser getDefaultChooser(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        return chooser;
    }


    public void success(String title, String content) {
        JOptionPane.showMessageDialog(parent, content, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void failed(AbstractCommand command) {
        JOptionPane.showMessageDialog(parent,
                format(Strings.ERROR_DETAIL.getText(),
                        command.getDescription(),
                        command.getFailedReason()),
                Strings.ERROR_TITLE.getText(),
                JOptionPane.ERROR_MESSAGE);
    }

    public void failed(String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                Strings.ERROR_TITLE.getText(),
                JOptionPane.ERROR_MESSAGE);
    }

    public void information(String text) {
        JOptionPane.showMessageDialog(parent,
                text,
                Strings.INFORMATION_TITLE.getText(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirm(String message, String title) {
        int response = JOptionPane.showConfirmDialog(parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return response == JOptionPane.YES_OPTION;
    }

    public JFrame getParentFrame() {
        return parent;
    }
}
