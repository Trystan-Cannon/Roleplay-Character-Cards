/*
 * The MIT License
 *
 * Copyright 2015 Trystan Cannon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.trystancannon.charactercards.command;

import io.github.trystancannon.charactercards.core.CharacterCards;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Parent class for all card command executors.
 * 
 * @author Trystan Cannon
 */
public abstract class CardCommandExecutor implements CommandExecutor {
    private final CharacterCards plugin;
    
    public CardCommandExecutor(CharacterCards plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Retrieves the plugin for this executor.
     * 
     * @return plugin
     */
    public CharacterCards getPlugin() {
        return plugin;
    }
    
    /**
     * Executes the command.
     * 
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return Command was executed.
     */
    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);
    
    /**
     * Executes the command with the given arguments.
     *
     * @param sender Sender of the command.
     * @param args The arguments with which the command will be executed.
     */
    public abstract void execute(CommandSender sender, String[] args);
    
    /**
     * Executes the command with the given arguments.
     *
     * @param args The arguments with which the command will be executed.
     */
    public abstract void execute(String[] args);
    
    /**
     * Sends the usage of this command to a receiver.
     * @param receiver 
     */
    public abstract void sendUsage(CommandSender receiver);
    
    
    /**
     * Returns a the <code>Player</code> object for the player with
     * the given name, if they are currently online.
     * 
     * @param name
     * @return Object for player with the given name.
     */
    public Player getOnlinePlayer(String name) {
        for (World world : plugin.getServer().getWorlds()) {
            for (Player player : world.getPlayers()) {
                if (player.getName().equalsIgnoreCase(name) || player.getDisplayName().equalsIgnoreCase(name)) {
                    return player;
                }
            }
        }
        
        return null;
    }
}
