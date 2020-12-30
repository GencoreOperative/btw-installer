package uk.co.gencoreoperative.btw.ui.panels;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.actions.ChooseAddon;
import uk.co.gencoreoperative.btw.ui.actions.MoveAddonDown;
import uk.co.gencoreoperative.btw.ui.actions.MoveAddonUp;
import uk.co.gencoreoperative.btw.ui.actions.RemoveAddon;
import uk.co.gencoreoperative.btw.ui.signals.AddonFiles;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AddonsPanel extends JPanel {
    private final Context context;
    private final DialogFactory dialogFactory;

    private RemoveAddon removeAddon;
    private MoveAddonUp moveAddonUp;
    private MoveAddonDown moveAddonDown;

    public AddonsPanel(Context context, DialogFactory dialogFactory) {
        this.context = context;
        this.dialogFactory = dialogFactory;

        setBorder(new TitledBorder("Better Than Wolves Addons"));
        setLayout(new MigLayout("fill, wrap, insets 10",
                "",
                "grow"));

        add(manageAddonsPanel(), "grow, center");
        add(addonsScrollPane(), "grow, h 128:100%");
    }

    private JPanel manageAddonsPanel() {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0",
                "[grow][grow]"));

        // Row 1
        removeAddon = new RemoveAddon(context, dialogFactory);
        moveAddonUp = new MoveAddonUp(context, dialogFactory);
        moveAddonDown = new MoveAddonDown(context, dialogFactory);

        removeAddon.setEnabled(false);
        moveAddonUp.setEnabled(false);
        moveAddonDown.setEnabled(false);

        panel.add(new JButton(new ChooseAddon(context, dialogFactory)), "w 25%");
        panel.add(new JButton(removeAddon), "w 25%");
        panel.add(new JButton(moveAddonUp), "w 25%");
        panel.add(new JButton(moveAddonDown), "w 25%, wrap");

        return panel;
    }

    private JPanel addonsScrollPane() {
        JPanel panel = new JPanel(new MigLayout(
                "fill, insets 0",
                "[grow][grow]"));

        // Row 2
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> addonsList = new JList<>(listModel);

        context.register(AddonFiles.class, (o, arg) -> {
            listModel.clear();
            if (context.contains(AddonFiles.class)) {
                for (int i = 0; i < AddonFiles.getAddons().size(); i++) {
                    listModel.addElement(AddonFiles.getAddons().get(i).getName().replace(".zip", ""));
                }
            }
            if (addonsList.getSelectedIndex() >= 0) {
                AddonFiles.selectedIndex = addonsList.getSelectedIndex();
            } else {
                addonsList.setSelectedIndex(AddonFiles.selectedIndex);
            }
        });

        addonsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (addonsList.getSelectedIndex() >= 0) {
                    AddonFiles.selectedIndex = addonsList.getSelectedIndex();
                }
                removeAddon.setSelectedIndex(addonsList.getSelectedIndex());
                moveAddonUp.setSelectedIndex(addonsList.getSelectedIndex());
                moveAddonDown.setSelectedIndex(addonsList.getSelectedIndex());
            }
        });

        addonsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(addonsList), "w 256:100%, h 100%, span, center");

        return panel;
    }
}
