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

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Trystan Cannon
 */
public class CardAgeExecutor extends CardCommandExecutor implements CommandExecutor {

    public CardAgeExecutor(CharacterCards plugin) {
        super(plugin);
    }
    
    @Override
    public void sendUsage(CommandSender receiver) {
        CharacterCards.sendLabeledMessage(receiver, ChatColor.RED + "Usage: /card age [integer from 1 to... a lot.]");
    }
    
    /**
     * Executes the /card age command.
     * 
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return Command was executed.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("card") && args != null && args.length >= 1 && args[0].equalsIgnoreCase("age")) {
            execute(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }
        
        return false;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && args.length >= 1) {
            Player player = (Player) sender;
            CharacterCard playerCard = CharacterCards.getCardForPlayer(player.getUniqueId());
            CharacterCard card;
            int age;
            
            // Set another player's age.
            if (args.length >= 2 && player.hasPermission("cards.edit.other")) {
                card = CharacterCards.getCardForPlayer(args[0]);
                age = parseInt(args[1]);
            // Set player's own age.
            } else {
                card = playerCard;
                age = parseInt(args[0]);
            }
            
            if (age < 1) {
                sendUsage(sender);
            } else {
                if (card == null) {
                    card = CharacterCards.createCardForPlayer(player.getUniqueId());
                }
                
                card.setAge(age);
                getPlugin().saveCard(card);
                
                if (card == playerCard) {
                    CharacterCards.sendLabeledMessage(sender, ChatColor.ITALIC + "Age set!");
                } else {
                    CharacterCards.sendLabeledMessage(sender, ChatColor.ITALIC + "Age for " + Bukkit.getPlayer(card.getPlayerId()).getDisplayName() + " set!");
                }
            }
        }
    }
    
    @Override
    public void execute(String[] args) {
    }
    
    /**
     * Parses an integer from a string, returning the parsed integer
     * if successful; -1 if not.
     * 
     * @param stringToParse
     * @return Parsed integer or -1 if the method fails.
     */
    private static int parseInt(String stringToParse) {
        try {
            int number = Integer.parseInt(stringToParse);
            return number;
        } catch (NumberFormatException failure) {}
        
        return -1;
    }
}
