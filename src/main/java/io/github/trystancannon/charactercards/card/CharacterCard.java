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
package io.github.trystancannon.charactercards.card;

import io.github.trystancannon.charactercards.user.Gender;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * @author Trystan Cannon
 */
public final class CharacterCard {
    /**
     * The text which appears at the very beginning of the description book. This
     * allows the plugin to identify that the book is the player's description book.
     */
    public static final String DESCRIPTION_BOOK_IDENTIFIER = "Character Description. Do not edit these auto-generated lines. Use the next page.";
    
    /**
     * The text which fills the second page in the book (the first page on which the user
     * can write.)
     */
    public static final String DESCRIPTION_BOOK_FILLER_PAGE = "Text goes here. You can delete me.";
    
    private final UUID playerId;
    private String name;
    private int age;
    private Gender sex;
    private String race;
    
    private ItemStack descBook;
    
    /**
     * The identifier page which makes the player's description book uniquely
     * theirs. This is simply: DESCRIPTION_BOOK_IDENTIFIER + " " + playerId.
     */
    private final String personalIdentifierPage;
    
    public CharacterCard(UUID playerId) {
        this.playerId = playerId;
        descBook = new ItemStack(Material.BOOK_AND_QUILL, 1);
        personalIdentifierPage = generatePersonalIdentifierPage(playerId);
        
        // Initialize the book's metadata which identifies it as the player's
        // description book.
        BookMeta descBookMeta = (BookMeta) descBook.getItemMeta();
        descBookMeta.setPages(personalIdentifierPage, DESCRIPTION_BOOK_FILLER_PAGE);
        
        descBook.setItemMeta(descBookMeta);
    }
    
    public CharacterCard(UUID playerId, String name, int age, Gender sex, String race, String description) {
        this.playerId = playerId;
        
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.race = race;
        
        // Initialize the book's metadata which identifies it as the player's
        // description book.
        descBook = new ItemStack(Material.BOOK_AND_QUILL, 1);
        personalIdentifierPage = generatePersonalIdentifierPage(playerId);
        
        BookMeta descBookMeta = (BookMeta) descBook.getItemMeta();
        
        // We have to split the pages into sections of 256 characters in length.
        List<String> pages = splitByLength(description, 256);
        // If there was a problem creating a description from the given text,
        // then we'll have an empty array list to start.
        if (pages == null) {
            pages = new ArrayList<>();
        }
        
        pages.add(0, personalIdentifierPage);
        
        descBookMeta.setPages(pages);
        descBook.setItemMeta(descBookMeta);
    }
    
