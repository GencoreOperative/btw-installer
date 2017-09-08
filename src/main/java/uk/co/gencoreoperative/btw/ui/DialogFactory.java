/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw.ui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.function.Predicate;

public class DialogFactory {

    private final Component parent;

    public DialogFactory(Component parent) {
        this.parent = parent;
    }

    public void getSuccessDialog(String title, String content) {
        JOptionPane.showMessageDialog(parent, title, content, JOptionPane.PLAIN_MESSAGE);
    }

    public File requestFileLocation(Strings title, final File current, File defaultLocation, Predicate<File> selector) {
        File path = current;
        if (current == null) path = defaultLocation;

        JFileChooser chooser = getDefaultChooser(title.getText());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setSelectedFile(path);
        chooser.ensureFileIsVisible(path);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return selector.test(f);
            }

            @Override
            public String getDescription() {
                return "Zip Archives";
            }
        });
        int result = chooser.showDialog(parent, Strings.BUTTON_SELECT.getText());
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
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return selector.test(f);
            }

            @Override
            public String getDescription() {
                return "Folders";
            }
        });
        int result = chooser.showDialog(parent, Strings.BUTTON_SELECT.getText());
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private JFileChooser getDefaultChooser(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        return chooser;
    }
}
