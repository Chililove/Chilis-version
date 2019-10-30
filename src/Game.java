
import java.util.ArrayList;

/**
 * This class is the main class of the "World of Zuul" application. "World of
 * Zuul" is a very simple, text based adventure game. Users can walk around some
 * scenery. That's all. It should really be extended to make it more
 * interesting!
 *
 * To play this game, create an instance of this class and call the "play"
 * method.
 *
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates and executes the
 * commands that the parser returns.
 *
 * @author Michael Kölling and David J. Barnes
 * @version 2011.07.31
 */
public class Game {

    private Parser parser;
    private Room currentRoom;
    Room outside, armory, pub, wizhut, bridge, crazyCatRoom;
    ArrayList<Item> inventory = new ArrayList<Item>();

    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        createRooms();
        parser = new Parser();

    }

    public static void main(String[] args) {
        Game mygame = new Game();
        mygame.play();

    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {

        // create the rooms
        outside = new Room("outside the town" + "\n\nI think we should head back to town! ^^\n");
        armory = new Room("in the armory" + "Come back later..");
        pub = new Room("in the hidden pub" + "\n\nToo agressive drunken dwarfs comes towards you" + "\n\nDo you want to fight them?\n");
        //loose/win 30 points - make some random thing to decide if winner or not each time. loose 10 if reply no.
        wizhut = new Room("in the wizards hut" + "\n\nThe wizard offers you a drink" + "\n\nDo you trust him?");
        /*yes - the wizard poisen you, loose 10 point, no - you turn down the drink, gain 10 points.*/
        bridge = new Room("under the bridges" + "\n\nWhat the hell is THAT thing?" + "\n\nShit, That's a fucking ugly cat!" + "\n\nDo you want to fight it?");
        //every 3rd try the cat will give 35 point, else it makes you sick with plague and drains 15 points.

        crazyCatRoom = new Room("in the crazy catwomans den");

        // initialise room exits
        outside.setExits(crazyCatRoom, armory, wizhut, pub);
        armory.setExits(bridge, crazyCatRoom, null, outside);
        pub.setExits(null, outside, null, crazyCatRoom);
        wizhut.setExits(outside, bridge, crazyCatRoom, null);
        armory.setExits(null, null, null, wizhut);
        crazyCatRoom.setExits(null, armory, pub, wizhut);

        currentRoom = outside;  // start game outside

        inventory.add(new Item("Spellbook"));
        inventory.add(new Item("FairyDust"));

    }

    public void timer() {
        try {
            for (int i = 1; i <= 10; i++) {

                Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play() {
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("\n      Back to reality(^-#).\nSee you soon young adventurer O.o\n");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("       Welcome to the World of Chili!");
        System.out.println("\nWorld of Chili is a new, incredibly fun adventure game.");
        System.out.println("\n       Type 'help' if you need help.\n");
        System.out.println();
        printLocationInfo();
        /*System.out.println("You are " + currentRoom.getDescription());
        System.out.print("Where do you want to go?\n");
        if (currentRoom.northExit != null) {
            System.out.print("north ");
        }
        if (currentRoom.eastExit != null) {
            System.out.print("east ");
        }
        if (currentRoom.southExit != null) {
            System.out.print("south ");
        }
        if (currentRoom.westExit != null) {
            System.out.print("west ");
        }
        System.out.println();
    }*/
        
            
    }        

    /**
     * Given a command, process (that is: execute) the command.
     *
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        if (command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        } else if (commandWord.equals("go")) {
            goRoom(command);
        } else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        } else if (commandWord.equals("inventory")) {
            printInventory();
        }

        return wantToQuit;
    }

    private void printInventory() {
        String output = "";
        for (int i = 0; i < inventory.size(); i++) {
            output += inventory.get(i).getDescription() + " ";

            System.out.println("You are carrying:");
            System.out.println(output);

        }
    }
    private void printLocationInfo()
        {
            System.out.println("You are " + currentRoom.getDescription());
        System.out.print("Where do you want to go?\n");
        if (currentRoom.northExit != null) {
            System.out.print("north ");
        }
        if (currentRoom.eastExit != null) {
            System.out.print("east ");
        }
        if (currentRoom.southExit != null) {
            System.out.print("south ");
        }
        if (currentRoom.westExit != null) {
            System.out.print("west ");
        }
        System.out.println();
        }

    // implementations of user commands:
    /**
     * Print out some help information. Here we print some stupid, cryptic
     * message and a list of the command words.
     */
    private void printHelp() {
        System.out.println("\nYou are lost. You are alone. You wander");
        System.out.println("in the woods.\n");
        System.out.println("Watch out for the shadows.\n");
        timer();
        System.out.println("What was that sound?\n");
        timer();
        System.out.println("Quick! Make a decision!!:\n");
        System.out.println(">go quit help inventory<\n");
    }

    /**
     * Try to go in one direction. If there is an exit, enter the new room,
     * otherwise print an error message.
     */
    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = null;
        if (direction.equals("north")) {
            nextRoom = currentRoom.northExit;
        }
        if (direction.equals("east")) {
            nextRoom = currentRoom.eastExit;
        }
        if (direction.equals("south")) {
            nextRoom = currentRoom.southExit;
        }
        if (direction.equals("west")) {
            nextRoom = currentRoom.westExit;
        }

        if (nextRoom == null) {
            System.out.println("There is no way!");
        } else {
            currentRoom = nextRoom;
            printLocationInfo();
           /* System.out.println("You are " + currentRoom.getDescription());
            System.out.print("Where do you want to go?: ");
            if (currentRoom.northExit != null) {
                System.out.print("north ");
            }
            if (currentRoom.eastExit != null) {
                System.out.print("east ");
            }
            if (currentRoom.southExit != null) {
                System.out.print("south ");
            }
            if (currentRoom.westExit != null) {
                System.out.print("west ");
            }
            System.out.println();
        }*/
    }
    }
    /**
     * "Quit" was entered. Check the rest of the command to see whether we
     * really quit the game.
     *
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
        if (command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true;  // signal that we want to quit
        }
    }

}
