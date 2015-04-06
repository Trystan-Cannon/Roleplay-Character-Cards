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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Trystan Cannon
 */
public class CardReloadExecutor extends CardCommandExecutor {

    public CardReloadExecutor(CharacterCards plugin) {
        super(plugin);
    }

    @Override
    public void sendUsage(CommandSender receiver) {
    }
    
    /**
     * Executes the /card reload command.
     * 
     * @param sender
     * @param command
     * @param label
     * @param args 
     * @return Command was executed.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("cards.reload")) {
            CharacterCards.sendLabeledMessage(sender, "Reloading character cards...");
            getPlugin().reloadCards();
            CharacterCards.sendLabeledMessage(sender, "Card reload complete!");
        }
    }
    
    @Override
    public void execute(String[] args) {
    }
}
