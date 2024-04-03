/*
 * KeysPerSecond: An open source input statistics displayer.
 * Copyright (C) 2017  Roan Hofland (roan@roanh.dev).  All rights reserved.
 * GitHub Repository: https://github.com/RoanH/KeysPerSecond
 *
 * KeysPerSecond is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KeysPerSecond is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.ssalhub.lskv.ui.dialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import dev.ssalhub.lskv.Main;
import dev.ssalhub.lskv.config.ThemeColor;
import dev.ssalhub.lskv.config.group.ThemeSettings;
import dev.ssalhub.lskv.ui.component.ColorPicker;
import locallib.roanh.util.Dialog;

/**
 * Dialog used to configure custom theme colors.
 * 
 * @author Roan
 * @see ThemeSettings
 */
public class ColorDialog extends JPanel {
    /**
     * Serial ID.
     */
    private static final long serialVersionUID = -5867337735121395139L;

    /**
     * Creates a new color configuration dialog
     * 
     * @param config The configuration to update.
     * @param live   If the GUI is already live and should
     *               be updated in real time.
     */
    public ColorDialog(ThemeSettings config, boolean live) {
        super(new GridLayout(3, 3, 4, 2));

        // enabled
        JCheckBox enable = new JCheckBox("", config.hasCustomColors());
        enable.addActionListener(e -> {
            config.setCustomColorsEnabled(enable.isSelected());
            if (live) {
                Main.reconfigure();
            }
        });

        add(new JLabel("Enable custom colours: "));
        add(enable);
        add(new JLabel());

        // foreground
        JLabel lfg = new JLabel("Foreground colour: ");
        ColorPicker cfg = new ColorPicker(config::getCustomForeground, config::setForeground, live);

        JPanel spanelfg = new JPanel(new BorderLayout());
        JSpinner sfg = new JSpinner(
                new SpinnerNumberModel(config.getCustomForeground().getAlpha() * 100.0D, 0.0D, 100.0D, 5.0D));
        spanelfg.add(new JLabel("Opacity (%): "), BorderLayout.LINE_START);
        spanelfg.add(sfg, BorderLayout.CENTER);
        sfg.addChangeListener(e -> {
            config.setForeground(
                    new ThemeColor(config.getCustomForeground().getRGB(), (float) ((double) sfg.getValue() / 100.0D)));
            if (live) {
                Main.reconfigure();
            }
        });

        add(lfg);
        add(cfg);
        add(spanelfg);

        // background
        JLabel lbg = new JLabel("Background colour: ");
        ColorPicker cbg = new ColorPicker(config::getCustomBackground, config::setBackground, live);

        JPanel spanelbg = new JPanel(new BorderLayout());
        JSpinner sbg = new JSpinner(
                new SpinnerNumberModel(config.getCustomBackground().getAlpha() * 100.0D, 0.0D, 100.0D, 5.0D));
        spanelbg.add(new JLabel("Opacity (%): "), BorderLayout.LINE_START);
        spanelbg.add(sbg, BorderLayout.CENTER);
        sbg.addChangeListener(e -> {
            config.setBackground(
                    new ThemeColor(config.getCustomBackground().getRGB(), (float) ((double) sbg.getValue() / 100.0D)));
            if (live) {
                Main.reconfigure();
            }
        });

        add(lbg);
        add(cbg);
        add(spanelbg);
    }

    /**
     * Shows the color configuration dialog
     * 
     * @param config The configuration to update.
     * @param live   If the GUI is already live and should
     *               be updated in real time.
     */
    public static final void configureColors(ThemeSettings config, boolean live) {
        boolean enabled = config.hasCustomColors();
        ThemeColor foreground = config.getCustomForeground();
        ThemeColor background = config.getCustomBackground();
        if (!Dialog.showSaveDialog(new ColorDialog(config, live))) {
            config.setCustomColorsEnabled(enabled);
            config.setForeground(foreground);
            config.setBackground(background);
            if (live) {
                Main.reconfigure();
            }
        }
    }
}
