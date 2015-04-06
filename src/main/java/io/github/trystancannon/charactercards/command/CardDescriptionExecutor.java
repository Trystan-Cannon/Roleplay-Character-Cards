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

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 *
 * @author Trystan Cannon
 */
public class CardDescriptionExecutor extends CardCommandExecutor implements CommandExecutor, Listener {

    private Object ChatColors;

    public CardDescriptionExecutor(CharacterCards plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void sendUsage(CommandSender receiver) {
        CharacterCards.sendLabeledMessage(receiver, ChatColor.RED + "Usage: /card description");
    }

    /**
     * Executes the /card description command. Also accepts /card desc.
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

    /**
     * Because a book gets a new meta data object when its contents change, it
     * is necessary to check for editing.
     *
     * If the player edits a book, and the book's meta data prior to the edit is
     * the same as a player card's current book meta data, then the player
     * card's version needs to be updated. This is the function of this method.
     *
     * @param editEvent
     */
    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent editEvent) {
        Player editPlayer = editEvent.getPlayer();
        CharacterCard playerCard = CharacterCards.getCardForPlayer(editPlayer.getUniqueId());
        CharacterCard card = CharacterCards.getCardForPlayer(editEvent.getPreviousBookMeta());
        
        if (editPlayer.hasPermission("cards.edit.other") && card != null && card != playerCard) {
            card.getDescriptionBook().setItemMeta(editEvent.getNewBookMeta());
            getPlugin().saveCard(card);
            CharacterCards.sendLabeledMessage(editPlayer, ChatColor.ITALIC + "Description for " + getPlugin().getServer().getPlayer(card.getPlayerId()).getDisplayName() + " set!");
            
            // Remove the book from the player's inventory.
            editPlayer.getInventory().setItemInHand(null);
        } else if (playerCard != null) {
            playerCard.getDescriptionBook().setItemMeta(editEvent.getNewBookMeta());
            getPlugin().saveCard(card);
            CharacterCards.sendLabeledMessage(editPlayer, ChatColor.ITALIC + "Description set!");
            
            // Remove the book from the player's inventory.
            editPlayer.getInventory().setItemInHand(null);
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();
            CharacterCard playerCard = CharacterCards.getCardForPlayer(playerId);

            // Edit another player's description:
            if (args != null && args.length > 0 && player.hasPermission("cards.edit.other")) {
                CharacterCard foreignPlayerCard = CharacterCards.getCardForPlayer(args[0]);

                if (foreignPlayerCard != null) {
                    // Player passed themselves as the player to edit, do so with a recursive call.
                    if (foreignPlayerCard.getPlayerId().equals(playerId)) {
                        execute(sender, null);
                        // Player truly would like to edit another player's description:
                    } else {
                        player.getInventory().addItem(foreignPlayerCard.getDescriptionBook());
                    }
                } else {
                    CharacterCards.sendLabeledMessage(sender, "You do not have permission to use this command, or the player " + args[0] + " does not have a character card.");
                }

                // Edit your description:
            } else {
                playerCard = playerCard == null ? CharacterCards.createCardForPlayer(playerId) : playerCard;

                // Check if the player still has the book. If, so don't give them
                // a new one.
                for (ItemStack stack : player.getInventory().getContents()) {
                    if (stack != null && stack.getType() == Material.BOOK_AND_QUILL && ((BookMeta) stack.getItemMeta()).getPages().equals(playerCard.getDescriptionBookMeta().getPages())) {
                        CharacterCards.sendLabeledMessage(sender, ChatColor.RED + "You still have your description book.");
                        return;
                    }
                }

                player.getInventory().addItem(playerCard.getDescriptionBook());
                CharacterCards.sendLabeledMessage(sender, "Once you've finished editing your description, simply click 'Done.'");
            }
        } else {
            sendUsage(sender);
        }
    }

    @Override
    public void execute(String[] args) {
    }
}
