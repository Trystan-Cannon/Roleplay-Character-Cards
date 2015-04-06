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

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the execution of the base command: /card.
 * 
 * @author Trystan Cannon
 */
public class BaseCommandExecutor extends CardCommandExecutor implements CommandExecutor {

    public BaseCommandExecutor(CharacterCards plugin) {
        super(plugin);
    }

    @Override
    public void sendUsage(CommandSender receiver) {
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((command.getName().equalsIgnoreCase("card") || command.getName().equalsIgnoreCase("c")) && sender instanceof Player) {
            if (args.length >= 1) {
                String subCommandName = args[0];
                
                // Execute whatever sub command the user had in mind.
                for (CardCommand subCommand : CardCommands.getSubCommandsForBaseExecutor(this)) {
                    if (subCommand.getCommandName().equalsIgnoreCase(subCommandName)) {
                        // Remove the name from the arguments.
                        String[] modifiedArgs = Arrays.copyOfRange(args, 1, args.length);
                        subCommand.getCardCommandExecutor().execute(sender, modifiedArgs);
                    }
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
    }
    
    @Override
    public void execute(String[] args) {
    }
}
