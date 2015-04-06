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

import io.github.trystancannon.charactercards.card.CharacterCard;
import io.github.trystancannon.charactercards.core.CharacterCards;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Trystan Cannon
 */
public class CardDeleteExecutor extends CardCommandExecutor {

    public CardDeleteExecutor(CharacterCards plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Player wants to delete their own character card:
        if (args == null || args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                CharacterCard card = CharacterCards.getCardForPlayer(player.getUniqueId());
                
                if (card != null) {
                    getPlugin().deleteCard(player.getUniqueId());
                } else {
                    CharacterCards.sendLabeledMessage(player, ChatColor.RED + "You don't have a character card!");
                }
                
                CharacterCards.sendLabeledMessage(sender, ChatColor.ITALIC + "Deleted your character card!");
            }
        // Player wants to delete the card of another player: Make sure they have
        // the right permissions to do so.
        } else if (args.length > 0 && sender.hasPermission("cards.edit.other")) {
            Player foreignPlayer = getOnlinePlayer(args[0]);
            
            if (foreignPlayer == null) {
                CharacterCards.sendLabeledMessage(sender, ChatColor.RED + "Could not find " + args[0]);
                return;
            }
            
            getPlugin().deleteCard(foreignPlayer.getUniqueId());
            CharacterCards.sendLabeledMessage(sender, ChatColor.ITALIC + "Deleted " + args[0] + "'s character card!");
        }
    }

    @Override
    public void execute(String[] args) {
    }

    @Override
    public void sendUsage(CommandSender receiver) {
        CharacterCards.sendLabeledMessage(receiver, ChatColor.RED + "Usage: /card delete");
    }
    
}
