import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.regex.*;

public class GameOfLife extends JFrame implements ActionListener {
  
  private int width = 0;
  private int height = 0;
  private JPanel buttonPanel = null;
  private JToggleButton[][] buttons = null;
  private JPanel actionsPanel = null;
  private JButton changeRulesButton = null;
  private JButton changeSizeButton = null;
  private JButton invertButton = null;
  private JButton clearButton = null;
  private JButton stepButton = null;
  private JToggleButton runButton = null;
  private javax.swing.Timer stepper = null;
  
  private Vector<Integer> B = new Vector<Integer>();
  private Vector<Integer> S = new Vector<Integer>();
  private Pattern patt = null;

  private boolean parseRules(String rules) {
    Matcher m = patt.matcher(rules);
    if (!m.matches())
      return false;
    B.removeAllElements();
    S.removeAllElements();
    for (char c : m.group(1).toCharArray())
      B.addElement(Character.getNumericValue(c));

    for (char c : m.group(2).toCharArray())
      S.addElement(Character.getNumericValue(c));

    return true;
  }

  private void changeSize() {
    while (true) {
      String s = JOptionPane.showInputDialog(this, "How wide do you want the game?", "Size", JOptionPane.QUESTION_MESSAGE);
      
      if (s == null) {
        System.exit(0);
      }
        
      try {
        width = Integer.parseInt(s);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Please enter an integer", "Invalid input!", JOptionPane.ERROR_MESSAGE);
        continue;
      }
      
      if (width <= 1) {
        JOptionPane.showMessageDialog(this, "Please enter an integer larger than 1", "Invalid input!", JOptionPane.ERROR_MESSAGE);
        continue;
      }
      
      break;
      
    }

    while (true) {
      String s = JOptionPane.showInputDialog(this, "How tall do you want the game?", "Size", JOptionPane.QUESTION_MESSAGE);
      
      if (s == null) {
        System.exit(0);
      }
        
      try {
        height = Integer.parseInt(s);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Please enter an integer", "Invalid input!", JOptionPane.ERROR_MESSAGE);
        continue;
      }
      
      if (height <= 1) {
        JOptionPane.showMessageDialog(this, "Please enter an integer larger than 1", "Invalid input!", JOptionPane.ERROR_MESSAGE);
        continue;
      }
      
      break;
      
    }

    if (buttonPanel != null)
      remove(buttonPanel);

    buttonPanel = new JPanel(new GridLayout(height, width));

    buttons = new JToggleButton[height][width];

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
      buttons[r][c] = new JToggleButton();
      buttons[r][c].setBackground(Color.BLACK);
      buttons[r][c].addActionListener(this);
      buttonPanel.add(buttons[r][c]);
      }
    }

    add(buttonPanel, BorderLayout.CENTER);
    revalidate();
  }

  public GameOfLife() {

    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (ClassNotFoundException e) {
      System.out.println("Error setting look and feel");
    } catch (InstantiationException e) {
      System.out.println("Error setting look and feel");
    } catch (IllegalAccessException e) {
      System.out.println("Error setting look and feel");
    } catch (UnsupportedLookAndFeelException e) {
      System.out.println("Error setting look and feel");
    }
    
    patt = Pattern.compile("B([0-8]*)/S([0-8]*)");
    
    parseRules("B3/S23");

    changeSize();
    
    stepper = new javax.swing.Timer(25, this);

    /*buttonPanel = new JPanel(new GridLayout(height, width));

    buttons = new JToggleButton[height][width];

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
      buttons[r][c] = new JToggleButton();
      buttons[r][c].setBackground(Color.BLACK);
      buttons[r][c].addActionListener(this);
      buttonPanel.add(buttons[r][c]);
      }
    }*/

    changeRulesButton = new JButton("Change game rules");
    changeRulesButton.addActionListener(this);

    changeSizeButton = new JButton("Change game size");
    changeSizeButton.addActionListener(this);

    invertButton = new JButton("Invert grid");
    invertButton.addActionListener(this);

    clearButton = new JButton("Clear");
    clearButton.addActionListener(this);

    stepButton = new JButton("Step");
    stepButton.addActionListener(this);

    runButton = new JToggleButton("Run");
    runButton.addActionListener(this);
    
    actionsPanel = new JPanel();
    actionsPanel.add(changeRulesButton);
    actionsPanel.add(changeSizeButton);
    actionsPanel.add(invertButton);
    actionsPanel.add(clearButton);
    actionsPanel.add(stepButton);
    actionsPanel.add(runButton);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    
    setTitle("Conway's Game of Life");
    //add(buttonPanel, BorderLayout.CENTER);
    add(actionsPanel, BorderLayout.SOUTH);
    setSize(width*25, height*25);
    setVisible(true);

  }
  
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == clearButton) {
      clearButtons();
    } else if (e.getSource() == stepButton || e.getSource() == stepper) {
      step();
    } else if (e.getSource() == runButton) {
      if (runButton.isSelected()) {
       stepper.start();
      } else {
        stepper.stop();
      }
    } else if (e.getSource() == changeRulesButton) {
      changeRules();
    } else if (e.getSource() == invertButton) {
      invertGrid();
    } else if (e.getSource() == changeSizeButton) {
      changeSize();
    }
  }
  
  private void clearButtons() {
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        buttons[r][c].setSelected(false);
      }
    }
  }

  private void invertGrid() {
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        buttons[r][c].setSelected(!buttons[r][c].isSelected());
      }
    }
  }

  private int neighbors(int r, int c) {
    int n = 0;
    if (r > 0) {
      if (c > 0 && buttons[r - 1][c - 1].isSelected())
        n++;
      if (buttons[r - 1][c].isSelected())
        n++;
      if (c < width - 1 && buttons[r - 1][c + 1].isSelected())
        n++;
    }

    if (c > 0 && buttons[r][c - 1].isSelected())
      n++;
    if (c < width - 1 && buttons[r][c + 1].isSelected())
      n++;

    if (r < height - 1) {
      if (c > 0 && buttons[r + 1][c - 1].isSelected())
        n++;
      if (buttons[r + 1][c].isSelected())
        n++;
      if (c < width - 1 && buttons[r + 1][c + 1].isSelected())
        n++;
    }

    return n;
  }
  
  private boolean[][] next() {
    boolean[][] nextGrid = new boolean[height][width];
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        int n = neighbors(r, c);
        if (!S.contains(n))
          nextGrid[r][c] = false;
        if (S.contains(n) && buttons[r][c].isSelected())
          nextGrid[r][c] = true;
        if (B.contains(n) && !buttons[r][c].isSelected())
          nextGrid[r][c] = true;
      }
    }
    return nextGrid;
  }

  private void step() {
    boolean[][] n = next();
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        if (n[r][c])
          buttons[r][c].setSelected(true);
        else
          buttons[r][c].setSelected(false);
      }
    }
  }

  private void changeRules() {
    while (true) {
      String s = JOptionPane.showInputDialog(this, "New game rules:", "Rules", JOptionPane.QUESTION_MESSAGE);
      if (s == null) {
        break;
      }
      if (!parseRules(s)) {
        JOptionPane.showMessageDialog(this, "The rules you entered were not valid (Golly/RLE format)", "Invalid input!", JOptionPane.ERROR_MESSAGE);
        continue;
      }
      break;
    }
  }

  public static void main(String[] args) {
    GameOfLife GOL = new GameOfLife();
  }
  
}