package uk.co.gencoreoperative.btw.ui;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTreeViewer {
    JFrame frame = new JFrame();
    JDialog dialog = new JDialog(frame, true);

    private String pathString = Strings.NOT_RECOGNISED.getText();

    public void view(ZipFile zipFile) throws IOException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("/");
        constructTree(zipFile, root);

        JTree tree = new JTree(root);
        tree.setShowsRootHandles(true);
        dialog.add(new JScrollPane(tree));

        JPanel panel = new JPanel();

        // Select button.
        JButton selectZIPFolder = new JButton(Strings.BUTTON_DIALOG_SELECT.getText());
        selectZIPFolder.setToolTipText(Strings.TOOLTIP_SELECT_ZIP.getText());
        selectZIPFolder.setIcon(Icons.ACCEPT.getIcon());
        selectZIPFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        selectZIPFolder.setEnabled(false);
        panel.add(selectZIPFolder);

        // Cancel button.
        JButton cancelButton = new JButton(Strings.BUTTON_CANCEL.getText());
        cancelButton.setToolTipText(Strings.TOOLTIP_CANCEL.getText());
        cancelButton.setIcon(Icons.DELETE.getIcon());
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                pathString = Strings.NOT_RECOGNISED.getText();
            }
        });
        panel.add(cancelButton);

        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                pathString = constructPath(selectedNode);
                selectZIPFolder.setEnabled(!pathString.equals(Strings.NOT_RECOGNISED.getText()));
            }
        });

        dialog.add(panel, BorderLayout.SOUTH);

        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Select the folder to import");
        dialog.setSize(512, 420);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public String getPathString() {
        return pathString;
    }

    private void constructTree(ZipFile zipFile, DefaultMutableTreeNode root) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if (entry.isDirectory()) {
                String[] path = entry.getName().split("/");
                DefaultMutableTreeNode prevRoot = root;

                for (String filename : path) {
                    DefaultMutableTreeNode newNode = getChild(prevRoot, filename);
                    prevRoot.add(newNode);
                    prevRoot = newNode;
                }
            }
        }
        zipFile.close();
    }

    private DefaultMutableTreeNode getChild(DefaultMutableTreeNode root, String name) {
        Enumeration e = root.children();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
            if (name.contentEquals(child.toString())) {
                return child;
            }
        }
        return new DefaultMutableTreeNode(name);
    }

    private String constructPath(DefaultMutableTreeNode node) {
        StringBuilder nodePath = new StringBuilder();
        for (TreeNode o : node.getPath()) {
            nodePath.append(o.toString()).append("/");
        }
        return nodePath.substring(2);
    }
}
