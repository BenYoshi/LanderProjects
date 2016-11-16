package pkg364gameofwar;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * @author Benny Ioschikhes
 */
public class GoWSerialization extends JFrame implements Serializable {

    private Card[] deck;
    private LinkedList<Card> p1, p2, pile, p1Garbage, p2Garbage;
    private final Random random = new Random();
    private Card p1Card, p2Card;

    private static ObjectInputStream saveData;

    public GoWSerialization() throws InterruptedException {
        deck = Card.createDeck();
        p1 = new LinkedList<>();
        p2 = new LinkedList<>();
        p1Garbage = new LinkedList<>();
        p2Garbage = new LinkedList<>();
        pile = new LinkedList<>();

        shuffle();
        deal();
        play();
        displayWinner();
    }

    public void makeWarGUI() {
        setTitle("Card Game of War");
        setSize(800, 400);

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try (ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream("saveData.txt"))) {
                    GoWSerialization savedGame = 
                            (GoWSerialization) ois.readObject();
                    deck = savedGame.deck;
                    p1 = savedGame.p1;
                    p2 = savedGame.p2;
                    p1Garbage = savedGame.p1Garbage;
                    p2Garbage = savedGame.p2Garbage;
                    pile = savedGame.pile;
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(GoWSerialization.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        });

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                save();
            }
        });

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int answer = JOptionPane.showConfirmDialog(rootPane,
                        "Would you like to save the game?");
                if (answer == JOptionPane.YES_OPTION) {
                    save();
                    System.exit(0);
                } else if (answer == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        });

        file.add(openMenuItem);
        file.add(saveMenuItem);
        file.add(exitMenuItem);
        menuBar.add(file);
        setJMenuBar(menuBar);

        setLayout(new GridLayout(1, 3));
        add(new DisplayPlayer1());
        add(new DisplayStats());
        add(new DisplayPlayer2());

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File("saveData.txt")))) {
            oos.writeObject(GoWSerialization.this);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(GoWSerialization.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private void shuffle() {
        for (int i = 0; i < deck.length; i++) {
            int randomIndex = random.nextInt(deck.length);
            Card temp = deck[i];
            deck[i] = deck[randomIndex];
            deck[randomIndex] = temp;
        }
    }

    private void deal() {
        for (int i = 0; i < 26; i++) {
            p1.add(deck[i]);
        }
        for (int i = 26; i < 52; i++) {
            p2.add(deck[i]);
        }
    }

    private void play() throws InterruptedException {
        int x;
        p1Card = p1.get(0);
        p2Card = p2.get(0);
        makeWarGUI();
        int i = 0;
        while (!gameOver()) {
            x = p1.get(0).compareTo(p2.get(0));

            Thread.sleep(1000);

            p1Card = p1.get(0);
            p2Card = p2.get(0);
            repaint();

            if (x > 0) {
                putInPile(1);
                player1Wins();
            } else if (x < 0) {
                putInPile(1);
                player2Wins();
            } else {
                war();
            }

            checkIfMainEmpty();
            i++;
        }
        System.out.println(i);
    }

    private void war() {
        if (p1.isEmpty() || p2.isEmpty()) {
        } else if (p1.size() >= 4 && p2.size() >= 4) {
            putInPile(3);
            int x = p1.get(0).compareTo(p2.get(0));
            putInPile(1);

            if (x > 0) {
                player1Wins();
            } else if (x < 0) {
                player2Wins();
            } else {
                war();
            }
        } else {
            if (p1.size() >= p2.size()) {
                putInPile(p2.size() - 1);
                int x = p1.get(0).compareTo(p2.get(0));
                putInPile(1);

                if (x > 0) {
                    player1Wins();
                } else if (x < 0) {
                    player2Wins();
                } else {
                    player1Wins();
                }
            } else {
                putInPile(p1.size() - 1);
                int x = p1.get(0).compareTo(p2.get(0));
                putInPile(1);

                if (x > 0) {
                    player1Wins();
                } else if (x < 0) {
                    player2Wins();
                } else {
                    player2Wins();
                }
            }
        }
    }

    private void checkIfMainEmpty() {
        if (p1.isEmpty()) {
            Collections.shuffle(p1Garbage);
            p1.addAll(p1Garbage);
            p1Garbage.clear();
        }
        if (p2.isEmpty()) {
            Collections.shuffle(p1Garbage);
            p2.addAll(p2Garbage);
            p2Garbage.clear();
        }
    }

    private void player1Wins() {
        p1Garbage.addAll(pile);
        pile.clear();
    }

    private void player2Wins() {
        p2Garbage.addAll(pile);
        pile.clear();
    }

    private void putInPile(int amount) {
        for (int i = 0; i < amount; i++) {
            checkIfMainEmpty();
            pile.add(p1.get(0));
            pile.add(p2.get(0));
            p1.remove(0);
            p2.remove(0);
        }
    }

    private void displayWinner() {
        String finalWinner = "";

        if (p1.size() + p1Garbage.size() == 52) {
            finalWinner = "Player 1 wins!";
        } else if (p2.size() + p2Garbage.size() == 52) {
            finalWinner = "Player 2 wins!";
        }

        JOptionPane.showMessageDialog(null, finalWinner,
                "We have a winner!", WIDTH);
    }

    private boolean gameOver() {
        return (p1.isEmpty() && p1Garbage.isEmpty())
                || (p2.isEmpty() && p2Garbage.isEmpty());
    }

    class DisplayPlayer1 extends JPanel implements Serializable {

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            super.paint(g2);

            g2.setFont(new Font("Serif", Font.BOLD, 25));
            g2.drawString("Player 1", 105, 60);
            setBackground(Color.decode("#62c4ff"));

            g2.setColor(Color.WHITE);
            g2.fill3DRect(75, 75, 150, 200, true);
            g2.setColor(p1Card.suit.ordinal() >= 2 ? Color.RED
                    : Color.BLACK);
            g2.drawString(p1Card.rank.toString(), 80, 100);
            g2.drawString(p1Card.suit.toString(), 140, 180);

            g2.translate(getWidth(), getHeight());
            g2.rotate(Math.PI);
            g2.drawString(p1Card.rank.toString(), 40, 100);
        }
    }

    class DisplayPlayer2 extends JPanel implements Serializable {

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            super.paint(g2);

            g2.setFont(new Font("Serif", Font.BOLD, 25));
            g2.setColor(Color.WHITE);
            g2.drawString("Player 2", 105, 60);
            setBackground(Color.decode("#9F0303"));

            g2.fill3DRect(75, 75, 150, 200, true);
            g2.setColor(p2Card.suit.ordinal() >= 2 ? Color.RED
                    : Color.BLACK);
            g2.drawString(p2Card.rank.toString(), 80, 100);
            g2.drawString(p2Card.suit.toString(), 140, 180);

            g2.translate(getWidth(), getHeight());
            g2.rotate(Math.PI);
            g2.drawString(p2Card.rank.toString(), 40, 100);
        }
    }

    class DisplayStats extends JPanel implements Serializable {

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            super.paint(g2);
            g2.setFont(new Font("Serif", Font.BOLD, 25));

            String p1String = "Player 1 hand: " + p1.size(),
                    p2String = "Player 2 hand: " + p2.size(),
                    p1GarbageString = "Player 1 garbage: " + p1Garbage.size(),
                    p2GarbageString = "Player 2 garbage: " + p2Garbage.size();

            g2.drawString(p1String, 10, 75);
            g2.drawString(p1GarbageString, 10, 125);
            g2.drawString(p2String, 30, 250);
            g2.drawString(p2GarbageString, 30, 300);
        }
    }
}