    private String generatePersonalIdentifierPage(UUID playerId) {
        return DESCRIPTION_BOOK_IDENTIFIER + " " + playerId;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public Gender getSex() {
        return sex;
    }
    
    public void setSex(Gender sex) {
        this.sex = sex;
    }
    
    public void setSex(String sex) {
        Gender _sex = Gender.parseSex(sex);
        
        if (_sex != null) {
            this.sex = _sex;
        }
    }
    
    public String getRace() {
        return race;
    }
    
    public void setRace(String race) {
        this.race = race;
    }
    
    public ItemStack getDescriptionBook() {
        BookMeta descBookMeta = getDescriptionBookMeta();
        
        // If the book is signed, we'll have to update its metadata to remove the
        // signature.
        if (descBookMeta.hasAuthor() || descBookMeta.hasTitle() || descBookMeta.hasLore()) {
            descBook = new ItemStack(Material.BOOK_AND_QUILL, 1);
            
            BookMeta fixedMeta = (BookMeta) descBook.getItemMeta();
            fixedMeta.setPages(descBookMeta.getPages());
            
            descBook.setItemMeta(fixedMeta);
        }
        
        return descBook;
    }
    
    public BookMeta getDescriptionBookMeta() {
        return (BookMeta) descBook.getItemMeta();
    }
    
    public String getDescription() {
        String description = "";
        BookMeta descBookMeta = getDescriptionBookMeta();
        
        // Book pageIndex indexing starts at 1. Make sure it is actually
        // a description book. If the book hasn't been changed since its creation,
        // then assume that the description has not changed, so we won't return anything.
        if (descBookMeta.getPageCount() >= 2 && descBookMeta.getPage(1).equals(personalIdentifierPage) && !descBookMeta.getPage(2).equals(DESCRIPTION_BOOK_FILLER_PAGE)) {
            for (int pageIndex = 2; pageIndex <= descBookMeta.getPageCount(); pageIndex++) {
                ////////////////////////////////////////////////////////////////
                /**
                 * For some reason, when the page is read back from the book
                 * meta, two new lines in a row appear as a single '\n' and the
                 * black <cdoe>ChatColor</code>.
                 * 
                 * To fix this, simply, all instances of the black chat color are removed.
                 */
                ////////////////////////////////////////////////////////////////
                description = description + descBookMeta.getPage(pageIndex).replaceAll(ChatColor.BLACK.toString(), "");
            }
        }
        
        return description;
    }
    
    public void addDescription(String description) {
        BookMeta descBookMeta = getDescriptionBookMeta();
        descBookMeta.addPage(description);
        
        getDescriptionBook().setItemMeta(descBookMeta);
    }
    
    /**
     * Saves the current character card to the given folder. The name of the
     * file is the <code>UUID</code> which identifies the player.
     * 
     * Each file is saved as follows:
     * 
     * name
     * age
     * race
     * sex
     * description line 1
     * description line 2
     * ...
     * 
     * @param folderPath
     * @return Save success or failure.
     */
    public boolean saveToFile(String folderPath) {
        File file = new File(folderPath + "/" + playerId + ".txt");
        
        // Create the file if it doesn't exist.
        try {
            if (!file.exists() || file.isDirectory()) {
                file.createNewFile();
            }
        } catch (IOException failure) {
            return false;
        }
        
        // Write the file.
        try (PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8")) {
            writer.println(name);
            writer.println(age);
            writer.println(race);
            writer.println(sex);
            
            // PrintWriter.println seems to skip over the new line
            // character, so we'll have to explicitly tell it to write
            // a new line.
            Scanner descriptionScanner = new Scanner(getDescription());
            while (descriptionScanner.hasNextLine()) {
                writer.println(descriptionScanner.nextLine());
            }
            descriptionScanner.close();
            
            writer.flush();
            writer.close();
        } catch (Exception failure) {
            return false;
        }
        
        return true;
    }
    
    public static CharacterCard fromFile(File file) {
        CharacterCard card = null;
        
        if (!file.exists()) {
            return null;
        }
        
        try (Scanner fileScanner = new Scanner(file.toPath(), StandardCharsets.UTF_8.name())) {
            /**
             * File structure:
             * 
             * name
             * age
             * race
             * sex
             * desc line 1
             * desc line 2
             * ...
             * desc line n
             * 
             * If one of the string fields doesn't save, then we get: null
             * If the integer filed doesn't save, then we get: 0
             */
            String name = fileScanner.nextLine();
            int age = fileScanner.nextInt();
            
            // Reread the age as a line, so the saved race
            // will be read from the next call to nextLine.
            fileScanner.nextLine();
            
            String race = fileScanner.nextLine();
            Gender sex = Gender.parseSex(fileScanner.nextLine());
            String description = "";
            
            while (fileScanner.hasNextLine()) {
                description = description + fileScanner.nextLine() + "\n";
            }
            
            // Strip the last \n character from the description.
            if (description.length() > 0) {
                description = description.substring(0, description.length() - 1);
            } else {
                description = null;
            }
            
            // Double check that we've read everything properly.
            if (name.equals("null")) {
                name = null;
            }
            if (race.equals("null")) {
                race = null;
            }
            
            // Create the card by using the file name as the UUID, minus the file extension, of course.
            // Make sure that we were able to properly load some fraction of information regarding
            // a player's card.
            if (!(name == null && age == 0 && sex == null && race == null && description == null)) {
                card = new CharacterCard(UUID.fromString(file.getName().replaceAll(".txt", "")), name, age, sex, race, description);
            }
        } catch (Exception failure) {
            failure.printStackTrace();
            return null;
        }
        
        return card;
    }
    
    /**
     * Splits the given string into equal intervals of the given size.
     * 
     * @param input
     * @param length
     * @return Sections of the given string in sections of the given length.
     */
    private List<String> splitByLength(String input, int length) {
        if (input != null && length > 0) {
            ArrayList<String> sections = new ArrayList<>();
            
            while (input.length() >= length) {
                sections.add(input.substring(0, length));
                input = input.substring(length, input.length());
            }
            sections.add(input);
            
            return sections;
        }
        
        return null;
    }
}