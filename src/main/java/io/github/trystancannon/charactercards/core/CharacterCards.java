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

package io.github.trystancannon.charactercards.core;

import io.github.trystancannon.charactercards.card.CharacterCard;
import io.github.trystancannon.charactercards.command.CardCommands;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * TODO:
 * - reload executor -- Might be unnecessary, but we'll do it.
 * 
 * @author Trystan Cannon
 */
public class CharacterCards extends JavaPlugin {
    /**
     * The label in chat which identifies a command as coming from this plugin.
     * Has a space at the end to separate the label from a message.
     */
    public static final String PLUGIN_COMMAND_LABEL = ChatColor.GOLD + "[" + ChatColor.AQUA + "Character Cards" + ChatColor.GOLD + "]" + ChatColor.RESET + " ";
    
    /**
     * The single allowed <code>CharacterCards</code> plugin instance.
     */
    private static CharacterCards characterCardsSingleton;
    
    /**
     * List of the currently loaded <code>CharacterCard</code> objects.
     */
    private static final HashMap<UUID, CharacterCard> cards = new HashMap<>();
    
    public CharacterCards() {
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("save")) {
            for (CharacterCard card : cards.values()) {
                card.saveToFile(getDataFolder().getAbsolutePath());
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public void onEnable() {
        // Create the plugin's data folder if doesn't already exist.
        if (!getDataFolder().exists()) {
            if (getDataFolder().mkdirs()) {
                getLogger().info("Created data folder.");
            } else {
                getLogger().info("Failed to create data folder.");
            }
        }
        
        if (characterCardsSingleton != null) {
            throw new UnsupportedOperationException("Character Cards plugin already running! Cannot enable a new one.");
        }
        
        characterCardsSingleton = this;
        
        // Initialize all of the card commands.
        CardCommands.init(this);
        // Setup the executor for the base command and its alias: /card and /c.
        getCommand("card").setExecutor(CardCommands.getCommand("card").getCardCommandExecutor());
        getCommand("c").setExecutor(CardCommands.getCommand("card").getCardCommandExecutor());
        
        // Reload all the cards currently saved.
        getLogger().info("Loading saved cards...");
        reloadCards();
        getLogger().log(Level.INFO, "Loaded {0} cards.", cards.size());
    }
    
    @Override
    public void onDisable() {
    }
    
    /**
     * Retrieves the <code>CharacterCard</code> object for the player with the
     * given card.
     * 
     * Will return <code>null</code> if the player does not have a card currently.
     * 
     * @param playerId
     * @return Card for the player with the given id.
     */
    public static CharacterCard getCardForPlayer(UUID playerId) {
        return cards.get(playerId);
    }
    
    /**
     * Attempts to locate the <code>CharacterCard</code> object for the
     * player with the given name.
     * 
     * May cause lag due to the iterative nature of this method.
     * 
     * @param name
     * @return Card for the player with the given name.
     */
    public static CharacterCard getCardForPlayer(String name) {
        for (UUID playerId : cards.keySet()) {
            if (Bukkit.getServer().getPlayer(playerId).getName().equalsIgnoreCase(name)) {
                return cards.get(playerId);
            }
        }
        
        return null;
    }
    
    /**
     * Attempts to locate the <code>CharacterCard</code> object for the
     * player whose description book has the given <code>BookMeta</code>.
     * 
     * @param descriptionBookMeta
     * @return Card for the player whose book has the given <code>BookMeta</code>.
     */
    public static CharacterCard getCardForPlayer(BookMeta descriptionBookMeta) {
        for (CharacterCard card : cards.values()) {
            if (card.getDescriptionBookMeta().equals(descriptionBookMeta)) {
                return card;
            }
        }
        
        return null;
    }
    
    /**
     * Creates a new <code>CharacterCard</code> for the player with the given
     * id.
     * 
     * Overwrites the existing card if the player already has one.
     * 
     * @param playerId
     * @return Newly created <code>CharacterCard</code> object.
     */
    public static CharacterCard createCardForPlayer(UUID playerId) {
        cards.put(playerId, new CharacterCard(playerId));
        return cards.get(playerId);
    }
    
    /**
     * Removes a player's <code>CharacterCard</code> from the currently loaded
     * list and deletes its save file, if it exists.
     * 
     * @param playerId
     */
    public void deleteCard(UUID playerId) {
        cards.remove(playerId);
        File file = new File(getDataFolder().getAbsolutePath() + "/" + playerId + ".txt");
        
        if (file.exists()) {
            file.delete();
        }
    }
    
    /**
     * Sends a message on behalf of the CharacterCards plugin to a receiver.
     * This message is labeled with the plugin's command label.
     * 
     * @param receiver Receiver of the message.
     * @param message Message to send.
     */
    public static void sendLabeledMessage(CommandSender receiver, String message) {
        if (receiver != null) {
            receiver.sendMessage(PLUGIN_COMMAND_LABEL + message);
        }
    }
    
    /**
     * Reloads the <code>cards</code> hash map by reading each file
     * found within the data folder as the output of a <code>CharacterCard</code>
     * object's call to <code>saveToFile</code>.
     */
    public void reloadCards() {
        for (File cardSaveFile : getDataFolder().listFiles()) {
            CharacterCard card = CharacterCard.fromFile(cardSaveFile);
            
            if (card != null) {
                cards.put(card.getPlayerId(), card);
            }
        }
    }
    
    /**
     * Saves a card to its proper place. This is a wrapper to
     * <code>CharacterCard.saveToFile</code>, but the argument passed is
     * the proper location for storage as designated by the plugin.
     * 
     * @param card 
     */
    public void saveCard(CharacterCard card) {
        card.saveToFile(getDataFolder().getAbsolutePath());
    }
}