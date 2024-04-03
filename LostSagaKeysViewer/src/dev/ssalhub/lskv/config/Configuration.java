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
package dev.ssalhub.lskv.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.ssalhub.lskv.Main;
import dev.ssalhub.lskv.config.group.AveragePanelSettings;
import dev.ssalhub.lskv.config.group.CommandSettings;
import dev.ssalhub.lskv.config.group.CurrentPanelSettings;
import dev.ssalhub.lskv.config.group.GraphPanelSettings;
import dev.ssalhub.lskv.config.group.LineGraphSettings;
import dev.ssalhub.lskv.config.group.KeyPanelSettings;
import dev.ssalhub.lskv.config.group.LayoutSettings;
import dev.ssalhub.lskv.config.group.MaxPanelSettings;
import dev.ssalhub.lskv.config.group.PositionSettings;
import dev.ssalhub.lskv.config.group.SpecialPanelSettings;
import dev.ssalhub.lskv.config.group.StatsSavingSettings;
import dev.ssalhub.lskv.config.group.ThemeSettings;
import dev.ssalhub.lskv.config.group.TotalPanelSettings;
import dev.ssalhub.lskv.config.legacy.LegacyCompatibleKeyConstructor;
import dev.ssalhub.lskv.config.legacy.LegacyPanelShowSetting;
import dev.ssalhub.lskv.config.legacy.ProxySetting;
import dev.ssalhub.lskv.config.setting.BooleanSetting;
import dev.ssalhub.lskv.config.setting.UpdateRateSetting;
import dev.ssalhub.lskv.layout.LayoutPosition;
import locallib.roanh.util.Dialog;
import locallib.roanh.util.FileSelector;
import locallib.roanh.util.FileSelector.FileExtension;

/**
 * This class contains all the configurable properties for the program.
 * 
 * @author Roan
 */
public class Configuration {
    /**
     * Extension filter for the current KeysPerSecond configuration file format.
     */
    public static final FileExtension KPS_NEW_EXT = FileSelector.registerFileExtension("KeysPerSecond config", "kps");
    /**
     * The original configuration file path or null
     * if this configuration was not loaded from a file.
     */
    private final Path data;
    /**
     * Whether or not the frame forces itself to be the top window.
     */
    private final BooleanSetting overlay = new BooleanSetting("overlay", false);
    /**
     * The number of milliseconds a single time frame takes.
     */
    private final UpdateRateSetting updateRate = new UpdateRateSetting("updateRate", UpdateRate.MS_100);
    /**
     * Whether or not the enable tracking key-modifier combinations.
     */
    private final BooleanSetting enableModifiers = new BooleanSetting("enableKeyModifierCombinations", false);
    /**
     * Whether or not to track all key presses.
     */
    private final BooleanSetting trackAllKeys = new BooleanSetting("trackAllKeys", false);
    /**
     * Whether or not to track all mouse button presses.
     */
    private final BooleanSetting trackAllButtons = new BooleanSetting("trackAllButtons", false);
    /**
     * Whether or not the frame is in windowed mode.
     */
    private final BooleanSetting windowed = new BooleanSetting("windowed", false);
    /**
     * The saved on screen position of the main frame (if previously saved).
     */
    private final PositionSettings position = new PositionSettings();
    /**
     * Default colour scheme settings.
     */
    private final ThemeSettings theme = new ThemeSettings();
    /**
     * Command key configuration.
     */
    private final CommandSettings commands = new CommandSettings();
    /**
     * General layout settings.
     */
    private final LayoutSettings layout = new LayoutSettings();
    /**
     * Automatic statistics saving settings.
     */
    private final StatsSavingSettings statsSaving = new StatsSavingSettings();
    /**
     * Graph settings.
     */
    private final SettingList<GraphPanelSettings> graphs = new SettingList<GraphPanelSettings>("graphs",
            GraphType::construct);
    /**
     * Special panel settings.
     */
    private final SettingList<SpecialPanelSettings> panels = new SettingList<SpecialPanelSettings>("panels",
            PanelType::construct);
    /**
     * Key panel configuration.
     */
    private final SettingList<KeyPanelSettings> keys = new SettingList<KeyPanelSettings>("keys",
            new LegacyCompatibleKeyConstructor());

    /**
     * Constructs a new configuration with default settings.
     */
    public Configuration() {
        this(null);
        panels.add(new MaxPanelSettings());
        panels.add(new AveragePanelSettings());
        panels.add(new CurrentPanelSettings());
    }

    /**
     * Constructs a new configuration object with the
     * given file to save settings to.
     * 
     * @param data The data save file.
     */
    protected Configuration(Path data) {
        this.data = data;
    }

