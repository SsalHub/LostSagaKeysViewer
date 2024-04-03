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
package dev.ssalhub.lskv.config.setting;

import dev.ssalhub.lskv.config.IndentWriter;
import dev.ssalhub.lskv.config.Setting;

/**
 * String setting implementation.
 * 
 * @author Roan
 */
public class StringSetting extends Setting<String> {

    /**
     * Constructs a new string setting with the given key and default value.
     * 
     * @param key          The setting key to associate the value with.
     * @param defaultValue The default value for this setting.
     */
    public StringSetting(String key, String defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public boolean parse(String data) {
        update(data);
        return false;
    }

    @Override
    public void write(IndentWriter out) {
        out.println(key + ": " + value);
    }
}
