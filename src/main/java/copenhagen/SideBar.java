package copenhagen;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

/**
 * This class is what is used for the side bar that is displayed in game and
 * contains all the logic associated with each button that is contained in the
 * side bar.
 */
public class SideBar {

    public boolean gameSaved;   // anytime a move is made in the game, set this this boolean to false
    private static final int buttonWidth = 100;
    private static final int buttonHeight = 40;
    private static final int startEndGap = 60;
    private static final int midGap = 70;
    private Color primaryColor;
    private Color secondaryColor;
    private Color letteringColor;
    private JPanel side;
    private JButton newGame;
    private JButton saveGame;
    private JButton loadGame;
    private JButton help;
    private JButton concede;
    private JButton exit;
    private JFrame exitWindow;
    private JFrame concedeWindow;

    /**
     * This is called when creating the side bar JPanel.
     *
     * @param pc This denotes the color that will be used for the background of
     * the buttons.
     * @param sc This denotes the color that will be used for the background the
     * panel.
     * @param lc This denotes the color that will be used for the text of the
     * buttons.
     */
    public SideBar(int[] pc, int[] sc, int[] lc) {
        primaryColor = new Color(pc[0], pc[1], pc[2]);
        secondaryColor = new Color(sc[0], sc[1], sc[2]);
        letteringColor = new Color(lc[0], lc[1], lc[2]);
        createPanel();
        createButtons();
    }

    /**
     * This function creates the side JPanel that will eventually be filled up
     * with things.
     */
    private void createPanel() {
        side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(secondaryColor);
        side.add(Box.createRigidArea(new Dimension(0, startEndGap)));
    }

    /**
     * This function creates the actual buttons that will be placed in the side
     * JPanel.
     */
    private void createButtons() {
        newGame = new JButton("New Game");
        styleButton(newGame, midGap);

        saveGame = new JButton("Save Game");
        styleButton(saveGame, midGap);
        saveGame.addActionListener(new SaveListener());

        loadGame = new JButton("Load Game");
        styleButton(loadGame, midGap);
        loadGame.addActionListener(new loadListener());

        help = new JButton("Help");
        styleButton(help, midGap);
        help.addActionListener(new HelpListener());

        concede = new JButton("Forfeit");
        styleButton(concede, midGap);
        concede.addActionListener(new ConcedeListener());

        exit = new JButton("Exit");
        styleButton(exit, startEndGap);
        exit.addActionListener(new ExitListener());
    }

    /**
     * This function is called to style the buttons with appropriate sizes and
     * colors.
     *
     * @param btn This is the button that will be affected.
     * @param gap This is the gap of blank space that will be created below the
     * button.
     */
    private void styleButton(JButton btn, int gap) {
        btn.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        btn.setBackground(primaryColor);
        btn.setForeground(letteringColor);
        side.add(btn);
        side.add(Box.createRigidArea(new Dimension(0, gap)));
    }

    /**
     * This is a button listener for when the saveGame button is clicked and
     * will prompt the user to save the game.
     */
    private class SaveListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Hnefatafl.saveGame();
        }
    }

    /**
     * This is a button listener for when the loadGame button is clicked and
     * will prompt the user to load a save file.
     */
    private class loadListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Hnefatafl.loadGame();
        }
    }

    /**
     * This is a button listener for when the help button is clicked and will
     * display the rules of the game.
     */
    private class HelpListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            new GameRules();
        }
    }
    
    
     /**
     * This is a button listener for when the forfeit button is clicked and will ask 
     * the user if they want to save his or her game, then exits the program.
     */
    private class ConcedeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (gameSaved) {
                System.exit(0);
            }

            Object[] forfeitOptions = {"Forfeit", "Don't Forfeit", "Cancel"};
            int n = JOptionPane.showOptionDialog(concedeWindow, "Are you sure you want to forfeit?", "Hnefatafl", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, forfeitOptions, forfeitOptions[0]);
            String winner;
            String loser;
            if (n == 0) {
                char turn = Hnefatafl.getTurn();
                if (turn == 'w') {
                    winner = "Attackers";
                    loser = "Defenders";
                } else {
                    winner = "Defenders";
                    loser = "Attackers";
                }

                Object[] saveOptions = {"Save", "Don't Save"};
                int m = JOptionPane.showOptionDialog(exitWindow, (loser + " Forfeit\n\n" + winner + " Win!\n\nWant to save your final game state?"), "Hnefatafl", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, saveOptions, saveOptions[0]);

                if (m == 0) {
                    Hnefatafl.saveGame();
                    System.exit(0);
                }
                if (m == 1) {
                    System.exit(0);
                }
            }

        }
    }

    /**
     * This is a button listener for when the user exits the game and depending
     * if the game is saved or not, will ask the user if they want to save his
     * or her game before exiting the program.
     */
    private class ExitListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (gameSaved) {
                System.exit(0);
            }
            Object[] options = {"Save", "Don't Save", "Cancel"};
            int n = JOptionPane.showOptionDialog(exitWindow, "Want to save your game progress?", "Hnefatafl", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (n == 0) {
                Hnefatafl.saveGame();
                System.exit(0);
            }
            if (n == 1) {
                System.exit(0);
            }
        }
    }

    /**
     * This function is called once the JPanel is fully formed and completed.
     *
     * @return This returns the newly created JPanel when the program is ran.
     */
    public JPanel getSideBar() {
        return side;
    }
}