    /**
     * Gets a list of legacy settings that can be used to parse
     * discontinued settings to current settings with a different name.
     * 
     * @param version The version of the configuration data being parsed
     *                can be used to determine which legacy settings should be
     *                included.
     * @return A list of legacy setting to parse.
     */
    protected List<Setting<?>> getLegacySettings(Version version) {
        List<Setting<?>> settings = new ArrayList<Setting<?>>();

        if (version.isBefore(8, 2)) {
            settings.add(ProxySetting.of("trackAllKeys", trackAllKeys, trackAllButtons));
        }

        if (version.isBefore(8, 8)) {
            statsSaving.collectLegacyProxies(settings);
            commands.collectLegacyProxies(settings);
            layout.collectLegacyProxies(settings);
            theme.collectLegacyProxies(settings);
            position.collectLegacyProxies(settings);

            LineGraphSettings graph = new LineGraphSettings();
            graph.collectLegacyProxies(settings);
            graphs.add(graph);

            MaxPanelSettings max = new MaxPanelSettings();
            max.collectLegacyProxies(settings);
            panels.add(max);

            AveragePanelSettings avg = new AveragePanelSettings();
            avg.collectLegacyProxies(settings);
            panels.add(avg);

            CurrentPanelSettings current = new CurrentPanelSettings();
            current.collectLegacyProxies(settings);
            panels.add(current);

            TotalPanelSettings total = new TotalPanelSettings();
            total.collectLegacyProxies(settings);
            panels.add(total);

            settings.add(new BooleanSetting("showKeys", true));
            settings.add(new LegacyPanelShowSetting("showMax", panels, max));
            settings.add(new LegacyPanelShowSetting("showAvg", panels, avg));
            settings.add(new LegacyPanelShowSetting("showCur", panels, current));
            settings.add(new LegacyPanelShowSetting("showTotal", panels, total));
            settings.add(new LegacyPanelShowSetting("graphEnabled", graphs, graph));
        }

        return settings;
    }

    /**
     * Gets a list of all settings in this configuration.
     * 
     * @return All settings in this configuration.
     */
    protected List<Setting<?>> getSettings() {
        return Arrays.asList(overlay, trackAllKeys, trackAllButtons, updateRate, enableModifiers, windowed);
    }

    /**
     * Gets a list of setting groups in this configuration.
     * 
     * @return All setting groups in this configuration.
     */
    protected List<SettingGroup> getSettingGroups() {
        return Arrays.asList(position, theme, commands, layout, statsSaving);
    }

    /**
     * Gets list of all setting lists in this configuration.
     * 
     * @return All setting lists in this configuration.
     */
    protected List<SettingList<? extends SettingGroup>> getSettingLists() {
        return Arrays.asList(graphs, panels, keys);
    }

    /**
     * Tests if this configuration has at least one displayable panel. Note that
     * this does not guarantee that the configuration actually results in a visible
     * GUI.
     * 
     * @return True if this configuration has at least one displayable panel.
     */
    public boolean isValid() {
        return !graphs.isEmpty() || !panels.isEmpty() || !keys.isEmpty();
    }

    /**
     * Gets the location on disk for this configuration file.
     * 
     * @return The on disk location of the configuration file.
     */
    public final Path getPath() {
        return data;
    }

    /**
     * Gets the theme settings for this configuration.
     * 
     * @return The theme settings.
     */
    public ThemeSettings getTheme() {
        return theme;
    }

