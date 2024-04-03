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
package dev.ssalhub.lskv.event;

/**
 * Abstract base class for providers of input events.
 * 
 * @author Roan
 */
public abstract class InputSource {
    /**
     * The event manager to report input to.
     */
    protected EventManager manager;

    /**
     * Constructs a new input source with the
     * given event manager to report to.
     * 
     * @param manager The event manager to report to.
     */
    public InputSource(EventManager manager) {
        this.manager = manager;
    }
}
