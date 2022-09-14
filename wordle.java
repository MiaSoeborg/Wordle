// Wordle by Mia S.
// Date: 30.05.2022

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;

public class Wordle extends JFrame {
	WordlePanel wordleGUI = new WordlePanel();
	// Create JFrame with textfield and button
	JFrame frame;
	WordlePanel[] panelArray = new WordlePanel[6];
	JTextField textField = new JTextField();
	JButton enter = new JButton("ENTER");
	int counter = 0;
	String wordle = pickWord();
	// Custom Clue colors
	Color clueColorGreen = new Color(109, 188, 101);
	Color clueColorYellow = new Color(251,219,82);
	Color clueColorGrey = new Color(89, 89, 89);

	
	// Constructor that defines the GUI
	Wordle() throws IOException {
		frame = new JFrame ("Wordle");
		frame.setSize(270, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new GridLayout(8, 1));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
		// Add WordlePanel to JFrame
		for (int i = 0; i < 6; i++) {
			panelArray[i] = new WordlePanel();
			frame.add(panelArray[i]);
		}
		
		frame.add(textField);
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		textField.requestFocus();
		frame.add(enter);
		frame.revalidate();
		enter.addActionListener(new UponEnter());
	}
	
	
	public static void main(String[] args) throws IOException {
		
		new Wordle();

	} // End of main method
	
	
	class WordlePanel extends JPanel {
		JLabel[] guesses = new JLabel[5];  
		
		// Constructor that sets the grid layout of guesses
		public WordlePanel() {
			this.setLayout(new GridLayout(1, 5));
			Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
			for (int i = 0; i < 5; i++) {
				guesses[i] = new JLabel();
				guesses[i].setHorizontalAlignment(SwingConstants.CENTER);
				guesses[i].setOpaque(true);
				guesses[i].setBorder(border);
				this.add(guesses[i]);
			}
		}
		
		// Method that fills the guesses array with input from user
		public void fillGuesses(String s, int i, Color c) {
			this.guesses[i].setText(s);
			this.guesses[i].setBackground(c);
			this.guesses[i].setForeground(Color.WHITE);
			this.guesses[i].setFont(new Font("Calibri", Font.BOLD, 16));
		}
	} // End of WordlePanel class
	
	
	// UponEnter class that is triggered when the "ENTER" button is clicked and gives the user feedback
	class UponEnter implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			String guess = textField.getText();
			
			
			if ("ENTER".equals(event.getActionCommand())) {
				
				if(onlyLetters(guess) == false) {
					JOptionPane.showMessageDialog(frame, "You can only type in English letters!");
					textField.setText(null);
				} 
				else if (fiveChars(guess) == false) {
					JOptionPane.showMessageDialog(frame, "Your guess must be an English word of five letters!");
				}
				else {
					evaluateGuess(guess.toUpperCase());
					textField.setText(null);
					counter++;
				}

				if (guess.toUpperCase().equals(wordle.toUpperCase())) {
					JOptionPane.showMessageDialog(frame, "Correct! You won!");
					textField.setEnabled(false);
					return;
				}
				
				if (counter > 5) {
					JOptionPane.showMessageDialog(frame, "Game over! You ran out of guesses! \n The correct word was: " + wordle);
					textField.setEnabled(false);
					return;
				}
				
				textField.requestFocus();
			} 
			else {
				return;
			}
		}
	} // End of UponEnter class

	
	// Evaluates the guess from the user, compares it to the wordle-word and uses the fillGuesses method to give color clues to the user
	public void evaluateGuess(String guess) {
		
		String[] wordleArray = wordle.split("");
		String[] guessArray = guess.split("");
		boolean[] used = new boolean[5];
		boolean[] notGrey = new boolean[5];
		
		// Green clues: If the letter matches wordle-word letter in the CORRECT position
		for (int i = 0; i < 5; i++) {
			currentRow().fillGuesses(guessArray[i], i, null);
			
			if (wordleArray[i].equals(guessArray[i])) {
				currentRow().fillGuesses(guessArray[i], i, clueColorGreen);
				used[i] = true;
				notGrey[i] = true;
			}
		}
		
		// Yellow clues: If the letter matches wordle-word letter in the WRONG position
		for (int i = 0; i < 5; i++) {
			if (notGrey[i] == true) {
				continue;
			}
			
			for (int j = 0; j< 5; j++) {
				if (used[j] == false && guessArray[i].equals(wordleArray[j])) {
					currentRow().fillGuesses(guessArray[i], i, clueColorYellow);
					used[j] = true;
					notGrey[i] = true;
					break;
				}
			}
		}
		
		// Grey clues: If letter is NOT not-grey, then paint it grey
		for (int i = 0; i < 5; i++) {
			if (notGrey[i] == false) {
				currentRow().fillGuesses(guessArray[i], i, clueColorGrey);
			}
		}
		return;
		
	} // End of evaluateGuess method
	
	
	// Gets the current panel row based on the counter/guess round
	public WordlePanel currentRow() {
		return this.panelArray[counter];
	}

	
	// Checks if input string only consists of letters
	public static boolean onlyLetters(String s) {
		
		s = s.toUpperCase();
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!(c >= 'A' && c <= 'Z')) {
				return false;
			}
		}
		return true;

	 }
	
	
	// Ensures that guess consists of five characters
	public boolean fiveChars(String s) {
		
		s = this.textField.getText();
		
		if (s.length() != 5) {
			return false;
		}
		else {
			return true;
		}
	}
	
	
	// Chooses Wordle-word randomly from text file
	public static String pickWord() throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader("words.txt"));
		String line = reader.readLine();
		List<String> words = new ArrayList<String>();
		
		while(line != null) {
			String[] wordsLine = line.split(" ");
			for (String word : wordsLine) {
				words.add(word);
			}
			line = reader.readLine();
		}
		
		Random rand = new Random(System.currentTimeMillis());
		String randomWord = words.get(rand.nextInt(words.size()));
		reader.close();
		
		return randomWord.toUpperCase();
	}
	
	
} // End of Wordle JFrame
