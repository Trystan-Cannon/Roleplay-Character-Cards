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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trystan Cannon
 */
public final class CardCommands {
    public static final List<CardCommand> commands = new ArrayList<>();
    private static boolean isInitialized = false;
    
    public static void init(CharacterCards plugin) {
        if (!isInitialized) {
            CardCommand baseCommand = new CardCommand("card", new BaseCommandExecutor(plugin));
            commands.add(baseCommand);
            
            // Register commands and their executors.
            baseCommand.addSubCommand(new CardCommand("card", new BaseCommandExecutor(plugin)));
            baseCommand.addSubCommand(new CardCommand("age", new CardAgeExecutor(plugin)));
            baseCommand.addSubCommand(new CardCommand("name", new CardNameExecutor(plugin)));
            baseCommand.addSubCommand(new CardCommand("description", new CardDescriptionExecutor(plugin)));
            baseCommand.addSubCommand(new CardCommand("race", new CardRaceExecutor(plugin)));
            baseCommand.addSubCommand(new CardCommand("sex", new CardSexExecutor(plugin)));
            baseCommand.addSubCommand(new CardCommand("reload", new CardReloadExecutor(plugin)));
            baseCommand.addSubCommand(new CardCommand("view", new CardViewExecutor(plugin)));
            baseCommand.addSubCommand(new CardCommand("delete", new CardDeleteExecutor(plugin)));
            
            // Register command aliases.
            baseCommand.addSubCommand(new CardCommand("desc", getCommand("card").getSubCommand("description").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("d", getCommand("card").getSubCommand("description").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("a", getCommand("card").getSubCommand("age").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("n", getCommand("card").getSubCommand("name").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("r", getCommand("card").getSubCommand("race").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("s", getCommand("card").getSubCommand("sex").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("v", getCommand("card").getSubCommand("view").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("re", getCommand("card").getSubCommand("reload").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("de", getCommand("card").getSubCommand("delete").getCardCommandExecutor()));
            baseCommand.addSubCommand(new CardCommand("del", getCommand("card").getSubCommand("delete").getCardCommandExecutor()));
            
            isInitialized = true;
        }
    }
    
    public static CardCommand getCommand(String commandName) {
        for (CardCommand command : commands) {
            if (command.getCommandName().equals(commandName)) {
                return command;
            }
        }
        
        return null;
    }
    
    public static List<CardCommand> getSubCommandsForBaseExecutor(CardCommandExecutor baseExecutor) {
        if (baseExecutor != null) {
            for (CardCommand baseCommand : commands) {
                if (baseCommand.getCardCommandExecutor().getClass().equals(baseExecutor.getClass())) {
                    return baseCommand.getSubCommands();
                }
            }
        }
        
        return null;
    }
}
