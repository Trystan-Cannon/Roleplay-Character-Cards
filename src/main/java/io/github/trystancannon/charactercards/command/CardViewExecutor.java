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

import java.util.Scanner;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @author Trystan Cannon
 */
public class CardViewExecutor extends CardCommandExecutor implements Listener {

    public CardViewExecutor(CharacterCards plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void sendUsage(CommandSender receiver) {
        CharacterCards.sendLabeledMessage(receiver, ChatColor.RED + "Usage: /card view [player name]");
    }

    /**
     * Executes the /card view command.
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

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent interaction) {
        Player player = interaction.getPlayer();

        if (player.getItemInHand().getType() == Material.AIR && interaction.getRightClicked() instanceof Player) {
            Player playerClicked = (Player) interaction.getRightClicked();

            if (CharacterCards.getCardForPlayer(playerClicked.getUniqueId()) != null) {
                playerViewCard(player, playerClicked.getUniqueId());
            } else {
                CharacterCards.sendLabeledMessage(player, ChatColor.RED + "Player " + playerClicked.getDisplayName() + " does not have a character card.");
            }
        }
    }

    public void playerViewCard(Player playerViewing, UUID cardId) {
        CharacterCard playerCard = CharacterCards.getCardForPlayer(cardId);
        String viewLabel = getPlugin().getServer().getPlayer(cardId).getDisplayName() + "'s Character Card";

        playerViewing.sendMessage(ChatColor.GOLD + "--- " + ChatColor.AQUA + viewLabel + ChatColor.GOLD + " ---");
        ////////////////////////////////////////////////////////////////
        // Don't display information that hasn't yet been set.
        if (playerCard != null) {
            if (playerCard.getName() != null) {
                playerViewing.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.WHITE + playerCard.getName());
            }

            if (playerCard.getAge() > 1) {
                playerViewing.sendMessage(ChatColor.GOLD + "Age: " + ChatColor.WHITE + playerCard.getAge());
            }

            if (playerCard.getSex() != null) {
                playerViewing.sendMessage(ChatColor.GOLD + "Sex: " + ChatColor.WHITE + playerCard.getSex());
            }

            if (playerCard.getRace() != null) {
                playerViewing.sendMessage(ChatColor.GOLD + "Race: " + ChatColor.WHITE + playerCard.getRace());
            }

            if (playerCard.getDescription().length() > 0) {
                playerViewing.sendMessage(ChatColor.GOLD + "Description: " + ChatColor.RESET);
                
                /**
                 * Bukkit doesn't like sending empty lines, so we'll have to populate
                 * the empty lines with spaces so Bukkit has something to write.
                 */
                Scanner descriptionScanner = new Scanner(playerCard.getDescription());
                while (descriptionScanner.hasNextLine()) {
                    String line = descriptionScanner.nextLine();
                    line = line.length() > 0 ? line : " ";
                    
                    playerViewing.sendMessage(line);
                }
            }
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            // One or more arguments? Assume the player wants to view the card
            // of another player.
            if (args.length >= 1) {
                Player playerToView = getOnlinePlayer(args[0]);
                CharacterCard playerCard;

                if (playerToView == null && CharacterCards.getCardForPlayer(args[0]) == null) {
                    CharacterCards.sendLabeledMessage(sender, ChatColor.RED + "Player " + args[0] + " does not have a character card.");
                } else {
                    playerCard = playerToView == null ? CharacterCards.getCardForPlayer(args[0]) : CharacterCards.getCardForPlayer(playerToView.getUniqueId());
                    playerViewCard((Player) sender, playerCard.getPlayerId());
                }
            // If there was no name passed, assume that the player wants to view their own card.
            } else if (CharacterCards.getCardForPlayer(player.getUniqueId()) != null) {
                playerViewCard(player, player.getUniqueId());
            } else {
                CharacterCards.sendLabeledMessage(sender, ChatColor.RED + "You do not have a character card.");
            }
        }
    }

    @Override
    public void execute(String[] args) {
    }
}