    /**
     * Gets a list of layout panels in this configuration.
     * 
     * @return All layout panels in this configuration.
     */
    public List<LayoutPosition> getLayoutComponents() {
        return Stream.concat(panels.stream(), Stream.concat(keys.stream(), graphs.stream()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets the layout settings for this configuration.
     * 
     * @return The layout settings.
     */
    public LayoutSettings getLayout() {
        return layout;
    }

    /**
     * Gets the size in pixels of all the layout grid cells.
     * 
     * @return The grid cell size in pixels.
     * @see #getLayout()
     */
    public int getCellSize() {
        return layout.getCellSize();
    }

    /**
     * Gets the offset from the border of a panel to the actual content.
     * 
     * @return The distance in pixels from panel border to panel content.
     * @see #getLayout()
     */
    public int getBorderOffset() {
        return layout.getBorderOffset();
    }

    /**
     * Gets the save frame position if one was saved.
     * 
     * @return The frame position.
     * @see PositionSettings#hasPosition()
     */
    public PositionSettings getFramePosition() {
        return position;
    }

    /**
     * Gets the command configuration.
     * 
     * @return The command settings.
     */
    public CommandSettings getCommands() {
        return commands;
    }

    /**
     * Gets a list of all graphs and their settings.
     * 
     * @return All graph settings.
     */
    public SettingList<GraphPanelSettings> getGraphs() {
        return graphs;
    }

    /**
     * Gets a list of all special panels and their settings.
     * 
     * @return All special panel settings.
     */
    public SettingList<SpecialPanelSettings> getPanels() {
        return panels;
    }

    /**
     * Gets a list of all key panels and their settings.
     * 
     * @return All key panel settings.
     */
    public SettingList<KeyPanelSettings> getKeys() {
        return keys;
    }

    /**
     * Gets the auto saving configuration.
     * 
     * @return The auto saving configuration.
     */
    public StatsSavingSettings getStatsSavingSettings() {
        return statsSaving;
    }

    /**
     * Checks if key-modifier tracking is enabled.
     * 
     * @return True if key-modifier tracking is enabled.
     */
    public final boolean isKeyModifierTrackingEnabled() {
        return enableModifiers.getValue();
    }

    /**
     * Enables or disables key-modifier tracking.
     * 
     * @param enabled True to enable key-modifer tracking.
     */
    public void setKeyModifierTrackingEnabled(boolean enabled) {
        enableModifiers.update(enabled);
    }

    /**
     * Gets the update rate for statistic panels.
     * 
     * @return The current update rate.
     */
    public UpdateRate getUpdateRate() {
        return updateRate.getValue();
    }

    /**
     * Gets the update rate for statistic panels.
     * 
     * @return The current update rate in milliseconds.
     */
    public int getUpdateRateMs() {
        return updateRate.getValue().getRate();
    }

    /**
     * Checks if all mouse buttons are tracked.
     * 
     * @return True if all mouse buttons are tracked.
     */
    public boolean isTrackAllButtons() {
        return trackAllButtons.getValue();
    }

    /**
     * Checks if all keys are tracked.
     * 
     * @return True if all keys are tracked.
     */
    public boolean isTrackAllKeys() {
        return trackAllKeys.getValue();
    }

    /**
     * Sets whether all keys are tracked.
     * 
     * @param track True to track all keys.
     */
    public void setTrackAllKeys(boolean track) {
        trackAllKeys.update(track);
    }

    /**
     * Sets whether all mouse buttons are tracked.
     * 
     * @param track True to track all mouse buttons.
     */
    public void setTrackAllButtons(boolean track) {
        trackAllButtons.update(track);
    }

    /**
     * Sets whether overlay mode is enabled.
     * 
     * @param overlay True to enable overlay mode.
     */
    public void setOverlayMode(boolean overlay) {
        this.overlay.update(overlay);
    }

    /**
     * Checks if overlay mode is enabled.
     * 
     * @return True if overlay mode is enabled.
     */
    public boolean isOverlayMode() {
        return overlay.getValue();
    }

    /**
     * Checks if windowed mode is enabled.
     * 
     * @return True if windowed mode is enabled.
     */
    public boolean isWindowedMode() {
        return windowed.getValue();
    }

    /**
     * Enables or disables windowed mode.
     * 
     * @param windowed True to enable windowed mode.
     */
    public void setWindowedMode(boolean windowed) {
        this.windowed.update(windowed);
    }

    /**
     * Sets the update rate for aggregate panels.
     * 
     * @param rate The new update rate.
     */
    public void setUpdateRate(UpdateRate rate) {
        updateRate.update(rate);
    }

    /**
     * Saves this configuration file.
     * 
     * @param pos Whether or not the ask to save
     *            the on screen position of the program.
     */
    public final void saveConfig(boolean pos) {
        boolean savepos = (!pos) ? false
                : (Dialog.showConfirmDialog("Do you want to save the onscreen position of the program?"));
        Path saveloc = Dialog.showFileSaveDialog(KPS_NEW_EXT, "config");
        if (saveloc != null) {
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(saveloc))) {
                write(new IndentWriter(out), savepos);
                Dialog.showMessageDialog("Configuration saved succesfully.");
            } catch (IOException e1) {
                e1.printStackTrace();
                Dialog.showErrorDialog("Failed to save the configuration!");
            }
        }
    }

    /**
     * Writes this configuration to the given writer.
     * 
     * @param out The writer to write to.
     * @param pos If true the current frame position will be written.
     */
    public void write(IndentWriter out, boolean pos) {
        out.println("version: " + Main.VERSION);
        out.println();

        out.println("# General");
        for (Setting<?> setting : getSettings()) {
            setting.write(out);
        }

        for (SettingGroup group : getSettingGroups()) {
            if (group == position) {
                if (pos && Main.frame.isVisible()) {
                    out.println();
                    position.update(Main.frame.getLocationOnScreen());
                    position.write(out);
                }
            } else {
                out.println();
                group.write(out);
            }
        }

        for (SettingList<?> list : getSettingLists()) {
            out.println();
            list.write(out);
        }
    }
}