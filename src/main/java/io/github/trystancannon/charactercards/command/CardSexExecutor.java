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
import io.github.trystancannon.charactercards.user.Gender;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Trystan Cannon
 */
public class CardSexExecutor extends CardCommandExecutor implements CommandExecutor {
    
    public CardSexExecutor(CharacterCards plugin) {
        super(plugin);
    }
    
    @Override
    public void sendUsage(CommandSender receiver) {
        CharacterCards.sendLabeledMessage(receiver, ChatColor.RED + "Usage: /card sex [male/female]");
    }
    
    /**
     * Executes the /card sex command.
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
        if (sender instanceof Player && args != null && args.length >= 1) {
            Player player = (Player) sender;
            CharacterCard playerCard = CharacterCards.getCardForPlayer(player.getUniqueId());
            CharacterCard card;
            String sex;
            
            // Player wants to edit the sex of another: Make sure they have the
            // right arguments and permissions to do so.
            if (args.length >= 2 && player.hasPermission("cards.edit.other")) {
                Player foreignPlayer = getOnlinePlayer(args[0]);
                
                if (foreignPlayer == null) {
                    CharacterCards.sendLabeledMessage(sender, ChatColor.RED + "Could not find " + args[0] + ".");
                    return;
                }
                
                player = foreignPlayer;
                sex = args[1];
                card = CharacterCards.getCardForPlayer(foreignPlayer.getUniqueId());
            } else {
                card = playerCard;
                sex = args[0];
            }
            
            // Create a card for the player if they don't already have one.
            if (card == null) {
                card = CharacterCards.createCardForPlayer(player.getUniqueId());
            }
            
            Gender gender = Gender.parseSex(sex);
            if (gender != null) {
                card.setSex(Gender.parseSex(sex));
                getPlugin().saveCard(card);
                
                if (card == playerCard) {
                    CharacterCards.sendLabeledMessage(sender, ChatColor.ITALIC + "Sex set!");
                } else {
                    CharacterCards.sendLabeledMessage(sender, ChatColor.ITALIC + "Sex for " + player.getDisplayName() + " set!");
                }
            // Seems like bad design.
            } else {
                sendUsage(sender);
            }
        } else {
            sendUsage(sender);
        }
    }
    
    @Override
    public void execute(String[] args) {
    }
}
